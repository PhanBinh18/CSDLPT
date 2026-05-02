package com.example.demo.controller;

import com.example.demo.dto.request.OrderRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/don-hang")
@RequiredArgsConstructor
public class DonHangController {

    private final DonHangService donHangService;

    // API Đặt hàng (Hỗ trợ phân xuất)
    // URL test: POST http://localhost:8080/api/don-hang
    @PostMapping
    public ResponseEntity<?> datHang(@RequestBody OrderRequest request) {
        try {
            OrderResponse response = donHangService.taoDonHangPhanXuat(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Nếu Service ném lỗi (ví dụ: Không đủ tồn kho), trả về HTTP 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Lỗi hệ thống khác
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }
    }
}
