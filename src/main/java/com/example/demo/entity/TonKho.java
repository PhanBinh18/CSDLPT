package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data // Dùng Lombok để tự sinh Getter/Setter
@Entity
@Table(name = "TonKho")
@IdClass(TonKhoId.class) // Khai báo sử dụng khóa phức hợp
public class TonKho {

    @Id
    @Column(name = "ID_Kho", length = 20)
    private String idKho;

    @Id
    @Column(name = "ID_SP", length = 20)
    private String idSp;

    @Column(name = "SoLuong")
    private Integer soLuong;
}