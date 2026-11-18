package com.example.gatewaydemo.service;

import com.example.gatewaydemo.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {
    private final ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public ProductService() {
        create(new Product.Input("Keyboard", new BigDecimal("49.90"), "Mechanical keyboard"));
        create(new Product.Input("Mouse", new BigDecimal("19.50"), "Wireless mouse"));
        create(new Product.Input("Monitor", new BigDecimal("199.00"), "24-inch display"));
    }
    public List<Product> list() {
        return products.values().stream().sorted(Comparator.comparing(Product::id)).toList();
    }
    public Product create(Product.Input input) {
        long id = ids.incrementAndGet();
        Product item = new Product(id, input.name(), input.price(), input.description());
        products.put(id, item);
        return item;
    }
    public Product update(long id, Product.Input input) {
        Product item = new Product(id, input.name(), input.price(), input.description());
        if (products.replace(id, item) == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        return item;
    }
    public void delete(long id) {
        if (products.remove(id) == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
    }
}
