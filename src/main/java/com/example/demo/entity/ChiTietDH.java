package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ChiTietDH")
@IdClass(ChiTietDHId.class)
public class ChiTietDH {

    @Id
    @Column(name = "ID_DH", length = 20)
    private String idDh;

    @Id
    @Column(name = "ID_SP", length = 20)
    private String idSp;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGia")
    private BigDecimal donGia;

    // 🔥 Trường then chốt để biết chi tiết này xuất từ kho nào
    @Column(name = "ID_Kho", length = 20)
    private String idKho;
}
