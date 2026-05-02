package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietDHId implements Serializable {
    private String idDh;
    private String idSp;

    public ChiTietDHId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietDHId that = (ChiTietDHId) o;
        return Objects.equals(idDh, that.idDh) && Objects.equals(idSp, that.idSp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDh, idSp);
    }
}
