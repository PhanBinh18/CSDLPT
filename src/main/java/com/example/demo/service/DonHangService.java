package com.example.demo.service;

import com.example.demo.config.DbContextHolder;
import com.example.demo.config.SiteEnum;
import com.example.demo.dto.request.OrderItemRequest;
import com.example.demo.dto.request.OrderRequest;
import com.example.demo.dto.response.OrderDetailResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPham;
import com.example.demo.repository.DonHangRepository;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.SanPhamRepository;
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
    private final SanPhamRepository sanPhamRepository;
    private final KhachHangRepository khachHangRepository;

    // --- LOGIC ĐỊNH TUYẾN MỚI TỪ BẠN ---
    private SiteEnum[] xacDinhThuTuUuTien(String khuVucKhachHang) {
        if (khuVucKhachHang == null) return new SiteEnum[]{SiteEnum.MIEN_BAC, SiteEnum.MIEN_NAM};

        return switch (khuVucKhachHang.toUpperCase()) {
            case "MIEN_BAC", "BAC_TRUNG_BO" -> new SiteEnum[]{SiteEnum.MIEN_BAC, SiteEnum.MIEN_NAM};
            case "MIEN_NAM", "NAM_TRUNG_BO" -> new SiteEnum[]{SiteEnum.MIEN_NAM, SiteEnum.MIEN_BAC};
            default -> new SiteEnum[]{SiteEnum.MIEN_BAC, SiteEnum.MIEN_NAM}; // Fallback an toàn
        };
    }

    private String khoTuongUng(SiteEnum site) {
        return site == SiteEnum.MIEN_BAC ? "KHO-NORTH" : "KHO-SOUTH";
    }
    // ------------------------------------

    public OrderResponse taoDonHangPhanXuat(OrderRequest request) {
        String idDhMoi = "DH" + System.currentTimeMillis();
        BigDecimal tongTien = BigDecimal.ZERO;
        List<OrderDetailResponse> chiTietResponses = new ArrayList<>();

        // 1. Xác định thứ tự kho ưu tiên dựa trên Khu vực của Khách hàng
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        KhachHang khach = khachHangRepository.findById(request.getIdKh())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        SiteEnum[] thuTuUuTien = xacDinhThuTuUuTien(khach.getKhuVuc());

        // 2. Tạo vỏ Đơn hàng và nhân bản
        DonHang donHang = new DonHang();
        donHang.setIdDh(idDhMoi);
        donHang.setIdKh(request.getIdKh());
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTrangThai("CHỜ XỬ LÝ");

        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        donHangRepository.save(donHang);
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_NAM);
        donHangRepository.save(donHang);

        // 3. Xử lý xuất kho
        for (OrderItemRequest item : request.getItems()) {
            int soLuongCanMua = item.getSoLuong();

            DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
            SanPham sanPham = sanPhamRepository.findById(item.getIdSp()).orElseThrow();
            BigDecimal donGiaThucTe = sanPham.getGia();

            // Vòng lặp duyệt qua các site theo thứ tự ưu tiên (Code ngắn đi phân nửa!)
            for (SiteEnum site : thuTuUuTien) {
                if (soLuongCanMua <= 0) break; // Đã đủ hàng thì thoát vòng lặp site

                DbContextHolder.setCurrentDb(site);
                String khoId = khoTuongUng(site);

                int slLayDapUng = (site == SiteEnum.MIEN_BAC)
                        ? tonKhoTxService.xuatHangTuKhoBac(idDhMoi, item.getIdSp(), soLuongCanMua, donGiaThucTe)
                        : tonKhoTxService.xuatHangTuKhoNam(idDhMoi, item.getIdSp(), soLuongCanMua, donGiaThucTe);

                if (slLayDapUng > 0) {
                    soLuongCanMua -= slLayDapUng;
                    tongTien = tongTien.add(donGiaThucTe.multiply(BigDecimal.valueOf(slLayDapUng)));
                    chiTietResponses.add(new OrderDetailResponse(item.getIdSp(), slLayDapUng, donGiaThucTe, donGiaThucTe.multiply(BigDecimal.valueOf(slLayDapUng)), khoId));
                }
            }

            if (soLuongCanMua > 0) {
                throw new RuntimeException("Không đủ tồn kho toàn hệ thống cho sản phẩm: " + item.getIdSp());
            }
        }

        // 4. Lưu tổng tiền
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