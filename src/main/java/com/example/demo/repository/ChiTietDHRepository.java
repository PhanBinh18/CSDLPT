package com.example.demo.repository;

import com.example.demo.entity.ChiTietDH;
import com.example.demo.entity.ChiTietDHId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ChiTietDHRepository extends JpaRepository<ChiTietDH, ChiTietDHId> {

    List<ChiTietDH> findByIdDh(String idDh);

    // Tính doanh thu thực sự của từng kho dựa trên ChiTietDH
    @Query("SELECT SUM(c.soLuong * c.donGia) FROM ChiTietDH c JOIN DonHang d ON c.idDh = d.idDh WHERE MONTH(d.ngayDat) = :thang AND YEAR(d.ngayDat) = :nam")
    BigDecimal calculateRevenueByMonth(@Param("thang") int thang, @Param("nam") int nam);

    // Lấy danh sách các mã đơn hàng có xuất hiện tại kho này
    @Query("SELECT DISTINCT c.idDh FROM ChiTietDH c")
    List<String> findAllDistinctIdDh();
}