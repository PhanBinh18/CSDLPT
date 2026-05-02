package com.example.demo.repository;

import com.example.demo.entity.ChiTietDH;
import com.example.demo.entity.ChiTietDHId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDHRepository extends JpaRepository<ChiTietDH, ChiTietDHId> {

    // Tìm tất cả chi tiết của một đơn hàng cụ thể
    List<ChiTietDH> findByIdDh(String idDh);
}