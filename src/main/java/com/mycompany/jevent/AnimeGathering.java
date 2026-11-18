package com.mycompany.jevent;

import java.time.LocalDate;

/** INHERITANCE: kumpul-kumpul komunitas anime. */
public class AnimeGathering extends Event {

    public AnimeGathering(String nama, LocalDate tanggal, String waktu, String kota, String lokasi,
                long hargaTiket, String penyelenggara, String deskripsi, String poster) {
        super(nama, tanggal, waktu, kota, lokasi, hargaTiket, penyelenggara, deskripsi, poster);
    }

    @Override
    public String getKategori() {
        return "Anime Gathering";
    }
}
