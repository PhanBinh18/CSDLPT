package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "KhachHang")
public class KhachHang {

    @Id
    @Column(name = "ID_KH", length = 20)
    private String idKh;

    @Column(name = "HoTen", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "Email", length = 100, unique = true)
    private String email;

    @Column(name = "SDT", length = 20)
    private String sdt;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "MatKhau", length = 255, nullable = false)
    private String matKhau;
}
