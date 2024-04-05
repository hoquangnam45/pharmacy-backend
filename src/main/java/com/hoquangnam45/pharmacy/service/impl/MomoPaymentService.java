package com.hoquangnam45.pharmacy.service.impl;

import com.google.common.base.Charsets;
import com.hoquangnam45.pharmacy.constant.TransactionStatus;
import com.hoquangnam45.pharmacy.entity.FileMetadata;
import com.hoquangnam45.pharmacy.entity.MedicinePreviewAudit;
import com.hoquangnam45.pharmacy.entity.PhoneNumber;
import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.pojo.MomoCaptureWalletRequest;
import com.hoquangnam45.pharmacy.pojo.MomoCaptureWalletRequestDeliveryInfo;
import com.hoquangnam45.pharmacy.pojo.MomoCaptureWalletRequestItem;
import com.hoquangnam45.pharmacy.pojo.MomoCaptureWalletRequestUserInfo;
import com.hoquangnam45.pharmacy.pojo.MomoCaptureWalletResponse;
import com.hoquangnam45.pharmacy.service.IPaymentService;
import com.hoquangnam45.pharmacy.service.IS3Service;
import com.hoquangnam45.pharmacy.util.Functions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.List;

/**
 * References: <a href="https://developers.momo.vn/v3/vi/docs/payment/api/wallet/onetime">...</a>
 * Momo Payment API support 2 type of payment method (one-time, and link the wallet). For simplicity this service
 * will only support the one-time payment method
 */
// When adding momo payment method, we should save some kind of tokens that will be used with momo api
// after that tokens should be used to submit to
@Service
public class MomoPaymentService implements IPaymentService<MomoCaptureWalletResponse> {
    private final String momoApiUrl;
    private final String momoToken;
    private final RestClient restClient;
    private final IS3Service s3Service;
    private final String partnerCode;
    private final String ipnUrl;
    private final String appUrl;
    private final Mac hasher;
    private final String storeName;

    public MomoPaymentService(
            @Value("pharma.payment.momo.apiUrl") String momoApiUrl,
            @Value("pharma.payment.momo.apiToken") String momoToken,
            RestClient restClient,
            IS3Service s3Service,
            String partnerCode,
            String ipnUrl,
            String secretKey,
            String appUrl,
            @Value("pharma.store.name") String storeName) throws NoSuchAlgorithmException, InvalidKeyException {
        this.momoApiUrl = momoApiUrl;
        this.momoToken = momoToken;
        this.restClient = restClient;
        this.s3Service = s3Service;
        this.partnerCode = partnerCode;
        this.ipnUrl = ipnUrl;
        this.appUrl = appUrl;
        this.storeName = storeName;
        this.hasher = Mac.getInstance("HmacSHA256");
        hasher.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
    }


    @Override
    public MomoCaptureWalletResponse startPayment(TransactionInfo transactionInfo) {
        MomoCaptureWalletRequestDeliveryInfo deliveryInfo = MomoCaptureWalletRequestDeliveryInfo.builder()
                .deliveryAddress(transactionInfo.getOrder().getDeliveryInfo().getAddress())
                .deliveryFee(transactionInfo.getDeliveryFee().longValue())
                .build();
        List<MomoCaptureWalletRequestItem> items = transactionInfo.getOrder().getOrderItems().stream()
                .map(item -> MomoCaptureWalletRequestItem.builder()
                        .id(item.getListing().getId())
                        .name(item.getListing().getPackaging().getMedicine().getName())
                        .category(item.getListing().getPackaging().getPackagingUnit().name())
                        .price(item.getListing().getPrice().longValue())
                        .description(item.getListing().getPackaging().getMedicine().getShortDescription())
                        .imageUrl(item.getListing().getPackaging().getMedicine().getPreviews().stream()
                                .filter(MedicinePreviewAudit::isMainPreview)
                                .map(MedicinePreviewAudit::getFileMetadata)
                                .map(FileMetadata::getPath)
                                .map(Functions.suppressException(s3Service::getDownloadPath))
                                .findFirst()
                                .orElse(null))
                        .manufacturer(item.getListing().getPackaging().getMedicine().getProducer().getName())
                        .taxAmount(0L)
                        .totalPrice(item.getTotalPrice().longValue())
                        .quantity(item.getQuantity())
                        .build())
                .toList();
        User dbUser = transactionInfo.getOrder().getUser();
        MomoCaptureWalletRequestUserInfo userInfo = MomoCaptureWalletRequestUserInfo.builder()
                .email(dbUser.getEmail())
                .phoneNumber(formatPhoneNumber(dbUser.getPhoneNumber()))
                .name(dbUser.getUsername())
                .build();
        MomoCaptureWalletRequest.MomoCaptureWalletRequestBuilder requestBuilder = MomoCaptureWalletRequest.builder()
                .amount(transactionInfo.getAmount().longValue())
                .requestId(transactionInfo.getId().toString())
                .orderId(transactionInfo.getOrder().getId())
                .deliveryInfo(deliveryInfo)
                .items(items)
                .partnerCode(partnerCode)
                .extraData("")
                .ipnUrl(ipnUrl)
                .orderGroupId(null) // TODO: Fill this later
                .orderInfo("Payment order with momo API")
                .redirectUrl(appUrl)
                .referenceId(null)
                .storeName(storeName)
                .subPartnerCode(null) // TODO: Fill this later
                .userInfo(userInfo);
        MomoCaptureWalletRequest request = requestBuilder.signature(generateSignature(requestBuilder))
                .build();
        MomoCaptureWalletResponse response = restClient.method(HttpMethod.POST)
                .uri(getMomoApiUrl("/v2/gateway/api/create"))
                .body(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(Charsets.UTF_8)
                .retrieve()
                .body(MomoCaptureWalletResponse.class);
        transactionInfo.setStatus(TransactionStatus.PENDING);
        return response;
    }



    @Override
    public void cancelPayment(TransactionInfo transactionInfo) {

    }

    @Override
    public void transactPayment(TransactionInfo transactionInfo) {

    }

    public String getMomoApiUrl(String path) {
        return momoApiUrl + "/" + path;
    }

    private String generateSignature(MomoCaptureWalletRequest.MomoCaptureWalletRequestBuilder builder) {
        MomoCaptureWalletRequest unsignedRequest = builder.build();
        return Base64.getEncoder().encodeToString(hasher.doFinal(
                MessageFormat.format(
                "accessKey={0}&amount={1}&extraData={2}" +
                        "&ipnUrl={3}&orderId={4}&orderInfo={5}" +
                        "&partnerCode={6}&redirectUrl={7}" +
                        "&requestId={8}&requestType={9}",
                momoToken,
                unsignedRequest.getAmount().toString(),
                unsignedRequest.getExtraData(),
                unsignedRequest.getIpnUrl(),
                unsignedRequest.getOrderId(),
                unsignedRequest.getOrderInfo(),
                unsignedRequest.getPartnerCode(),
                unsignedRequest.getRedirectUrl(),
                unsignedRequest.getRequestId(),
                unsignedRequest.getRequestType()).getBytes()));
    }

    private String formatPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return MessageFormat.format("+{0} {1}", phoneNumber.getCountryCode(), phoneNumber.getPhoneNumber());
    }
}
