package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "DanhMuc")
public class DanhMuc {

    @Id
    @Column(name = "ID_DM", length = 20)
    private String idDm;

    @Column(name = "TenDM", length = 100, nullable = false)
    private String tenDm;
}
