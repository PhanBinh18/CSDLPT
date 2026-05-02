package com.example.demo.service;

import com.example.demo.entity.ChiTietDH;
import com.example.demo.entity.TonKho;
import com.example.demo.entity.TonKhoId;
import com.example.demo.repository.ChiTietDHRepository;
import com.example.demo.repository.TonKhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TonKhoTransactionService {

    private final TonKhoRepository tonKhoRepository;
    private final ChiTietDHRepository chiTietDHRepository;

    @Transactional  // Giao dịch cục bộ
    public int xuatHangTuKhoBac(String idDh, String idSp, int soLuongCanMua, BigDecimal donGia) {
        TonKho tk = tonKhoRepository.findById(new TonKhoId("KHO-NORTH", idSp)).orElse(null);
        if (tk == null || tk.getSoLuong() <= 0) return 0;

        int slLay = Math.min(soLuongCanMua, tk.getSoLuong());
        tk.setSoLuong(tk.getSoLuong() - slLay);
        tonKhoRepository.save(tk);

        ChiTietDH ct = new ChiTietDH();
        ct.setIdDh(idDh);
        ct.setIdSp(idSp);
        ct.setSoLuong(slLay);
        ct.setDonGia(donGia);
        ct.setIdKho("KHO-NORTH");
        chiTietDHRepository.save(ct);

        return slLay;
    }

    @Transactional  // Giao dịch cục bộ
    public int xuatHangTuKhoNam(String idDh, String idSp, int soLuongCanMua, BigDecimal donGia) {
        TonKho tk = tonKhoRepository.findById(new TonKhoId("KHO-SOUTH", idSp)).orElse(null);
        if (tk == null || tk.getSoLuong() < soLuongCanMua) return 0;

        tk.setSoLuong(tk.getSoLuong() - soLuongCanMua);
        tonKhoRepository.save(tk);

        ChiTietDH ct = new ChiTietDH();
        ct.setIdDh(idDh);
        ct.setIdSp(idSp);
        ct.setSoLuong(soLuongCanMua);
        ct.setDonGia(donGia);
        ct.setIdKho("KHO-SOUTH");
        chiTietDHRepository.save(ct);

        return soLuongCanMua;
    }
}