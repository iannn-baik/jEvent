package com.mycompany.jevent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Menyimpan data ke file di folder pengguna: ~/.jevent/
 * (events.tsv, kategori.txt, dan folder poster/).
 */
public class Penyimpanan {
    private final Path folder = Path.of(System.getProperty("user.home"), ".jevent");
    private final Path fileEvent = folder.resolve("events.tsv");
    private final Path fileKategori = folder.resolve("kategori.txt");
    private final Path folderPoster = folder.resolve("poster");

    public boolean adaData() {
        return Files.exists(fileEvent) && Files.exists(fileKategori);
    }

    public List<String> muatKategori() throws IOException {
        List<String> hasil = new ArrayList<>();
        for (String baris : Files.readAllLines(fileKategori, StandardCharsets.UTF_8)) {
            if (!baris.isBlank()) {
                hasil.add(baris.trim());
            }
        }
        return hasil;
    }

    public List<Event> muatEvent() throws IOException {
        List<Event> hasil = new ArrayList<>();
        for (String baris : Files.readAllLines(fileEvent, StandardCharsets.UTF_8)) {
            if (baris.isBlank()) {
                continue;
            }
            String[] f = baris.split("\t", -1);
            if (f.length < 11) {
                continue;
            }
            try {
                Event e = EventFactory.buat(unesc(f[0]), unesc(f[1]), LocalDate.parse(f[2]),
                        unesc(f[3]), unesc(f[4]), unesc(f[5]), Long.parseLong(f[6]),
                        unesc(f[7]), unesc(f[8]), unesc(f[9]));
                e.setDilihat(Integer.parseInt(f[10]));
                hasil.add(e);
            } catch (RuntimeException ex) {
                System.err.println("Baris data dilewati (rusak): " + ex.getMessage());
            }
        }
        return hasil;
    }

    public void simpan(List<Event> daftarEvent, List<String> daftarKategori) throws IOException {
        Files.createDirectories(folder);

        List<String> barisEvent = new ArrayList<>();
        for (Event e : daftarEvent) {
            barisEvent.add(String.join("\t",
                    esc(e.getKategori()), esc(e.getNama()), e.getTanggal().toString(),
                    esc(e.getWaktu()), esc(e.getKota()), esc(e.getLokasi()),
                    String.valueOf(e.getHargaTiket()), esc(e.getPenyelenggara()),
                    esc(e.getDeskripsi()), esc(e.getPoster()), String.valueOf(e.getDilihat())));
        }
        Files.write(fileEvent, barisEvent, StandardCharsets.UTF_8);

        List<String> barisKategori = new ArrayList<>();
        for (String k : daftarKategori) {
            barisKategori.add(k.replace("\n", " ").replace("\r", " "));
        }
        Files.write(fileKategori, barisKategori, StandardCharsets.UTF_8);
    }

    /** Menyalin gambar poster ke folder aplikasi supaya tidak hilang kalau file aslinya dipindah. */
    public String salinPoster(File sumber) throws IOException {
        Files.createDirectories(folderPoster);
        String aman = sumber.getName().replaceAll("[^A-Za-z0-9._-]", "_");
        Path tujuan = folderPoster.resolve(System.currentTimeMillis() + "_" + aman);
        Files.copy(sumber.toPath(), tujuan, StandardCopyOption.REPLACE_EXISTING);
        return tujuan.toString();
    }

    // ---- escape agar tab / baris baru di dalam teks tidak merusak format file ----

    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\t') {
                sb.append("\\t");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c != '\r') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String unesc(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                sb.append(n == 't' ? '\t' : n == 'n' ? '\n' : n);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
