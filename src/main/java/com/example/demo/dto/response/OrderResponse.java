package com.example.demo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponse {
    private String idDh;
    private String thongBao; // VD: "Đặt hàng thành công", "Kho không đủ đáp ứng"
    private BigDecimal tongTien;
    private List<OrderDetailResponse> danhSachSanPham;
}
