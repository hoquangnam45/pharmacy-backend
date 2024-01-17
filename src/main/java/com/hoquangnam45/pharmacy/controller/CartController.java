package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.component.PriceCalculator;
import com.hoquangnam45.pharmacy.entity.Medicine;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.entity.MedicinePreview;
import com.hoquangnam45.pharmacy.pojo.CartItem;
import com.hoquangnam45.pharmacy.pojo.CartItemListing;
import com.hoquangnam45.pharmacy.pojo.PutCartItemRequest;
import com.hoquangnam45.pharmacy.service.CartService;
import com.hoquangnam45.pharmacy.service.IS3Service;
import com.hoquangnam45.pharmacy.util.Functions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cart")
public class CartController {
    private final CartService cartService;
    private final PriceCalculator priceCalculator;
    private final IS3Service s3Service;

    public CartController(CartService cartService, PriceCalculator priceCalculator, IS3Service s3Service) {
        this.cartService = cartService;
        this.priceCalculator = priceCalculator;
        this.s3Service = s3Service;
    }

    @PutMapping
    public ResponseEntity<CartItem> addItemToCart(@RequestBody PutCartItemRequest request) {
        return Optional.ofNullable(cartService.addItemToCart(request))
                .map(this::mapCartItemEntityToPOJO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalStateException("Not possible to enter here"));
    }

    @DeleteMapping
    public ResponseEntity<CartItem> removeItemFromCart(@RequestBody PutCartItemRequest request) {
        return Optional.ofNullable(cartService.removeItemFromCart(request))
                .map(this::mapCartItemEntityToPOJO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("{userId}")
    public ResponseEntity<List<CartItem>> getAllCartItem(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId).stream()
                .map(this::mapCartItemEntityToPOJO)
                .collect(Collectors.toList()));
    }

    private CartItem mapCartItemEntityToPOJO(com.hoquangnam45.pharmacy.entity.CartItem cartItem) {
        MedicineListing listing = cartItem.getListing();
        MedicinePackaging packaging = listing.getPackaging();
        Medicine medicine = packaging.getMedicine();
        String mainPreviewUrl = medicine.getPreviews().stream()
                .filter(MedicinePreview::isMainPreview)
                .findFirst()
                .map(preview -> preview.getFileMetadata().getPath())
                .map(Functions.suppressException(s3Service::getDownloadPath))
                .orElse(null);
        return CartItem.builder()
                .id(cartItem.getId())
                .quantity(cartItem.getQuantity())
                .price(priceCalculator.calculateCartItemPrice(cartItem))
                .listing(CartItemListing.builder()
                        .description(medicine.getDescription())
                        .listingPreview(mainPreviewUrl)
                        .packagingId(packaging.getId())
                        .medicineId(medicine.getId())
                        .price(listing.getPrice())
                        .packagingUnit(packaging.getPackagingUnit())
                        .id(listing.getId())
                        .build())
                .build();
    }
}
