package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.entity.CartItem;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.PutCartItemRequest;
import com.hoquangnam45.pharmacy.repo.CartItemRepo;
import com.hoquangnam45.pharmacy.repo.MedicineListingRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {
    private final CartItemRepo cartItemRepo;
    private final MedicineListingRepo medicineListingRepo;

    public CartService(CartItemRepo cartItemRepo, MedicineListingRepo medicineListingRepo) {
        this.cartItemRepo = cartItemRepo;
        this.medicineListingRepo = medicineListingRepo;
    }

    public CartItem addItemToCart(PutCartItemRequest request) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        if (request.getQuantity() <= 0) {
            throw new ApiError(400, "Added quantity must be positive");
        }
        CartItem cartItem = cartItemRepo.findByListingIdAndUser_Id(request.getListingId(), request.getUserId());
        int newQuantity = Optional.ofNullable(cartItem)
                .map(CartItem::getQuantity)
                .map(quantity -> quantity + request.getQuantity())
                .orElseGet(request::getQuantity);

        // New quantity should be positive
        MedicineListing listing = medicineListingRepo.findListingFullyFetchedById(request.getListingId());
        if (cartItem == null) {
            cartItem = addItemToCart(request, userId);
        } else {
            cartItem.setQuantity(newQuantity);
        }
        cartItem.setListing(listing);
        return cartItem;
    }

    public CartItem removeItemFromCart(PutCartItemRequest request) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        if (request.getQuantity() <= 0) {
            throw new ApiError(400, "Removed quantity must be positive");
        }
        CartItem cartItem = cartItemRepo.findByListingIdAndUser_Id(request.getListingId(), request.getUserId());
        if (cartItem == null) {
            return null;
        }
        int newQuantity = cartItem.getQuantity() - request.getQuantity();
        if (newQuantity <= 0) {
            // delete cart item from cart
            cartItemRepo.delete(cartItem);
            return null;
        }
        MedicineListing listing = medicineListingRepo.findListingFullyFetchedById(request.getListingId());
        cartItem.setQuantity(newQuantity);
        cartItem.setListing(listing);
        return cartItem;
    }


    private CartItem addItemToCart(PutCartItemRequest request, UUID userId) {
        return cartItemRepo.save(CartItem.builder()
                .userId(userId)
                .listingId(request.getListingId())
                .quantity(request.getQuantity())
                .build());
    }

    public List<CartItem> getCartItems(UUID userCartId) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!userId.equals(userCartId)) {
            // Allow only owner to get their own cart
            throw new ApiError(401, "No permission");
        }
        return Optional.ofNullable(cartItemRepo.findCartItemUserIdFetched(userId))
                .orElseThrow(() -> new ApiError(400, "Invalid cart"));
    }
}
