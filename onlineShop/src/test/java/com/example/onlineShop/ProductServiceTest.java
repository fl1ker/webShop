package com.example.onlineShop;

import com.example.onlineShop.models.Image;
import com.example.onlineShop.models.Product;
import com.example.onlineShop.models.User;
import com.example.onlineShop.repositories.ImageRepository;
import com.example.onlineShop.repositories.ProductRepository;
import com.example.onlineShop.repositories.UserRepository;
import com.example.onlineShop.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private Principal principal;

    @Mock
    private MultipartFile file1;

    @Mock
    private MultipartFile file2;

    @Mock
    private MultipartFile file3;

    @InjectMocks
    private ProductService productService;

    private User testUser;
    private Product testProduct;
    private Image testImage;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(100);
        testProduct.setUser(testUser);
        testProduct.setActive(true);
        testProduct.setImages(new ArrayList<>());

        testImage = new Image();
        testImage.setId(1L);
        testImage.setName("test.jpg");
        testImage.setOriginalFileName("test.jpg");
        testImage.setContentType("image/jpeg");
        testImage.setSize(1024L);
        testImage.setBytes(new byte[]{1, 2, 3});
    }

    @Test
    void listProducts_WithTitle_ShouldReturnFilteredProducts() {
        // Given
        String title = "test";
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByTitleContainingAndActiveTrue(title)).thenReturn(expectedProducts);

        // When
        List<Product> result = productService.listProducts(title);

        // Then
        assertEquals(expectedProducts, result);
        verify(productRepository).findByTitleContainingAndActiveTrue(title);
        verify(productRepository, never()).findByActiveTrue();
    }

    @Test
    void listProducts_WithNullTitle_ShouldReturnAllActiveProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByActiveTrue()).thenReturn(expectedProducts);

        // When
        List<Product> result = productService.listProducts(null);

        // Then
        assertEquals(expectedProducts, result);
        verify(productRepository).findByActiveTrue();
        verify(productRepository, never()).findByTitleContainingAndActiveTrue(anyString());
    }

    @Test
    void listProducts_WithEmptyTitle_ShouldReturnAllActiveProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByActiveTrue()).thenReturn(expectedProducts);

        // When
        List<Product> result = productService.listProducts("");

        // Then
        assertEquals(expectedProducts, result);
        verify(productRepository).findByActiveTrue();
    }

    @Test
    void saveProduct_WithOneFile_ShouldSaveProductWithPreviewImage() throws IOException {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(file1.getSize()).thenReturn(1024L);
        when(file1.getName()).thenReturn("file1");
        when(file1.getOriginalFilename()).thenReturn("test1.jpg");
        when(file1.getContentType()).thenReturn("image/jpeg");
        when(file1.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(file2.getSize()).thenReturn(0L);
        when(file3.getSize()).thenReturn(0L);

        Product savedProduct = new Product();
        savedProduct.setImages(Arrays.asList(testImage));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        productService.saveProduct(principal, testProduct, file1, file2, file3);

        // Then
        assertEquals(testUser, testProduct.getUser());
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void saveProduct_WithMultipleFiles_ShouldSaveAllImages() throws IOException {
        // Given
        setupMultipleFiles();
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        Product savedProduct = new Product();
        savedProduct.setImages(Arrays.asList(testImage));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        productService.saveProduct(principal, testProduct, file1, file2, file3);

        // Then
        assertEquals(testUser, testProduct.getUser());
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void saveProduct_WithIOException_ShouldThrowException() throws IOException {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(file1.getSize()).thenReturn(1024L);
        when(file1.getBytes()).thenThrow(new IOException("File read error"));

        // When & Then
        assertThrows(IOException.class, () ->
                productService.saveProduct(principal, testProduct, file1, file2, file3));
    }

    @Test
    void getUserByPrincipal_WithNullPrincipal_ShouldReturnNewUser() {
        // When
        User result = productService.getUserByPrincipal(null);

        // Then
        assertNotNull(result);
        assertNull(result.getEmail());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void getUserByPrincipal_WithValidPrincipal_ShouldReturnUser() {
        // Given
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // When
        User result = productService.getUserByPrincipal(principal);

        // Then
        assertEquals(testUser, result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void deleteProduct_WhenUserOwnsProduct_ShouldDeactivateProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        productService.deleteProduct(testUser, 1L);

        // Then
        assertFalse(testProduct.isActive());
        verify(productRepository).save(testProduct);
    }

    @Test
    void deleteProduct_WhenUserDoesNotOwnProduct_ShouldNotDelete() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        productService.deleteProduct(anotherUser, 1L);

        // Then
        assertTrue(testProduct.isActive()); // Should remain active
        verify(productRepository, never()).save(testProduct);
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldNotThrowException() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertDoesNotThrow(() -> productService.deleteProduct(testUser, 999L));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.getProductById(1L);

        // Then
        assertEquals(testProduct, result);
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldReturnNull() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Product result = productService.getProductById(999L);

        // Then
        assertNull(result);
        verify(productRepository).findById(999L);
    }

    @Test
    void updateProduct_WhenProductNotFound_ShouldReturnEarly() throws IOException {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        productService.updateProduct(999L, "New Title", "New Description", 200,
                file1, file2, file3, principal);

        // Then
        verify(productRepository, never()).save(any(Product.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void updateProduct_WhenUserDoesNotOwnProduct_ShouldReturnEarly() throws IOException {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(principal.getName()).thenReturn("another@example.com");
        when(userRepository.findByEmail("another@example.com")).thenReturn(anotherUser);

        // When
        productService.updateProduct(1L, "New Title", "New Description", 200,
                file1, file2, file3, principal);

        // Then
        verify(productRepository, never()).save(any(Product.class));
        assertNotEquals("New Title", testProduct.getTitle());
    }

    @Test
    void updateProduct_WithValidData_ShouldUpdateProduct() throws IOException {
        // Given
        testProduct.getImages().add(testImage);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(file1.getSize()).thenReturn(0L);
        when(file2.getSize()).thenReturn(0L);
        when(file3.getSize()).thenReturn(0L);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.updateProduct(1L, "Updated Title", "Updated Description", 250,
                file1, file2, file3, principal);

        // Then
        assertEquals("Updated Title", testProduct.getTitle());
        assertEquals("Updated Description", testProduct.getDescription());
        assertEquals(250, testProduct.getPrice());
        verify(productRepository, atLeastOnce()).save(testProduct);
    }

    @Test
    void updateProduct_WithNewFile_ShouldReplaceExistingImage() throws IOException {
        // Given
        testProduct.getImages().add(testImage);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        when(file1.getSize()).thenReturn(1024L);
        when(file1.getName()).thenReturn("newFile");
        when(file1.getOriginalFilename()).thenReturn("new.jpg");
        when(file1.getContentType()).thenReturn("image/jpeg");
        when(file1.getBytes()).thenReturn(new byte[]{4, 5, 6});
        when(file2.getSize()).thenReturn(0L);
        when(file3.getSize()).thenReturn(0L);

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.updateProduct(1L, "Updated Title", "Updated Description", 250,
                file1, file2, file3, principal);

        // Then
        verify(imageRepository).delete(testImage);
        verify(productRepository, atLeastOnce()).save(testProduct);
    }

    @Test
    void updateProduct_WithoutPreviewImageId_ShouldSetPreviewImageId() throws IOException {
        // Given
        testImage.setId(100L);
        testProduct.getImages().add(testImage);
        testProduct.setPreviewImageId(null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(file1.getSize()).thenReturn(0L);
        when(file2.getSize()).thenReturn(0L);
        when(file3.getSize()).thenReturn(0L);

        Product savedProduct = new Product();
        savedProduct.setImages(Arrays.asList(testImage));
        savedProduct.setPreviewImageId(null);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        productService.updateProduct(1L, "Updated Title", "Updated Description", 250,
                file1, file2, file3, principal);

        // Then
        verify(productRepository, times(2)).save(any(Product.class));
    }

    private void setupMultipleFiles() throws IOException {
        when(file1.getSize()).thenReturn(1024L);
        when(file1.getName()).thenReturn("file1");
        when(file1.getOriginalFilename()).thenReturn("test1.jpg");
        when(file1.getContentType()).thenReturn("image/jpeg");
        when(file1.getBytes()).thenReturn(new byte[]{1, 2, 3});

        when(file2.getSize()).thenReturn(2048L);
        when(file2.getName()).thenReturn("file2");
        when(file2.getOriginalFilename()).thenReturn("test2.jpg");
        when(file2.getContentType()).thenReturn("image/jpeg");
        when(file2.getBytes()).thenReturn(new byte[]{4, 5, 6});

        when(file3.getSize()).thenReturn(3072L);
        when(file3.getName()).thenReturn("file3");
        when(file3.getOriginalFilename()).thenReturn("test3.jpg");
        when(file3.getContentType()).thenReturn("image/jpeg");
        when(file3.getBytes()).thenReturn(new byte[]{7, 8, 9});
    }
    @Test
    void updateProduct_WithNewFile2_ShouldReplaceSecondImage() throws IOException {
        // Given
        Image image2 = new Image();
        image2.setId(2L);
        image2.setName("test2.jpg");
        image2.setOriginalFileName("test2.jpg");
        image2.setContentType("image/jpeg");
        image2.setSize(2048L);
        image2.setBytes(new byte[]{4, 5, 6});

        testProduct.getImages().add(testImage); // First image
        testProduct.getImages().add(image2);    // Second image
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        when(file1.getSize()).thenReturn(0L); // No new file1
        when(file2.getSize()).thenReturn(2048L);
        when(file2.getName()).thenReturn("newFile2");
        when(file2.getOriginalFilename()).thenReturn("new2.jpg");
        when(file2.getContentType()).thenReturn("image/jpeg");
        when(file2.getBytes()).thenReturn(new byte[]{7, 8, 9});
        when(file3.getSize()).thenReturn(0L); // No new file3

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.updateProduct(1L, "Updated Title", "Updated Description", 250,
                file1, file2, file3, principal);

        // Then
        verify(imageRepository).delete(image2); // Verify second image is deleted
        verify(productRepository, atLeastOnce()).save(testProduct);
        assertEquals(2, testProduct.getImages().size()); // Should still have 2 images
        assertEquals("new2.jpg", testProduct.getImages().get(1).getOriginalFileName()); // New file2 added
    }
    @Test
    void updateProduct_WithNewFile3_ShouldReplaceThirdImage() throws IOException {
        // Given
        Image image2 = new Image();
        image2.setId(2L);
        image2.setName("test2.jpg");
        image2.setOriginalFileName("test2.jpg");
        image2.setContentType("image/jpeg");
        image2.setSize(2048L);
        image2.setBytes(new byte[]{4, 5, 6});

        Image image3 = new Image();
        image3.setId(3L);
        image3.setName("test3.jpg");
        image3.setOriginalFileName("test3.jpg");
        image3.setContentType("image/jpeg");
        image3.setSize(3072L);
        image3.setBytes(new byte[]{7, 8, 9});

        testProduct.getImages().add(testImage); // First image
        testProduct.getImages().add(image2);    // Second image
        testProduct.getImages().add(image3);    // Third image
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        when(file1.getSize()).thenReturn(0L); // No new file1
        when(file2.getSize()).thenReturn(0L); // No new file2
        when(file3.getSize()).thenReturn(3072L);
        when(file3.getName()).thenReturn("newFile3");
        when(file3.getOriginalFilename()).thenReturn("new3.jpg");
        when(file3.getContentType()).thenReturn("image/jpeg");
        when(file3.getBytes()).thenReturn(new byte[]{10, 11, 12});

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.updateProduct(1L, "Updated Title", "Updated Description", 250,
                file1, file2, file3, principal);

        // Then
        verify(imageRepository).delete(image3); // Verify third image is deleted
        verify(productRepository, atLeastOnce()).save(testProduct);
        assertEquals(3, testProduct.getImages().size()); // Should still have 3 images
        assertEquals("new3.jpg", testProduct.getImages().get(2).getOriginalFileName()); // New file3 added
    }
}