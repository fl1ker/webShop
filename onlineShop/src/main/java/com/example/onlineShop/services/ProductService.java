package com.example.onlineShop.services;

import com.example.onlineShop.models.Image;
import com.example.onlineShop.models.Product;
import com.example.onlineShop.models.User;
import com.example.onlineShop.repositories.ImageRepository;
import com.example.onlineShop.repositories.ProductRepository;
import com.example.onlineShop.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository,
                          UserRepository userRepository,
                          ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }
    public List<Product> listProducts(String title) {
        if (title != null) return productRepository.findByTitle(title);
        return productRepository.findAll();
    }

    public void saveProduct(Principal principal, Product product, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        product.setUser(getUserByPrincipal(principal));
        Image image1;
        Image image2;
        Image image3;
        if(file1.getSize() > 0){
            image1 = toImageEntity(file1);
            image1.setPreviewImage(true);
            product.addImageToProduct(image1);
        }
        if(file2.getSize() > 0){
            image2 = toImageEntity(file2);
            product.addImageToProduct(image2);
        }
        if(file3.getSize() > 0){
            image3 = toImageEntity(file3);
            product.addImageToProduct(image3);
        }

        log.info("Saving new Product. Title: {}; Author email: {}", product.getTitle(), product.getUser().getEmail());
        Product productFromDb = productRepository.save(product);
        productFromDb.setPreviewImageId(productFromDb.getImages().get(0).getId());
        productRepository.save(product);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) return new User();
        return userRepository.findByEmail(principal.getName());
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public void deleteProduct(User user, Long id) {
        log.info("Attempting to delete product with id = {}", id);
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            log.info("Product found: {}", product.getTitle());
            if (product.getUser().getId().equals(user.getId())) {
                productRepository.delete(product);
                log.info("Product with id = {} was deleted", id);
            } else {
                log.error("User: {} doesn't own this product with id = {}", user.getEmail(), id);
            }
        } else {
            log.error("Product with id = {} was not found", id);
        }
    }

    public Product getProductById(Long id){
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateProduct(Long id, String title, String description, int price,
                              MultipartFile file1, MultipartFile file2, MultipartFile file3,
                              Principal principal) throws IOException {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return;

        User currentUser = getUserByPrincipal(principal);
        if (!product.getUser().getId().equals(currentUser.getId())) {
            log.warn("User {} tried to edit product {} not belonging to them", currentUser.getEmail(), id);
            return;
        }

        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);

        List<Image> images = product.getImages();

        // ===== ЗАМЕНА file1 =====
        if (file1.getSize() > 0) {
            if (images.size() >= 1) {
                imageRepository.delete(images.get(0)); // удалить из БД
                images.remove(0); // удалить из списка
            }
            Image image1 = toImageEntity(file1);
            image1.setPreviewImage(true);
            product.addImageToProduct(image1);
            product.setPreviewImageId(null); // будет пересчитан позже
        }

        // ===== ЗАМЕНА file2 =====
        if (file2.getSize() > 0) {
            if (images.size() >= 2) {
                imageRepository.delete(images.get(1));
                images.remove(1);
            }
            Image image2 = toImageEntity(file2);
            product.addImageToProduct(image2);
        }

        // ===== ЗАМЕНА file3 =====
        if (file3.getSize() > 0) {
            if (images.size() >= 3) {
                imageRepository.delete(images.get(2));
                images.remove(2);
            }
            Image image3 = toImageEntity(file3);
            product.addImageToProduct(image3);
        }

        Product savedProduct = productRepository.save(product);

        // Если не указан previewImageId — назначим
        if (savedProduct.getPreviewImageId() == null && !savedProduct.getImages().isEmpty()) {
            savedProduct.setPreviewImageId(savedProduct.getImages().get(0).getId());
            productRepository.save(savedProduct);
        }

        log.info("Product with id = {} updated", id);
    }

}
