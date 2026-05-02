package com.example.demo.service;

import com.example.demo.config.DbContextHolder;
import com.example.demo.config.SiteEnum;
import com.example.demo.dto.response.InventoryResponse;
import com.example.demo.repository.TonKhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TonKhoService {

    private final TonKhoRepository tonKhoRepository;

    public InventoryResponse kiemTraTonKhoToanHeThong(String idSp) {
        InventoryResponse response = new InventoryResponse();
        response.setIdSp(idSp);

        int tongTonKho = 0;
        Map<String, Integer> chiTiet = new HashMap<>();

        // 1. Nhảy sang site Miền Bắc lấy dữ liệu
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        Integer tonKhoBac = tonKhoRepository.getSoLuongBySpAndKho(idSp, "KHO-NORTH");
        int slBac = (tonKhoBac != null) ? tonKhoBac : 0;
        tongTonKho += slBac;
        chiTiet.put("KHO-NORTH", slBac); // Giả định KHO-NORTH là ID của kho miền Bắc

        // 2. Nhảy sang site Miền Nam lấy dữ liệu
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_NAM);
        Integer tonKhoNam = tonKhoRepository.getSoLuongBySpAndKho(idSp, "KHO-SOUTH");
        int slNam = (tonKhoNam != null) ? tonKhoNam : 0;
        tongTonKho += slNam;
        chiTiet.put("KHO-SOUTH", slNam); // Giả định KHO-SOUTH là ID của kho miền Nam

        // 3. Dọn dẹp context
        DbContextHolder.clear();

        // 4. Trả kết quả gom cụm
        response.setTongTonKhoToanHeThong(tongTonKho);
        response.setChiTietTungKho(chiTiet);

        return response;
    }
}