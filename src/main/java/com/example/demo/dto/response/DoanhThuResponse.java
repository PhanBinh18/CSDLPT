package com.example.demo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class DoanhThuResponse {
    private int thang;
    private int nam;
    private BigDecimal tongDoanhThuToanHeThong;

    // Lưu doanh thu của từng kho
    private Map<String, BigDecimal> chiTietTungKho;
}