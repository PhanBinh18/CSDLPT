package com.example.demo.service;

import com.example.demo.config.DbContextHolder;
import com.example.demo.config.SiteEnum;
import com.example.demo.dto.response.DoanhThuResponse;
import com.example.demo.entity.DonHang;
import com.example.demo.repository.ChiTietDHRepository;
import com.example.demo.repository.DonHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BaoCaoService {

    private final ChiTietDHRepository chiTietDHRepository;
    private final DonHangRepository donHangRepository;

    // --- YÊU CẦU 6.1: THỐNG KÊ DOANH THU THEO THÁNG PHÂN TÁN ---
    public DoanhThuResponse thongKeDoanhThu(int thang, int nam) {
        DoanhThuResponse response = new DoanhThuResponse();
        response.setThang(thang);
        response.setNam(nam);

        Map<String, BigDecimal> chiTiet = new HashMap<>();
        BigDecimal tongDoanhThu = BigDecimal.ZERO;

        // 1. Sang Kho Bắc lấy doanh thu
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        BigDecimal dtBac = chiTietDHRepository.calculateRevenueByMonth(thang, nam);
        if (dtBac == null) dtBac = BigDecimal.ZERO;
        chiTiet.put("KHO-NORTH", dtBac);
        tongDoanhThu = tongDoanhThu.add(dtBac);

        // 2. Sang Kho Nam lấy doanh thu
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_NAM);
        BigDecimal dtNam = chiTietDHRepository.calculateRevenueByMonth(thang, nam);
        if (dtNam == null) dtNam = BigDecimal.ZERO;
        chiTiet.put("KHO-SOUTH", dtNam);
        tongDoanhThu = tongDoanhThu.add(dtNam);

        DbContextHolder.clear();

        response.setTongDoanhThuToanHeThong(tongDoanhThu);
        response.setChiTietTungKho(chiTiet);
        return response;
    }

    // --- YÊU CẦU 6.2: TÌM ĐƠN HÀNG XUẤT TỪ NHIỀU KHO (SPLIT ORDERS) ---
    // Thuật toán: Lấy mã đơn ở Kho Bắc giao với mã đơn ở Kho Nam
    public List<DonHang> timDonHangXuatTuNhieuKho() {

        // 1. Lấy danh sách ID Đơn hàng có dính dáng tới Kho Bắc
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        Set<String> dhBac = new HashSet<>(chiTietDHRepository.findAllDistinctIdDh());

        // 2. Lấy danh sách ID Đơn hàng có dính dáng tới Kho Nam
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_NAM);
        Set<String> dhNam = new HashSet<>(chiTietDHRepository.findAllDistinctIdDh());

        // 3. Xử lý trên RAM: Tìm tập giao (Intersection) của 2 tập hợp
        // Lệnh này sẽ loại bỏ các ID không có trong dhNam, chỉ giữ lại ID nằm ở CẢ 2 KHO
        dhBac.retainAll(dhNam);

        // 4. Lấy thông tin chi tiết của các đơn hàng này
        List<DonHang> ketQua = new ArrayList<>();
        if (!dhBac.isEmpty()) {
            // Quay về Trụ sở chính (Miền Bắc) để tra thông tin tổng quát của Đơn hàng
            DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
            ketQua = donHangRepository.findAllById(dhBac);
        }

        DbContextHolder.clear();
        return ketQua;
    }
}