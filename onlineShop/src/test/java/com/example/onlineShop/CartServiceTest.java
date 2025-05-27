package com.example.onlineShop;

import com.example.onlineShop.models.*;
import com.example.onlineShop.repositories.*;
import com.example.onlineShop.services.CartService;
import com.example.onlineShop.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private Principal principal;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setOrders(new ArrayList<>());

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setPrice(100);
        testProduct.setActive(true);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);
        testCartItem.setCart(testCart);
    }

    @Test
    void addToCart_WhenUserNotFound_ShouldLogErrorAndReturn() {
        // Given
        when(principal.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // When
        cartService.addToCart(principal, 1L, 1);

        // Then
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addToCart_WhenCartDoesNotExist_ShouldCreateNewCart() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        cartService.addToCart(principal, 1L, 1);

        // Then
        verify(cartRepository, times(2)).save(any(Cart.class)); // Once for creation, once after adding item
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addToCart_WhenProductNotFound_ShouldLogErrorAndReturn() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        cartService.addToCart(principal, 999L, 1);

        // Then
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartRepository, never()).save(testCart);
    }

    @Test
    void addToCart_WhenProductIsInactive_ShouldLogErrorAndReturn() {
        // Given
        testProduct.setActive(false);
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        cartService.addToCart(principal, 1L, 1);

        // Then
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartRepository, never()).save(testCart);
    }

    @Test
    void addToCart_WhenItemAlreadyExists_ShouldUpdateQuantity() {
        // Given
        testCart.getItems().add(testCartItem);
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        cartService.addToCart(principal, 1L, 3);

        // Then
        assertEquals(5, testCartItem.getQuantity()); // 2 + 3
        verify(cartItemRepository).save(testCartItem);
        verify(cartRepository).save(testCart);
    }

    @Test
    void addToCart_WhenItemDoesNotExist_ShouldCreateNewItem() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        cartService.addToCart(principal, 1L, 2);

        // Then
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(testCart);
    }

    @Test
    void removeFromCart_WhenUserNotFound_ShouldLogErrorAndReturn() {
        // Given
        when(principal.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // When
        cartService.removeFromCart(principal, 1L);

        // Then
        verify(cartItemRepository, never()).delete(any(CartItem.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void removeFromCart_WhenCartNotFound_ShouldLogErrorAndReturn() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(null);

        // When
        cartService.removeFromCart(principal, 1L);

        // Then
        verify(cartItemRepository, never()).delete(any(CartItem.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void removeFromCart_WhenItemExists_ShouldRemoveItem() {
        // Given
        testCart.getItems().add(testCartItem);
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);
        when(cartItemRepository.findById(1L)).thenReturn(testCartItem);

        // When
        cartService.removeFromCart(principal, 1L);

        // Then
        verify(cartItemRepository).delete(testCartItem);
        verify(cartRepository).save(testCart);
    }

    @Test
    void removeFromCart_WhenItemNotFound_ShouldLogErrorAndReturn() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);
        when(cartItemRepository.findById(999L)).thenReturn(null);

        // When
        cartService.removeFromCart(principal, 999L);

        // Then
        verify(cartItemRepository, never()).delete(any(CartItem.class));
        verify(cartRepository, never()).save(testCart);
    }

    @Test
    void removeFromCart_WhenItemBelongsToAnotherCart_ShouldLogErrorAndReturn() {
        // Given
        Cart anotherCart = new Cart();
        anotherCart.setId(2L);
        testCartItem.setCart(anotherCart);

        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);
        when(cartItemRepository.findById(1L)).thenReturn(testCartItem);

        // When
        cartService.removeFromCart(principal, 1L);

        // Then
        verify(cartItemRepository, never()).delete(testCartItem);
        verify(cartRepository, never()).save(testCart);
    }

    @Test
    void getCartByPrincipal_WhenUserNotFound_ShouldReturnNull() {
        // Given
        when(principal.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // When
        Cart result = cartService.getCartByPrincipal(principal);

        // Then
        assertNull(result);
    }

    @Test
    void getCartByPrincipal_WhenUserExists_ShouldReturnCart() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);

        // When
        Cart result = cartService.getCartByPrincipal(principal);

        // Then
        assertEquals(testCart, result);
        verify(cartRepository).findByUserId(1L);
    }

    @Test
    void getTotalPrice_WhenCartIsNull_ShouldReturnZero() {
        // When
        int result = cartService.getTotalPrice(null);

        // Then
        assertEquals(0, result);
    }

    @Test
    void getTotalPrice_WhenCartItemsIsNull_ShouldReturnZero() {
        // Given
        testCart.setItems(null);

        // When
        int result = cartService.getTotalPrice(testCart);

        // Then
        assertEquals(0, result);
    }

    @Test
    void getTotalPrice_WhenCartHasItems_ShouldCalculateTotal() {
        // Given
        CartItem item1 = new CartItem();
        item1.setProduct(testProduct); // price = 100
        item1.setQuantity(2);

        Product product2 = new Product();
        product2.setPrice(50);
        CartItem item2 = new CartItem();
        item2.setProduct(product2);
        item2.setQuantity(3);

        testCart.getItems().addAll(Arrays.asList(item1, item2));

        // When
        int result = cartService.getTotalPrice(testCart);

        // Then
        assertEquals(350, result); // (100 * 2) + (50 * 3) = 200 + 150 = 350
    }

    @Test
    void getTotalPrice_WhenCartIsEmpty_ShouldReturnZero() {
        // When
        int result = cartService.getTotalPrice(testCart);

        // Then
        assertEquals(0, result);
    }

    @Test
    void checkoutCart_WhenCartIsNull_ShouldReturnEarly() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(null);

        // When
        cartService.checkoutCart(principal);

        // Then
        verify(emailService, never()).sendPurchaseConfirmation(anyString(), anyString(), anyInt());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void checkoutCart_WhenCartIsEmpty_ShouldReturnEarly() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);

        // When
        cartService.checkoutCart(principal);

        // Then
        verify(emailService, never()).sendPurchaseConfirmation(anyString(), anyString(), anyInt());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void checkoutCart_WhenCartHasItems_ShouldCreateOrdersAndSendEmails() {
        // Given
        testCart.getItems().add(testCartItem);
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);

        // When
        cartService.checkoutCart(principal);

        // Then
        assertEquals(1, testUser.getOrders().size());
        Order createdOrder = testUser.getOrders().get(0);
        assertEquals(testUser, createdOrder.getUser());
        assertEquals(testProduct, createdOrder.getProduct());
        assertEquals(2, createdOrder.getQuantity());
        assertNotNull(createdOrder.getPurchaseDate());

        verify(emailService).sendPurchaseConfirmation("test@example.com", "Test Product", 2);
        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepository).save(testCart);
        verify(userRepository).save(testUser);
    }

    @Test
    void checkoutCart_WithMultipleItems_ShouldCreateMultipleOrders() {
        // Given
        Product product2 = new Product();
        product2.setId(2L);
        product2.setTitle("Second Product");
        product2.setPrice(50);

        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setProduct(product2);
        item2.setQuantity(1);
        item2.setCart(testCart);

        testCart.getItems().addAll(Arrays.asList(testCartItem, item2));

        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);

        // When
        cartService.checkoutCart(principal);

        // Then
        assertEquals(2, testUser.getOrders().size());
        verify(emailService).sendPurchaseConfirmation("test@example.com", "Test Product", 2);
        verify(emailService).sendPurchaseConfirmation("test@example.com", "Second Product", 1);
        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepository).save(testCart);
        verify(userRepository).save(testUser);
    }

    @Test
    void checkoutCart_ShouldSetCorrectPurchaseDate() {
        // Given
        testCart.getItems().add(testCartItem);
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);

        LocalDateTime beforeCheckout = LocalDateTime.now();

        // When
        cartService.checkoutCart(principal);

        // Then
        LocalDateTime afterCheckout = LocalDateTime.now();
        Order createdOrder = testUser.getOrders().get(0);

        assertTrue(createdOrder.getPurchaseDate().isAfter(beforeCheckout.minusSeconds(1)));
        assertTrue(createdOrder.getPurchaseDate().isBefore(afterCheckout.plusSeconds(1)));
    }

    @Test
    void checkoutCart_WhenEmailServiceThrowsException_ShouldContinueProcessing() {
        // Given
        testCart.getItems().add(testCartItem);
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(testCart);
        doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendPurchaseConfirmation(anyString(), anyString(), anyInt());

        // When & Then
        assertDoesNotThrow(() -> cartService.checkoutCart(principal));
        assertEquals(1, testUser.getOrders().size());
        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepository).save(testCart);
        verify(userRepository).save(testUser);
    }
}