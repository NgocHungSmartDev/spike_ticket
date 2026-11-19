package com.example.gatewaydemo.service;

import com.example.gatewaydemo.model.Customer;
import com.example.gatewaydemo.model.Delivery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AdminService {
    private final ConcurrentHashMap<Long, Customer> customers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Delivery> deliveries = new ConcurrentHashMap<>();
    private final AtomicLong customerIds = new AtomicLong();
    private final AtomicLong deliveryIds = new AtomicLong();

    public AdminService() {
        Customer first = createCustomer(new Customer.Input("Nguyen An", "an@example.com", "0901234567", "Ho Chi Minh City"));
        Customer second = createCustomer(new Customer.Input("Tran Binh", "binh@example.com", "0912345678", "Ha Noi"));
        createDelivery(new Delivery.Input(first.id(), "Ho Chi Minh City", "In transit", "VN-1001"));
        createDelivery(new Delivery.Input(second.id(), "Ha Noi", "Pending", "VN-1002"));
    }

    public List<Customer> customers() { return customers.values().stream().sorted(Comparator.comparing(Customer::id)).toList(); }
    public Customer createCustomer(Customer.Input input) {
        long id = customerIds.incrementAndGet();
        Customer item = new Customer(id, input.name(), input.email(), input.phone(), input.address());
        customers.put(id, item);
        return item;
    }
    public Customer updateCustomer(long id, Customer.Input input) {
        Customer item = new Customer(id, input.name(), input.email(), input.phone(), input.address());
        if (customers.replace(id, item) == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        return item;
    }
    public void deleteCustomer(long id) {
        if (deliveries.values().stream().anyMatch(d -> d.customerId().equals(id)))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Delete this customer's deliveries first");
        if (customers.remove(id) == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
    }

    public List<Delivery> deliveries() { return deliveries.values().stream().sorted(Comparator.comparing(Delivery::id)).toList(); }
    public Delivery createDelivery(Delivery.Input input) {
        validateDelivery(input);
        long id = deliveryIds.incrementAndGet();
        Delivery item = new Delivery(id, input.customerId(), input.destination(), input.status(), input.trackingCode());
        deliveries.put(id, item);
        return item;
    }
    public Delivery updateDelivery(long id, Delivery.Input input) {
        if (!deliveries.containsKey(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found");
        validateDelivery(input);
        Delivery item = new Delivery(id, input.customerId(), input.destination(), input.status(), input.trackingCode());
        deliveries.put(id, item);
        return item;
    }
    public void deleteDelivery(long id) {
        if (deliveries.remove(id) == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found");
    }
    private void validateDelivery(Delivery.Input input) {
        if (!customers.containsKey(input.customerId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found");
        if (!List.of("Pending", "In transit", "Delivered", "Cancelled").contains(input.status()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid delivery status");
    }
}
