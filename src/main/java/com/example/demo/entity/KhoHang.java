package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "KhoHang")
public class KhoHang {

    @Id
    @Column(name = "ID_Kho", length = 20)
    private String idKho;

    @Column(name = "KhuVuc", length = 50, nullable = false)
    private String khuVuc;

    @Column(name = "DiaChi")
    private String diaChi;
}
