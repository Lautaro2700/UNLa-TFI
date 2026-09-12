package com.tfi.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-product")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse findById(@PathVariable Long id);
}
