package com.example.demo.repository;

import com.example.demo.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {

    // Tìm danh sách đơn hàng theo mã khách hàng
    List<DonHang> findByIdKh(String idKh);
}