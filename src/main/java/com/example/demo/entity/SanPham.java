package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "SanPham")
public class SanPham {

    @Id
    @Column(name = "ID_SP", length = 20)
    private String idSp;

    @Column(name = "TenSP", length = 255, nullable = false)
    private String tenSp;

    @Column(name = "MoTa", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "Gia", nullable = false)
    private BigDecimal gia;

    // Lưu trực tiếp ID của Danh mục
    @Column(name = "ID_DM", length = 20)
    private String idDm;
}
