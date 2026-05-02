package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor    // Thêm dòng này
@AllArgsConstructor   // Thêm dòng này
public class OrderDetailResponse {
    private String idSp;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String khoXuatHang;
}