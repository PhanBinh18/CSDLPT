package com.example.demo.service;

import com.example.demo.config.DbContextHolder;
import com.example.demo.config.SiteEnum;
import com.example.demo.dto.request.OrderItemRequest;
import com.example.demo.dto.request.OrderRequest;
import com.example.demo.dto.response.OrderDetailResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.ChiTietDH;
import com.example.demo.entity.DonHang;
import com.example.demo.entity.TonKho;
import com.example.demo.entity.TonKhoId;
import com.example.demo.repository.ChiTietDHRepository;
import com.example.demo.repository.DonHangRepository;
import com.example.demo.repository.TonKhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDHRepository chiTietDHRepository;
    private final TonKhoRepository tonKhoRepository;

    // Lưu ý: @Transactional ở đây đảm bảo nếu giữa chừng lỗi, toàn bộ lệnh Insert sẽ bị hủy (Rollback)
    @Transactional
    public OrderResponse taoDonHangPhanXuat(OrderRequest request) {
        String idDhMoi = "DH" + System.currentTimeMillis(); // Sinh mã đơn hàng ngẫu nhiên
        BigDecimal tongTienDonHang = BigDecimal.ZERO;
        List<OrderDetailResponse> chiTietResponses = new ArrayList<>();

        // 1. Lưu thông tin Đơn Hàng vào Trụ sở chính (Ví dụ đặt Mặc định là Miền Bắc)
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        DonHang donHang = new DonHang();
        donHang.setIdDh(idDhMoi);
        donHang.setIdKh(request.getIdKh());
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTrangThai("CHỜ XỬ LÝ");
        donHangRepository.save(donHang); // Tạm lưu trước để lấy ID, lát cập nhật tổng tiền sau

        // 2. Duyệt qua từng sản phẩm khách đặt để tìm kho xuất hàng
        for (OrderItemRequest item : request.getItems()) {
            int soLuongCanMua = item.getSoLuong();
            BigDecimal donGiaGiaDinh = new BigDecimal("1000000"); // Giả định giá, thực tế bạn phải join bảng SanPham để lấy

            // --- CHIẾN LƯỢC TÌM KHO (Ưu tiên Kho Bắc, thiếu thì sang Kho Nam) ---

            // Bước A: Thử lấy hàng từ Kho Miền Bắc
            DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
            TonKho tkBac = tonKhoRepository.findById(new TonKhoId("KHO_MB", item.getIdSp())).orElse(null);

            if (tkBac != null && tkBac.getSoLuong() > 0) {
                int slLayTuBac = Math.min(soLuongCanMua, tkBac.getSoLuong());

                // Trừ tồn kho Miền Bắc
                tkBac.setSoLuong(tkBac.getSoLuong() - slLayTuBac);
                tonKhoRepository.save(tkBac);

                // Tạo Chi tiết đơn hàng báo xuất từ Kho Bắc
                luuChiTietDonHang(idDhMoi, item.getIdSp(), slLayTuBac, donGiaGiaDinh, "KHO_MB");
                chiTietResponses.add(taoResponseChiTiet(item.getIdSp(), slLayTuBac, donGiaGiaDinh, "KHO_MB"));

                soLuongCanMua -= slLayTuBac; // Cập nhật số lượng còn thiếu
                tongTienDonHang = tongTienDonHang.add(donGiaGiaDinh.multiply(new BigDecimal(slLayTuBac)));
            }

            // Bước B: Nếu vẫn còn thiếu hàng, nhảy sang Kho Miền Nam lấy tiếp
            if (soLuongCanMua > 0) {
                DbContextHolder.setCurrentDb(SiteEnum.MIEN_NAM);
                TonKho tkNam = tonKhoRepository.findById(new TonKhoId("KHO_MN", item.getIdSp())).orElse(null);

                if (tkNam != null && tkNam.getSoLuong() >= soLuongCanMua) {
                    // Trừ tồn kho Miền Nam
                    tkNam.setSoLuong(tkNam.getSoLuong() - soLuongCanMua);
                    tonKhoRepository.save(tkNam);

                    // Tạo Chi tiết đơn hàng báo xuất từ Kho Nam
                    luuChiTietDonHang(idDhMoi, item.getIdSp(), soLuongCanMua, donGiaGiaDinh, "KHO_MN");
                    chiTietResponses.add(taoResponseChiTiet(item.getIdSp(), soLuongCanMua, donGiaGiaDinh, "KHO_MN"));

                    tongTienDonHang = tongTienDonHang.add(donGiaGiaDinh.multiply(new BigDecimal(soLuongCanMua)));
                    soLuongCanMua = 0; // Đã đủ hàng
                }
            }

            // Bước C: Nếu quét cả 2 kho mà vẫn thiếu (soLuongCanMua > 0) -> Báo lỗi, Rollback toàn bộ!
            if (soLuongCanMua > 0) {
                throw new RuntimeException("Hệ thống không đủ tồn kho cho sản phẩm: " + item.getIdSp());
            }
        }

        // 3. Cập nhật lại tổng tiền cho Đơn hàng ở DB chính
        DbContextHolder.setCurrentDb(SiteEnum.MIEN_BAC);
        donHang.setTongTien(tongTienDonHang);
        donHangRepository.save(donHang);

        DbContextHolder.clear();

        // 4. Trả về kết quả
        OrderResponse response = new OrderResponse();
        response.setIdDh(idDhMoi);
        response.setThongBao("Đặt hàng thành công!");
        response.setTongTien(tongTienDonHang);
        response.setDanhSachSanPham(chiTietResponses);

        return response;
    }

    // --- CÁC HÀM TIỆN ÍCH (HELPER) BÊN DƯỚI ---
    private void luuChiTietDonHang(String idDh, String idSp, int soLuong, BigDecimal donGia, String idKho) {
        ChiTietDH ct = new ChiTietDH();
        ct.setIdDh(idDh);
        ct.setIdSp(idSp);
        ct.setSoLuong(soLuong);
        ct.setDonGia(donGia);
        ct.setIdKho(idKho); // Lưu bằng chứng phân mảnh vào kho tương ứng
        chiTietDHRepository.save(ct);
    }

    private OrderDetailResponse taoResponseChiTiet(String idSp, int soLuong, BigDecimal donGia, String idKho) {
        OrderDetailResponse res = new OrderDetailResponse();
        res.setIdSp(idSp);
        res.setSoLuong(soLuong);
        res.setDonGia(donGia);
        res.setThanhTien(donGia.multiply(new BigDecimal(soLuong)));
        res.setKhoXuatHang(idKho);
        return res;
    }
}