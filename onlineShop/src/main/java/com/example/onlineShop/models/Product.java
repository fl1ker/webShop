package com.example.onlineShop.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "price")
    private int price;

    @Column(name = "active")
    private boolean active = true;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "product")
    private List<Image> images = new ArrayList<>();

    private Long previewImageId;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    private LocalDateTime dateOfCreated;

    public Product() {
    }

    public Product(Long id, String title, String description, int price, List<Image> images,
                   Long previewImageId, User user, LocalDateTime dateOfCreated,boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.images = images;
        this.previewImageId = previewImageId;
        this.user = user;
        this.dateOfCreated = dateOfCreated;
        this.active = active;
    }

    @PrePersist
    private void init() {
        dateOfCreated = LocalDateTime.now();
    }

    public void addImageToProduct(Image image) {
        image.setProduct(this);
        images.add(image);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Long getPreviewImageId() {
        return previewImageId;
    }

    public void setPreviewImageId(Long previewImageId) {
        this.previewImageId = previewImageId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateOfCreated() {
        return dateOfCreated;
    }

    public void setDateOfCreated(LocalDateTime dateOfCreated) {
        this.dateOfCreated = dateOfCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return price == product.price &&
                Objects.equals(id, product.id) &&
                Objects.equals(title, product.title) &&
                Objects.equals(description, product.description) &&
                Objects.equals(previewImageId, product.previewImageId) &&
                Objects.equals(dateOfCreated, product.dateOfCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, price, previewImageId, dateOfCreated);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", previewImageId=" + previewImageId +
                ", dateOfCreated=" + dateOfCreated +
                '}';
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}