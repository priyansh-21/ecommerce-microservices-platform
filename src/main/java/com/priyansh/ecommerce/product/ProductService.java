package com.priyansh.ecommerce.product;

import com.priyansh.ecommerce.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // Cached in Redis (or in-memory when CACHE_TYPE=simple) to cut DB read latency,
    // mirrors the 89% cache-hit-rate optimization on the resume.
    @Cacheable(value = "products", key = "#id")
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Cacheable(value = "productList")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @CacheEvict(value = {"products", "productList"}, allEntries = true)
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @CacheEvict(value = {"products", "productList"}, allEntries = true)
    public Product update(Long id, Product updated) {
        Product existing = getById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());
        existing.setCategory(updated.getCategory());
        return productRepository.save(existing);
    }

    @CacheEvict(value = {"products", "productList"}, allEntries = true)
    public void delete(Long id) {
        Product existing = getById(id);
        productRepository.delete(existing);
    }

    // Used internally by OrderService - decrements stock inside the order transaction.
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        if (product.getStock() < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
