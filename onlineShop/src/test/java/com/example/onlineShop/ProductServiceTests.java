package com.example.onlineShop;

import com.example.onlineShop.models.Image;
import com.example.onlineShop.models.Product;
import com.example.onlineShop.models.User;
import com.example.onlineShop.repositories.ImageRepository;
import com.example.onlineShop.repositories.ProductRepository;
import com.example.onlineShop.repositories.UserRepository;
import com.example.onlineShop.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private Principal principal;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private User testUser;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setUser(testUser);

        testFile = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    @DisplayName("Powinien zwrócić listę produktów po tytule")
    void shouldListProductsByTitle() {
        // given
        String title = "Test";
        List<Product> expectedProducts = List.of(testProduct);
        when(productRepository.findByTitleContainingAndActiveTrue(title))
                .thenReturn(expectedProducts);

        // when
        List<Product> result = productService.listProducts(title);

        // then
        assertEquals(expectedProducts, result);
        verify(productRepository).findByTitleContainingAndActiveTrue(title);
    }

    @Test
    @DisplayName("Powinien zwrócić wszystkie aktywne produkty gdy tytuł jest pusty")
    void shouldListAllActiveProductsWhenTitleIsEmpty() {
        // given
        List<Product> expectedProducts = List.of(testProduct);
        when(productRepository.findByActiveTrue()).thenReturn(expectedProducts);

        // when
        List<Product> result = productService.listProducts("");

        // then
        assertEquals(expectedProducts, result);
        verify(productRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Powinien zapisać produkt z obrazami")
    void shouldSaveProductWithImages() throws IOException {
        // given
        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // when
        productService.saveProduct(principal, testProduct, testFile, testFile, testFile);

        // then
        verify(productRepository, times(2)).save(any(Product.class));
        assertEquals(3, testProduct.getImages().size());
    }

    @Test
    @DisplayName("Powinien usunąć produkt")
    void shouldDeleteProduct() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // when
        productService.deleteProduct(testUser, 1L);

        // then
        verify(productRepository).save(testProduct);
        assertFalse(testProduct.isActive());
    }

    @Test
    @DisplayName("Nie powinien usunąć produktu innego użytkownika")
    void shouldNotDeleteProductOfOtherUser() {
        // given
        User otherUser = new User();
        otherUser.setId(2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // when
        productService.deleteProduct(otherUser, 1L);

        // then
        verify(productRepository, never()).save(any(Product.class));
        assertTrue(testProduct.isActive());
    }

    @Test
    @DisplayName("Powinien zaktualizować produkt")
    void shouldUpdateProduct() throws IOException {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // when
        productService.updateProduct(1L, "New Title", "New Description", 100,
                testFile, testFile, testFile, principal);

        // then
        assertEquals("New Title", testProduct.getTitle());
        assertEquals("New Description", testProduct.getDescription());
        assertEquals(100, testProduct.getPrice());
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    @DisplayName("Powinien zwrócić użytkownika na podstawie Principal")
    void shouldGetUserByPrincipal() {
        // given
        when(principal.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        // when
        User result = productService.getUserByPrincipal(principal);

        // then
        assertEquals(testUser, result);
    }

    @Test
    @DisplayName("Powinien zwrócić nowego użytkownika gdy Principal jest null")
    void shouldReturnNewUserWhenPrincipalIsNull() {
        // when
        User result = productService.getUserByPrincipal(null);

        // then
        assertNotNull(result);
        assertTrue(result instanceof User);
    }
}