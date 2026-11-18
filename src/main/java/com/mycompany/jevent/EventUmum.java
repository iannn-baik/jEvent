package com.mycompany.jevent;

import java.time.LocalDate;

/** INHERITANCE: event dengan kategori buatan admin (di luar kategori bawaan). */
public class EventUmum extends Event {
    private final String kategori;

    public EventUmum(String kategori, String nama, LocalDate tanggal, String waktu, String kota,
                     String lokasi, long hargaTiket, String penyelenggara, String deskripsi,
                     String poster) {
        super(nama, tanggal, waktu, kota, lokasi, hargaTiket, penyelenggara, deskripsi, poster);
        this.kategori = kategori;
    }

    @Override
    public String getKategori() {
        return kategori;
    }
}
