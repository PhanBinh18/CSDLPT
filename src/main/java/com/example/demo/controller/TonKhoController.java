package com.example.demo.controller;

import com.example.demo.dto.response.InventoryResponse;
import com.example.demo.service.TonKhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ton-kho")
@RequiredArgsConstructor
public class TonKhoController {

    private final TonKhoService tonKhoService;

    // API Tra cứu tồn kho toàn hệ thống
    // URL test: GET http://localhost:8080/api/ton-kho/SP01
    @GetMapping("/{idSp}")
    public ResponseEntity<InventoryResponse> kiemTraTonKho(@PathVariable String idSp) {
        InventoryResponse response = tonKhoService.kiemTraTonKhoToanHeThong(idSp);
        return ResponseEntity.ok(response);
    }
}
