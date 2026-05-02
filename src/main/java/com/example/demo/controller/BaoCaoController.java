package com.example.demo.controller;

import com.example.demo.dto.response.DoanhThuResponse;
import com.example.demo.entity.DonHang;
import com.example.demo.service.BaoCaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bao-cao")
@RequiredArgsConstructor
public class BaoCaoController {

    private final BaoCaoService baoCaoService;

    // API 1: Lấy báo cáo doanh thu phân tán
    // Dùng Postman: GET http://localhost:8080/api/bao-cao/doanh-thu?thang=5&nam=2026
    @GetMapping("/doanh-thu")
    public ResponseEntity<DoanhThuResponse> thongKeDoanhThu(
            @RequestParam int thang,
            @RequestParam int nam) {
        return ResponseEntity.ok(baoCaoService.thongKeDoanhThu(thang, nam));
    }

    // API 2: Lấy các đơn hàng bị phân mảnh (Lấy hàng từ nhiều kho)
    // Dùng Postman: GET http://localhost:8080/api/bao-cao/don-hang-phan-xuat
    @GetMapping("/don-hang-phan-xuat")
    public ResponseEntity<List<DonHang>> getDonHangPhanXuat() {
        return ResponseEntity.ok(baoCaoService.timDonHangXuatTuNhieuKho());
    }
}