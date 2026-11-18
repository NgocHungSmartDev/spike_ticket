package com.example.gatewaydemo.controller;

import com.example.gatewaydemo.model.Product;
import com.example.gatewaydemo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }
    @GetMapping public List<Product> list() { return service.list(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product.Input input) { return service.create(input); }
    @PutMapping("/{id}")
    public Product update(@PathVariable long id, @Valid @RequestBody Product.Input input) { return service.update(id, input); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) { service.delete(id); }
}
