package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.AddItemToCartRequest;
import com.lcwd.electronic.store.dtos.CartDto;
import com.lcwd.electronic.store.entities.Cart;
import com.lcwd.electronic.store.entities.CartItem;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.BadApiRequestException;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.CartItemRepository;
import com.lcwd.electronic.store.repositories.CartRepository;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.CartService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest request) {

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if (quantity <= 0 || productId.isEmpty()){
            throw new BadApiRequestException("Quantity or ProductId is invalid");
        }


        //fetch the product
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found in db"));
        //fetch the user from db:
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found in db"));

        Cart cart = null;
        try{
            cart = cartRepository.findByUser(user).get();
        } catch (NoSuchElementException e){
            cart = new Cart();
            cart.setCartId(java.util.UUID.randomUUID().toString());
            cart.setCreatedAt(new Date());
        }

        //perform cart operations
        //if cart items already present then update quantity
        AtomicReference<Boolean> updated = new AtomicReference<>(false);
        List<CartItem> items = cart.getItems();
        items = items.stream().map(item -> {

            if (item.getProduct().getProductId().equals(productId)){
                //item is already present in cart
                item.setQuantity(quantity);
                item.setTotalPrice(quantity * product.getDiscountedPrice());
                updated.set(true);
            }

            return item;
        }).collect(Collectors.toList());
//        cart.setItems(updatedItems);

        //create items
        if (!updated.get()){
            CartItem  cartItem = CartItem.builder()
                    .quantity(quantity)
                    .product(product)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .build();
            cart.getItems().add(cartItem);
        }

        cart.setUser(user);
        Cart updatedCart = cartRepository.save(cart);

        return modelMapper.map(updatedCart, CartDto.class);

    }

    @Override
    public void removeItemFromCart(String userId, int cartItem) {
        CartItem itemToRemove = cartItemRepository.findById(cartItem).orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        cartItemRepository.delete(itemToRemove);
    }

    @Override
    public void clearCart(String userId) {
        //fetch the user from db:
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found in db"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user is not found"));
        cart.getItems().clear();
        cartRepository.save(cart);

    }

    @Override
    public CartDto getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found in db"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user is not found"));

        return modelMapper.map(cart, CartDto.class);
    }
}
