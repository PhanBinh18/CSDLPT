package com.example.demo.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private String idKh; // Ai là người đặt?
    private List<OrderItemRequest> items; // Họ đặt những món gì?
}