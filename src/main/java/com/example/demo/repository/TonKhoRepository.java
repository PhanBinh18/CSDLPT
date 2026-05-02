package com.example.demo.repository;

import com.example.demo.entity.TonKho;
import com.example.demo.entity.TonKhoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TonKhoRepository extends JpaRepository<TonKho, TonKhoId> {

    // Lấy số lượng tồn kho của 1 sản phẩm (tại Site đang được trỏ tới)
    @Query("SELECT t.soLuong FROM TonKho t WHERE t.idSp = :idSp")
    Integer getSoLuongBySp(@Param("idSp") String idSp);
}