# J-EVENT – Pusat Informasi Jadwal Event Jejepangan di Lampung

Aplikasi desktop JavaFX (Maven) sesuai laporan projek J-Event.

## Fitur Pengunjung
- **Beranda**: event akan datang, event populer, dan yang baru ditambahkan
- **Jelajahi Event**: pencarian + filter kategori, tanggal, dan lokasi (kabupaten/kota Lampung)
- **Detail Event**: poster, nama, tanggal, waktu, lokasi, deskripsi, penyelenggara
- **Kalender Event**: kalender bulanan, klik tanggal untuk melihat event

## Fitur Admin
Menu **Admin** -> login (default: `admin` / `admin123`, ubah di `App.java`)
- **Dashboard**: jumlah event, event per kategori & lokasi, kegiatan mendatang
- **Tambah / Edit / Hapus Event** (termasuk upload poster)
- **Manajemen Kategori**: tambah / hapus kategori

## Penyimpanan data
Data disimpan otomatis di folder `.jevent` pada home user
(`events.tsv`, `kategori.txt`, `poster/`). Hapus folder itu untuk mengembalikan data contoh.

## Konsep OOP
- Encapsulation: atribut `Event` private + getter/setter
- Inheritance: `CosplayEvent`, `AnimeGathering`, `FestivalJepang`, `KompetisiCosplay`, `EventUmum` extends `Event`
- Abstraction/Polymorphism: `Event.getKategori()` abstract
- Factory: `EventFactory.buat(...)`; manajer data: `EventManager`; akun: `Admin`

## Menjalankan
Butuh JDK 17+.

    mvn clean javafx:run

Di NetBeans: klik kanan project -> **Run**.
