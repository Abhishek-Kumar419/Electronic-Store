package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.AddItemToCartRequest;
import com.lcwd.electronic.store.dtos.CartDto;

public interface CartService {

    //add items to cart :
    //case 1: cart for user is not available: we will create the cart and add the item
    //case 2: cart is already present: we will just add the item to existing cart
    CartDto addItemToCart(String userId, AddItemToCartRequest request);


    //remove item from cart:
    void removeItemFromCart(String userId, int cartItem);

    //clear the cart:
    void clearCart(String userId);

    CartDto getCartByUser(String userId);



}
