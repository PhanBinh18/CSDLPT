package com.example.demo.service;

import com.example.demo.config.DbContextHolder;
import com.example.demo.config.SiteEnum;
import com.example.demo.dto.request.OrderItemRequest;
import com.example.demo.dto.request.OrderRequest;
import com.example.demo.dto.response.OrderDetailResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.SanPham; // Import thêm Entity SanPham
import com.example.demo.repository.DonHangRepository;
import com.example.demo.repository.SanPhamRepository; // Import thêm Repository
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final TonKhoTransactionService tonKhoTxService;

    // 🔥 Inject thêm SanPhamRepository vào đây
    private final SanPhamRepository sanPhamRepository;

    public OrderResponse taoDonHangPhanXuat(OrderRequest request) {
        String idDhMoi = "DH" + System.currentTimeMillis();
        BigDecimal tongTien = BigDecimal.ZERO;
        List<OrderDetailResponse> chiTietResponses = new ArrayList<>();

        DonHang donHang = new DonHang();
        donHang.setIdDh(idDhMoi);
        donHang.setIdKh(request.getIdKh());
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTrangThai("CHỜ XỬ LÝ");

        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        donHangRepository.save(donHang);

        DbContextHolder.setCurrentDb(SiteEnum.MIEN_NAM);
        donHangRepository.save(donHang);

        for (OrderItemRequest item : request.getItems()) {
            int soLuongCanMua = item.getSoLuong();

            // 🔥 TRUY VẤN ĐƠN GIÁ THẬT TỪ DATABASE
            DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
            SanPham sanPham = sanPhamRepository.findById(item.getIdSp())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trên hệ thống: " + item.getIdSp()));
            BigDecimal donGiaThucTe = sanPham.getGia();

            DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
            int slLayTuBac = tonKhoTxService.xuatHangTuKhoBac(idDhMoi, item.getIdSp(), soLuongCanMua, donGiaThucTe); // Dùng donGiaThucTe

            if (slLayTuBac > 0) {
                soLuongCanMua -= slLayTuBac;
                tongTien = tongTien.add(donGiaThucTe.multiply(BigDecimal.valueOf(slLayTuBac)));
                chiTietResponses.add(new OrderDetailResponse(item.getIdSp(), slLayTuBac, donGiaThucTe, donGiaThucTe.multiply(BigDecimal.valueOf(slLayTuBac)), "KHO-NORTH"));
            }

            if (soLuongCanMua > 0) {
                DbContextHolder.setCurrentDb(SiteEnum.MIEN_NAM);
                int slLayTuNam = tonKhoTxService.xuatHangTuKhoNam(idDhMoi, item.getIdSp(), soLuongCanMua, donGiaThucTe); // Dùng donGiaThucTe

                if (slLayTuNam > 0) {
                    soLuongCanMua -= slLayTuNam;
                    tongTien = tongTien.add(donGiaThucTe.multiply(BigDecimal.valueOf(slLayTuNam)));
                    chiTietResponses.add(new OrderDetailResponse(item.getIdSp(), slLayTuNam, donGiaThucTe, donGiaThucTe.multiply(BigDecimal.valueOf(slLayTuNam)), "KHO-SOUTH"));
                }
            }

            if (soLuongCanMua > 0) {
                throw new RuntimeException("Không đủ tồn kho toàn hệ thống cho sản phẩm: " + item.getIdSp());
            }
        }

        donHang.setTongTien(tongTien);

        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        donHangRepository.save(donHang);

        DbContextHolder.setCurrentDb(SiteEnum.MIEN_NAM);
        donHangRepository.save(donHang);

        DbContextHolder.clear();

        OrderResponse response = new OrderResponse();
        response.setIdDh(idDhMoi);
        response.setThongBao("Đặt hàng thành công!");
        response.setTongTien(tongTien);
        response.setDanhSachSanPham(chiTietResponses);

        return response;
    }
}