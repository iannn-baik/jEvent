package com.mycompany.jevent;

public class CosplayEvent extends Event {

    private String kategori;

    public CosplayEvent(String nama, String tanggal,
                        String lokasi, String kategori) {
        super(nama, tanggal, lokasi);
        this.kategori = kategori;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo();
        System.out.println("Kategori: " + kategori);
    }
}