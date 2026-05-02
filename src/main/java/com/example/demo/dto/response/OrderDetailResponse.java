package com.example.demo.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetailResponse {
    private String idSp;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    // 🔥 Báo cho người dùng biết món này xuất từ kho nào
    private String khoXuatHang;
}