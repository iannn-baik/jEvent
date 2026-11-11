package com.mycompany.jevent;
public class Event {
    private String nama;
    private String tanggal;
    private String lokasi;

    public Event(String nama, String tanggal, String lokasi) {
        this.nama = nama;
        this.tanggal = tanggal;
        this.lokasi = lokasi;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getTanggal() {
        return tanggal;
    }
    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
    public String getLokasi() {
        return lokasi;
    }
    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }
    public void tampilkanInfo() {
        System.out.println("Nama Event: " + nama);
        System.out.println("Tanggal: " + tanggal);
        System.out.println("Lokasi: " + lokasi);
    }
}