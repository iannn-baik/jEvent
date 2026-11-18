package com.mycompany.jevent;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Membuat objek Event yang sesuai kategori, plus pembantu validasi. */
public final class EventFactory {

    public static final List<String> KATEGORI_BAWAAN =
            List.of("Cosplay", "Anime Gathering", "Festival Jepang", "Kompetisi Cosplay");

    /** Kabupaten/kota di Provinsi Lampung. */
    public static final List<String> DAFTAR_KOTA = List.of(
            "Bandar Lampung", "Metro", "Lampung Selatan", "Lampung Tengah", "Lampung Utara",
            "Lampung Barat", "Lampung Timur", "Way Kanan", "Tulang Bawang", "Tanggamus",
            "Pesawaran", "Pringsewu", "Mesuji", "Tulang Bawang Barat", "Pesisir Barat");

    private static final Pattern POLA_WAKTU =
            Pattern.compile("^(\\d{1,2})[:.](\\d{2})(?:\\s*-\\s*(\\d{1,2})[:.](\\d{2}))?$");

    private EventFactory() { }

    public static Event buat(String kategori, String nama, LocalDate tanggal, String waktu,
                             String kota, String lokasi, long harga, String penyelenggara,
                             String deskripsi, String poster) {
        switch (kategori) {
            case "Cosplay":
                return new CosplayEvent(nama, tanggal, waktu, kota, lokasi, harga, penyelenggara, deskripsi, poster);
            case "Anime Gathering":
                return new AnimeGathering(nama, tanggal, waktu, kota, lokasi, harga, penyelenggara, deskripsi, poster);
            case "Festival Jepang":
                return new FestivalJepang(nama, tanggal, waktu, kota, lokasi, harga, penyelenggara, deskripsi, poster);
            case "Kompetisi Cosplay":
                return new KompetisiCosplay(nama, tanggal, waktu, kota, lokasi, harga, penyelenggara, deskripsi, poster);
            default:
                return new EventUmum(kategori, nama, tanggal, waktu, kota, lokasi, harga, penyelenggara, deskripsi, poster);
        }
    }

    /**
     * Merapikan input waktu: "10.00-17.00" menjadi "10:00 - 17:00".
     * Mengembalikan null kalau formatnya tidak valid.
     */
    public static String normalisasiWaktu(String input) {
        Matcher m = POLA_WAKTU.matcher(input.trim());
        if (!m.matches()) {
            return null;
        }
        String mulai = jam(m.group(1), m.group(2));
        if (mulai == null) {
            return null;
        }
        if (m.group(3) == null) {
            return mulai;
        }
        String selesai = jam(m.group(3), m.group(4));
        if (selesai == null) {
            return null;
        }
        return mulai + " - " + selesai;
    }

    private static String jam(String jam, String menit) {
        int j = Integer.parseInt(jam);
        int m = Integer.parseInt(menit);
        if (j > 23 || m > 59) {
            return null;
        }
        return String.format("%02d:%02d", j, m);
    }
}
