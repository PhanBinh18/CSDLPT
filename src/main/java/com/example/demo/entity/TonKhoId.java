package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;

// Class này dùng để định nghĩa khóa chính gồm 2 cột
public class TonKhoId implements Serializable {
    private String idKho;
    private String idSp;

    // Bắt buộc phải có constructor rỗng, equals và hashCode
    public TonKhoId() {}

    public TonKhoId(String idKho, String idSp) {
        this.idKho = idKho;
        this.idSp = idSp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TonKhoId tonKhoId = (TonKhoId) o;
        return Objects.equals(idKho, tonKhoId.idKho) && Objects.equals(idSp, tonKhoId.idSp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idKho, idSp);
    }
}
