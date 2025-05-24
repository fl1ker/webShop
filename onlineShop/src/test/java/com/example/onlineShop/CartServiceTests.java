package com.example.onlineShop;

import com.example.onlineShop.models.*;
import com.example.onlineShop.repositories.*;
import com.example.onlineShop.services.CartService;
import com.example.onlineShop.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTests {

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
        testUser.setEmail("test@test.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setPrice(100);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(1);
        testCartItem.setCart(testCart);
    }

    @Test
    @DisplayName("Powinien dodać nowy produkt do koszyka")
    void shouldAddNewProductToCart() {
        // given
        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(testCart);
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        // when
        cartService.addToCart(principal, testProduct.getId(), 1);

        // then
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(testCart);
    }

    @Test
    @DisplayName("Powinien zwiększyć ilość istniejącego produktu w koszyku")
    void shouldIncreaseQuantityOfExistingProduct() {
        // given
        testCart.addItem(testCartItem);

        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(testCart);
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        // when
        cartService.addToCart(principal, testProduct.getId(), 2);

        // then
        assertEquals(3, testCartItem.getQuantity());
        verify(cartItemRepository).save(testCartItem);
    }

    @Test
    @DisplayName("Powinien usunąć produkt z koszyka")
    void shouldRemoveProductFromCart() {
        // given
        testCart.addItem(testCartItem);

        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(testCart);
        when(cartItemRepository.findById(testCartItem.getId())).thenReturn(Optional.of(testCartItem));

        // when
        cartService.removeFromCart(principal, testCartItem.getId());

        // then
        verify(cartItemRepository).delete(testCartItem);
        verify(cartRepository).save(testCart);
        assertTrue(testCart.getItems().isEmpty());
    }

    @Test
    @DisplayName("Powinien zwrócić koszyk dla użytkownika")
    void shouldGetCartForUser() {
        // given
        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(testCart);

        // when
        Cart result = cartService.getCartByPrincipal(principal);

        // then
        assertEquals(testCart, result);
    }

    @Test
    @DisplayName("Powinien obliczyć całkowitą cenę koszyka")
    void shouldCalculateTotalPrice() {
        // given
        testCart.addItem(testCartItem);
        testCartItem.setQuantity(2);

        // when
        int total = cartService.getTotalPrice(testCart);

        // then
        assertEquals(200, total); // 2 * 100
    }

    @Test
    @DisplayName("Powinien zrealizować zamówienie")
    void shouldCheckoutCart() {
        // given
        testCart.addItem(testCartItem);

        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(testCart);

        // when
        cartService.checkoutCart(principal);

        // then
        verify(emailService).sendPurchaseConfirmation(
                testUser.getEmail(),
                testProduct.getTitle(),
                testCartItem.getQuantity()
        );
        verify(cartRepository).save(testCart);
        verify(userRepository).save(testUser);
        assertTrue(testCart.getItems().isEmpty());
    }

    @Test
    @DisplayName("Nie powinien realizować pustego koszyka")
    void shouldNotCheckoutEmptyCart() {
        // given
        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(testCart);

        // when
        cartService.checkoutCart(principal);

        // then
        verify(emailService, never()).sendPurchaseConfirmation(any(), any(), anyInt());
        verify(userRepository, never()).save(any());
    }
}