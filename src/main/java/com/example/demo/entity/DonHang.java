package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "DonHang")
public class DonHang {

    @Id
    @Column(name = "ID_DH", length = 20)
    private String idDh;

    @Column(name = "NgayDat")
    private LocalDateTime ngayDat;

    @Column(name = "TongTien")
    private BigDecimal tongTien;

    @Column(name = "TrangThai", length = 50)
    private String trangThai;

    @Column(name = "ID_KH", length = 20)
    private String idKh;
}