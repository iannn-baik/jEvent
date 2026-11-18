package com.mycompany.jevent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Pusat pengelolaan data event & kategori (dipakai pengunjung dan admin). */
public class EventManager {
    private final ObservableList<Event> daftarEvent = FXCollections.observableArrayList();
    private final ObservableList<String> daftarKategori = FXCollections.observableArrayList();
    private final Penyimpanan penyimpanan = new Penyimpanan();

    public EventManager() {
        if (penyimpanan.adaData()) {
            try {
                daftarKategori.addAll(penyimpanan.muatKategori());
                daftarEvent.addAll(penyimpanan.muatEvent());
                return;
            } catch (IOException ex) {
                System.err.println("Gagal membaca data, memakai data contoh: " + ex.getMessage());
                daftarKategori.clear();
                daftarEvent.clear();
            }
        }
        isiDataAwal();
        simpan();
    }

    // ================= EVENT =================

    public void tambahEvent(Event event) {
        daftarEvent.add(event);
        simpan();
    }

    /** Mengganti event lama dengan versi baru (posisi tetap). */
    public void ubahEvent(Event lama, Event baru) {
        int idx = daftarEvent.indexOf(lama);
        if (idx >= 0) {
            baru.setDilihat(lama.getDilihat());
            daftarEvent.set(idx, baru);
            simpan();
        }
    }

    public void hapusEvent(Event event) {
        daftarEvent.remove(event);
        simpan();
    }

    /** True kalau sudah ada event dengan nama & tanggal sama (selain yang sedang diedit). */
    public boolean sudahAda(String nama, LocalDate tanggal, Event abaikan) {
        return daftarEvent.stream().anyMatch(e -> e != abaikan
                && e.getNama().equalsIgnoreCase(nama) && e.getTanggal().equals(tanggal));
    }

    public void catatDilihat(Event event) {
        event.setDilihat(event.getDilihat() + 1);
        simpan();
    }

    public ObservableList<Event> getDaftarEvent() {
        return daftarEvent;
    }

    // ================= QUERY UNTUK TAMPILAN =================

    public List<Event> getMendatang(int batas) {
        return daftarEvent.stream()
                .filter(e -> !e.isSudahLewat())
                .sorted(Comparator.comparing(Event::getTanggal))
                .limit(batas)
                .collect(Collectors.toList());
    }

    public List<Event> getPopuler(int batas) {
        return daftarEvent.stream()
                .sorted(Comparator.comparingInt(Event::getDilihat).reversed()
                        .thenComparing(Event::getTanggal))
                .limit(batas)
                .collect(Collectors.toList());
    }

    /** Event yang paling baru ditambahkan admin. */
    public List<Event> getTerbaru(int batas) {
        List<Event> hasil = new ArrayList<>();
        for (int i = daftarEvent.size() - 1; i >= 0 && hasil.size() < batas; i--) {
            hasil.add(daftarEvent.get(i));
        }
        return hasil;
    }

    public List<Event> getPadaTanggal(LocalDate tanggal) {
        return daftarEvent.stream()
                .filter(e -> e.getTanggal().equals(tanggal))
                .collect(Collectors.toList());
    }

    public Map<String, Long> hitungPerKategori() {
        Map<String, Long> hasil = new LinkedHashMap<>();
        for (String k : daftarKategori) {
            hasil.put(k, 0L);
        }
        for (Event e : daftarEvent) {
            hasil.merge(e.getKategori(), 1L, Long::sum);
        }
        return hasil;
    }

    public Map<String, Long> hitungPerKota() {
        Map<String, Long> hasil = new TreeMap<>();
        for (Event e : daftarEvent) {
            hasil.merge(e.getKota(), 1L, Long::sum);
        }
        return hasil;
    }

    // ================= KATEGORI =================

    public ObservableList<String> getDaftarKategori() {
        return daftarKategori;
    }

    /** @return false kalau nama kosong atau sudah ada. */
    public boolean tambahKategori(String nama) {
        String bersih = nama.trim();
        if (bersih.isEmpty() || daftarKategori.stream().anyMatch(k -> k.equalsIgnoreCase(bersih))) {
            return false;
        }
        daftarKategori.add(bersih);
        simpan();
        return true;
    }

    public boolean kategoriDipakai(String kategori) {
        return daftarEvent.stream().anyMatch(e -> e.getKategori().equals(kategori));
    }

    public void hapusKategori(String kategori) {
        daftarKategori.remove(kategori);
        simpan();
    }

    // ================= PENYIMPANAN =================

    public String salinPoster(File file) throws IOException {
        return penyimpanan.salinPoster(file);
    }

    public void simpan() {
        try {
            penyimpanan.simpan(daftarEvent, daftarKategori);
        } catch (IOException ex) {
            System.err.println("Gagal menyimpan data: " + ex.getMessage());
        }
    }

    private void isiDataAwal() {
        daftarKategori.addAll(EventFactory.KATEGORI_BAWAAN);

        daftarEvent.add(new CosplayEvent("Lampung Cosplay Day", LocalDate.of(2026, 12, 6),
                "10:00 - 18:00", "Bandar Lampung", "Mal Boemi Kedaton", 25000,
                "Komunitas Cosplay Lampung",
                "Hari berkumpulnya para cosplayer Lampung. Ada parade, photo session, dan bazar merchandise.",
                ""));
        daftarEvent.add(new AnimeGathering("Wibu Gathering Lampung", LocalDate.of(2026, 12, 20),
                "13:00 - 17:00", "Bandar Lampung", "Taman Gajah", 0,
                "Komunitas Anime Lampung",
                "Kumpul santai penggemar anime: nonton bareng, tukar koleksi, dan kuis berhadiah.",
                ""));
        daftarEvent.add(new FestivalJepang("Matsuri Lampung", LocalDate.of(2027, 1, 17),
                "09:00 - 21:00", "Bandar Lampung", "Universitas Lampung", 15000,
                "Panitia Matsuri Lampung",
                "Festival budaya Jepang dengan kuliner, pertunjukan musik, dan workshop budaya.",
                ""));
        daftarEvent.add(new KompetisiCosplay("Lampung Cosplay Contest", LocalDate.of(2027, 2, 14),
                "10:00 - 16:00", "Metro", "Gedung Pertemuan Kota Metro", 20000,
                "Komunitas Cosplay Lampung",
                "Kompetisi cosplay kategori individu dan grup dengan hadiah menarik.",
                ""));
    }
}
