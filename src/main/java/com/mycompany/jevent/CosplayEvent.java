package com.mycompany.jevent;

import java.time.LocalDate;

/** INHERITANCE: event cosplay. */
public class CosplayEvent extends Event {

    public CosplayEvent(String nama, LocalDate tanggal, String waktu, String kota, String lokasi,
                long hargaTiket, String penyelenggara, String deskripsi, String poster) {
        super(nama, tanggal, waktu, kota, lokasi, hargaTiket, penyelenggara, deskripsi, poster);
    }

    @Override
    public String getKategori() {
        return "Cosplay";
    }
}
