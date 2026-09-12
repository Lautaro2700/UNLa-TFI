package com.tfi.order.service;

import com.tfi.order.client.ProductClient;
import com.tfi.order.client.ProductResponse;
import com.tfi.order.dto.OrderDTO;
import com.tfi.order.model.Order;
import com.tfi.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order create(OrderDTO dto) {
        // Consulta al ms-product via OpenFeign
        ProductResponse product = productClient.findById(dto.getProductId());

        // Verifica que haya stock suficiente
        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
        }

        // Calcula el precio total
        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(dto.getQuantity()));

        // Crea la orden
        Order order = Order.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(dto.getQuantity())
                .totalPrice(totalPrice)
                .build();

        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}
