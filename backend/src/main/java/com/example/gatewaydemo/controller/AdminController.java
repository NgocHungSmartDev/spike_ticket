package com.example.gatewaydemo.controller;

import com.example.gatewaydemo.model.Customer;
import com.example.gatewaydemo.model.Delivery;
import com.example.gatewaydemo.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {
    private final AdminService service;
    public AdminController(AdminService service) { this.service = service; }

    @GetMapping("/customers") public List<Customer> customers() { return service.customers(); }
    @PostMapping("/customers") @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@Valid @RequestBody Customer.Input input) { return service.createCustomer(input); }
    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable long id, @Valid @RequestBody Customer.Input input) { return service.updateCustomer(id, input); }
    @DeleteMapping("/customers/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable long id) { service.deleteCustomer(id); }

    @GetMapping("/deliveries") public List<Delivery> deliveries() { return service.deliveries(); }
    @PostMapping("/deliveries") @ResponseStatus(HttpStatus.CREATED)
    public Delivery createDelivery(@Valid @RequestBody Delivery.Input input) { return service.createDelivery(input); }
    @PutMapping("/deliveries/{id}")
    public Delivery updateDelivery(@PathVariable long id, @Valid @RequestBody Delivery.Input input) { return service.updateDelivery(id, input); }
    @DeleteMapping("/deliveries/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDelivery(@PathVariable long id) { service.deleteDelivery(id); }
}
