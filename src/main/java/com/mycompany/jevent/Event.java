package com.mycompany.jevent;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Class induk (abstract) untuk semua jenis event.
 * ENCAPSULATION : atribut private, diakses lewat getter/setter.
 * ABSTRACTION   : getKategori() wajib diimplementasikan subclass.
 */
public abstract class Event {
    private String nama;
    private LocalDate tanggal;
    private String waktu;          // contoh: "10:00 - 18:00"
    private String kota;           // kabupaten/kota di Lampung (untuk filter lokasi)
    private String lokasi;         // nama tempat / alamat
    private long hargaTiket;
    private String penyelenggara;
    private String deskripsi;
    private String poster;         // path file gambar (boleh kosong)
    private int dilihat;           // dipakai untuk "Event Populer"

    public Event(String nama, LocalDate tanggal, String waktu, String kota, String lokasi,
                 long hargaTiket, String penyelenggara, String deskripsi, String poster) {
        this.nama = nama;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.kota = kota;
        this.lokasi = lokasi;
        this.hargaTiket = hargaTiket;
        this.penyelenggara = penyelenggara;
        this.deskripsi = deskripsi;
        this.poster = poster == null ? "" : poster;
    }

    /** POLYMORPHISM: tiap subclass punya kategori sendiri. */
    public abstract String getKategori();

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public String getWaktu() { return waktu; }
    public void setWaktu(String waktu) { this.waktu = waktu; }

    public String getKota() { return kota; }
    public void setKota(String kota) { this.kota = kota; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public long getHargaTiket() { return hargaTiket; }
    public void setHargaTiket(long hargaTiket) { this.hargaTiket = hargaTiket; }

    public String getPenyelenggara() { return penyelenggara; }
    public void setPenyelenggara(String penyelenggara) { this.penyelenggara = penyelenggara; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster == null ? "" : poster; }

    public int getDilihat() { return dilihat; }
    public void setDilihat(int dilihat) { this.dilihat = dilihat; }

    public boolean isSudahLewat() {
        return tanggal.isBefore(LocalDate.now());
    }

    public String getStatus() {
        return isSudahLewat() ? "Selesai" : "Akan datang";
    }

    public String getHargaTampil() {
        if (hargaTiket == 0) {
            return "Gratis";
        }
        return String.format(Locale.forLanguageTag("id-ID"), "Rp %,d", hargaTiket);
    }
}
