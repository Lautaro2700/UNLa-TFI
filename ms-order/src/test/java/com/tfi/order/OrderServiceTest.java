package com.tfi.order;

import com.tfi.order.client.ProductClient;
import com.tfi.order.client.ProductResponse;
import com.tfi.order.dto.OrderDTO;
import com.tfi.order.model.Order;
import com.tfi.order.repository.OrderRepository;
import com.tfi.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    private ProductResponse product;
    private Order order;

    @BeforeEach
    void setUp() {
        product = new ProductResponse();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1500.00"));
        product.setStock(10);

        order = Order.builder()
                .id(1L)
                .productId(1L)
                .productName("Laptop")
                .quantity(2)
                .totalPrice(new BigDecimal("3000.00"))
                .status("PENDING")
                .build();
    }

    @Test
    void createOrder_success() {
        OrderDTO dto = new OrderDTO();
        dto.setProductId(1L);
        dto.setQuantity(2);

        when(productClient.findById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.create(dto);

        assertNotNull(result);
        assertEquals("Laptop", result.getProductName());
        assertEquals(2, result.getQuantity());
        assertEquals(new BigDecimal("3000.00"), result.getTotalPrice());
        verify(productClient, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_insufficientStock() {
        OrderDTO dto = new OrderDTO();
        dto.setProductId(1L);
        dto.setQuantity(20);

        when(productClient.findById(1L)).thenReturn(product);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.create(dto));

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_productNotFound() {
        OrderDTO dto = new OrderDTO();
        dto.setProductId(99L);
        dto.setQuantity(1);

        when(productClient.findById(99L)).thenThrow(new RuntimeException("Product not found"));

        assertThrows(RuntimeException.class, () -> orderService.create(dto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void findAll_returnsOrderList() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findById_existingOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getProductName());
    }

    @Test
    void findById_orderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.findById(99L));

        assertTrue(exception.getMessage().contains("Order not found"));
    }
}
