package com.example.demo.dto.response;

import lombok.Data;
import java.util.Map;

@Data
public class InventoryResponse {
    private String idSp;
    private Integer tongTonKhoToanHeThong;

    // Map lưu số lượng theo từng kho, VD: {"KHO_MB": 150, "KHO_MN": 50}
    private Map<String, Integer> chiTietTungKho;
}