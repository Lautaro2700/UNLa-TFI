package com.tfi.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfi.order.controller.OrderController;
import com.tfi.order.dto.OrderDTO;
import com.tfi.order.model.Order;
import com.tfi.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;
    private Order order;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        order = Order.builder()
                .id(1L)
                .productId(1L)
                .productName("Laptop")
                .quantity(2)
                .totalPrice(new BigDecimal("3000.00"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_returns200WithOrderList() throws Exception {
        when(orderService.findAll()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Laptop"))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService, times(1)).findAll();
    }

    @Test
    void findById_existingOrder_returns200() throws Exception {
        when(orderService.findById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Laptop"));

        verify(orderService, times(1)).findById(1L);
    }

    @Test
    void create_validOrder_returns201() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setProductId(1L);
        dto.setQuantity(2);

        when(orderService.create(any(OrderDTO.class))).thenReturn(order);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.totalPrice").value(3000.00));

        verify(orderService, times(1)).create(any(OrderDTO.class));
    }

    @Test
    void create_invalidOrder_returns400() throws Exception {
        OrderDTO dto = new OrderDTO();

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).create(any(OrderDTO.class));
    }

    @Test
    void delete_existingOrder_returns204() throws Exception {
        doNothing().when(orderService).delete(1L);

        mockMvc.perform(delete("/api/order/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).delete(1L);
    }
}