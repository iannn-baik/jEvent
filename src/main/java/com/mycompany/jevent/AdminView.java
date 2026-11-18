package com.mycompany.jevent;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

/** Halaman admin: Dashboard, Tambah/Edit/Hapus Event, dan Manajemen Kategori. */
public class AdminView {
    private final EventManager manager;
    private final BorderPane view = new BorderPane();

    // ---- form event ----
    private final Label lblModeForm = new Label("Tambah Event Baru");
    private final TextField tfNama = new TextField();
    private final ComboBox<String> cbKategori = new ComboBox<>();
    private final DatePicker dpTanggal = new DatePicker();
    private final TextField tfWaktu = new TextField();
    private final ComboBox<String> cbKota = new ComboBox<>();
    private final TextField tfLokasi = new TextField();
    private final TextField tfHarga = new TextField();
    private final TextField tfPenyelenggara = new TextField();
    private final TextArea taDeskripsi = new TextArea();
    private final Label lblPoster = new Label("Belum ada poster");
    private final Button btnSimpan = new Button("Simpan Event");
    private final TableView<Event> tabel = new TableView<>();

    private Event sedangDiedit;     // null = mode tambah
    private File posterBaru;        // poster yang baru dipilih (belum disalin)
    private String posterLama = ""; // poster event yang sedang diedit

    public AdminView(EventManager manager, Admin admin, Runnable keluar) {
        this.manager = manager;

        Label judul = new Label("Dashboard Admin");
        judul.getStyleClass().add("judul-seksi");
        Label user = new Label("Masuk sebagai: " + admin.getUsername());
        Button btnKeluar = new Button("Logout");
        btnKeluar.setOnAction(e -> keluar.run());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(12, judul, spacer, user, btnKeluar);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 16, 10, 16));

        Tab tabDashboard = new Tab("Dashboard", buatDashboard());
        tabDashboard.setOnSelectionChanged(e -> {
            if (tabDashboard.isSelected()) {
                tabDashboard.setContent(buatDashboard()); // segarkan statistik
            }
        });
        Tab tabEvent = new Tab("Tambah / Kelola Event", buatTabEvent());
        Tab tabKategori = new Tab("Manajemen Kategori", buatTabKategori());

        TabPane tabPane = new TabPane(tabDashboard, tabEvent, tabKategori);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        view.setTop(bar);
        view.setCenter(tabPane);
    }

    public Node getView() {
        return view;
    }

    // ==================== DASHBOARD ====================

    private Node buatDashboard() {
        long total = manager.getDaftarEvent().size();
        long mendatang = manager.getDaftarEvent().stream().filter(e -> !e.isSudahLewat()).count();

        HBox stat = new HBox(14,
                kartuStat("Total Event", total),
                kartuStat("Akan Datang", mendatang),
                kartuStat("Sudah Selesai", total - mendatang),
                kartuStat("Jumlah Kategori", manager.getDaftarKategori().size()));

        VBox perKategori = blokStatistik("Event per Kategori", manager.hitungPerKategori(), total);
        VBox perKota = blokStatistik("Event per Lokasi", manager.hitungPerKota(), total);
        HBox.setHgrow(perKategori, Priority.ALWAYS);
        HBox.setHgrow(perKota, Priority.ALWAYS);
        HBox dua = new HBox(24, perKategori, perKota);

        VBox akanDatang = new VBox(6, UiUtil.judulSeksi("Kegiatan yang Akan Datang"));
        if (manager.getMendatang(5).isEmpty()) {
            akanDatang.getChildren().add(new Label("Tidak ada kegiatan mendatang."));
        }
        for (Event e : manager.getMendatang(5)) {
            akanDatang.getChildren().add(new Label(UiUtil.FORMAT_TANGGAL.format(e.getTanggal())
                    + "  -  " + e.getNama() + " (" + e.getKota() + ")"));
        }

        VBox isi = new VBox(20, stat, dua, akanDatang);
        isi.setPadding(new Insets(20));
        ScrollPane scroll = new ScrollPane(isi);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private VBox kartuStat(String label, long nilai) {
        Label angka = new Label(String.valueOf(nilai));
        angka.getStyleClass().add("stat-angka");
        Label teks = new Label(label);
        teks.getStyleClass().add("stat-label");
        VBox kartu = new VBox(2, angka, teks);
        kartu.getStyleClass().add("kartu-stat");
        kartu.setAlignment(Pos.CENTER);
        kartu.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(kartu, Priority.ALWAYS);
        return kartu;
    }

    private VBox blokStatistik(String judul, Map<String, Long> data, long total) {
        VBox blok = new VBox(8, UiUtil.judulSeksi(judul));
        if (data.isEmpty()) {
            blok.getChildren().add(new Label("Belum ada data."));
        }
        for (Map.Entry<String, Long> en : data.entrySet()) {
            Label nama = new Label(en.getKey());
            nama.setPrefWidth(150);
            nama.setMinWidth(150);
            ProgressBar bar = new ProgressBar(total == 0 ? 0 : en.getValue() / (double) total);
            bar.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(bar, Priority.ALWAYS);
            Label jumlah = new Label(String.valueOf(en.getValue()));
            HBox baris = new HBox(10, nama, bar, jumlah);
            baris.setAlignment(Pos.CENTER_LEFT);
            blok.getChildren().add(baris);
        }
        return blok;
    }

    // ==================== TAMBAH / EDIT / HAPUS EVENT ====================

    private Node buatTabEvent() {
        cbKategori.setItems(manager.getDaftarKategori());
        cbKategori.setPromptText("Pilih kategori");
        cbKota.getItems().setAll(EventFactory.DAFTAR_KOTA);
        cbKota.setPromptText("Pilih kabupaten/kota");
        dpTanggal.setPromptText("Pilih tanggal");
        tfNama.setPromptText("Contoh: Lampung Cosplay Day");
        tfWaktu.setPromptText("Contoh: 10:00 - 18:00");
        tfLokasi.setPromptText("Contoh: Mal Boemi Kedaton");
        tfHarga.setPromptText("Isi 0 jika gratis");
        tfPenyelenggara.setPromptText("Nama komunitas / panitia");
        taDeskripsi.setPromptText("Deskripsi kegiatan");
        taDeskripsi.setPrefRowCount(3);
        taDeskripsi.setWrapText(true);
        cbKategori.setMaxWidth(Double.MAX_VALUE);
        cbKota.setMaxWidth(Double.MAX_VALUE);
        dpTanggal.setMaxWidth(Double.MAX_VALUE);

        Button btnPoster = new Button("Pilih Poster...");
        btnPoster.setOnAction(e -> pilihPoster());
        Button btnHapusPoster = new Button("Hapus Poster");
        btnHapusPoster.setOnAction(e -> {
            posterBaru = null;
            posterLama = "";
            lblPoster.setText("Belum ada poster");
        });
        HBox barisPoster = new HBox(10, btnPoster, btnHapusPoster, lblPoster);
        barisPoster.setAlignment(Pos.CENTER_LEFT);

        btnSimpan.getStyleClass().add("tombol-utama");
        btnSimpan.setOnAction(e -> simpanEvent());
        Button btnReset = new Button("Reset / Batal Edit");
        btnReset.setOnAction(e -> resetForm());
        HBox barisTombol = new HBox(10, btnSimpan, btnReset);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        ColumnConstraints c0 = new ColumnConstraints();
        c0.setMinWidth(95);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setMinWidth(95);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c0, c1, c2, c3);

        form.add(new Label("Nama Event"), 0, 0);
        form.add(tfNama, 1, 0);
        form.add(new Label("Kategori"), 2, 0);
        form.add(cbKategori, 3, 0);
        form.add(new Label("Tanggal"), 0, 1);
        form.add(dpTanggal, 1, 1);
        form.add(new Label("Waktu (WIB)"), 2, 1);
        form.add(tfWaktu, 3, 1);
        form.add(new Label("Kab./Kota"), 0, 2);
        form.add(cbKota, 1, 2);
        form.add(new Label("Lokasi"), 2, 2);
        form.add(tfLokasi, 3, 2);
        form.add(new Label("Harga Tiket (Rp)"), 0, 3);
        form.add(tfHarga, 1, 3);
        form.add(new Label("Penyelenggara"), 2, 3);
        form.add(tfPenyelenggara, 3, 3);
        form.add(new Label("Deskripsi"), 0, 4);
        form.add(taDeskripsi, 1, 4, 3, 1);
        form.add(new Label("Poster"), 0, 5);
        form.add(barisPoster, 1, 5, 3, 1);
        form.add(barisTombol, 1, 6, 3, 1);

        siapkanTabel();
        Button btnEdit = new Button("Edit Event Terpilih");
        btnEdit.setOnAction(e -> editTerpilih());
        Button btnHapus = new Button("Hapus Event Terpilih");
        btnHapus.getStyleClass().add("tombol-bahaya");
        btnHapus.setOnAction(e -> hapusTerpilih());
        HBox barisAksi = new HBox(10, btnEdit, btnHapus);
        barisAksi.setAlignment(Pos.CENTER_RIGHT);

        lblModeForm.getStyleClass().add("judul-seksi");
        VBox isi = new VBox(12, lblModeForm, form, new Separator(),
                UiUtil.judulSeksi("Daftar Event (klik dua kali untuk edit)"), tabel, barisAksi);
        isi.setPadding(new Insets(16));

        ScrollPane scroll = new ScrollPane(isi);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private void siapkanTabel() {
        TableColumn<Event, String> colNama = new TableColumn<>("Nama Event");
        colNama.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNama()));

        TableColumn<Event, String> colKategori = new TableColumn<>("Kategori");
        colKategori.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKategori()));

        TableColumn<Event, LocalDate> colTanggal = new TableColumn<>("Tanggal");
        colTanggal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getTanggal()));
        colTanggal.setCellFactory(k -> new TableCell<Event, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean kosong) {
                super.updateItem(item, kosong);
                setText(kosong || item == null ? null : UiUtil.FORMAT_TANGGAL.format(item));
            }
        });

        TableColumn<Event, String> colWaktu = new TableColumn<>("Waktu");
        colWaktu.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWaktu()));

        TableColumn<Event, String> colKota = new TableColumn<>("Kab./Kota");
        colKota.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKota()));

        TableColumn<Event, String> colHarga = new TableColumn<>("Harga");
        colHarga.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHargaTampil()));

        TableColumn<Event, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        tabel.getColumns().addAll(colNama, colKategori, colTanggal, colWaktu, colKota, colHarga, colStatus);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabel.setPlaceholder(new Label("Belum ada event"));
        tabel.setItems(manager.getDaftarEvent());
        tabel.setPrefHeight(240);
        tabel.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                editTerpilih();
            }
        });
    }

    private void pilihPoster() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih Poster Event");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(view.getScene().getWindow());
        if (file != null) {
            posterBaru = file;
            lblPoster.setText(file.getName());
        }
    }

    private void simpanEvent() {
        String nama = tfNama.getText().trim();
        String kategori = cbKategori.getValue();
        LocalDate tanggal = dpTanggal.getValue();
        String waktuText = tfWaktu.getText().trim();
        String kota = cbKota.getValue();
        String lokasi = tfLokasi.getText().trim();
        String hargaText = tfHarga.getText().trim();
        String penyelenggara = tfPenyelenggara.getText().trim();
        String deskripsi = taDeskripsi.getText().trim();

        if (nama.isEmpty() || kategori == null || tanggal == null || waktuText.isEmpty()
                || kota == null || lokasi.isEmpty() || hargaText.isEmpty()
                || penyelenggara.isEmpty() || deskripsi.isEmpty()) {
            UiUtil.alert(Alert.AlertType.WARNING, "Data Belum Lengkap",
                    "Semua kolom wajib diisi (poster boleh dikosongkan).");
            return;
        }

        String waktu = EventFactory.normalisasiWaktu(waktuText);
        if (waktu == null) {
            UiUtil.alert(Alert.AlertType.ERROR, "Format Waktu Salah",
                    "Gunakan format jam, contoh: 10:00 atau 10:00 - 17:00");
            return;
        }

        long harga;
        try {
            harga = Long.parseLong(hargaText.replaceAll("(?i)rp|[.\\s]", ""));
        } catch (NumberFormatException ex) {
            UiUtil.alert(Alert.AlertType.ERROR, "Format Salah",
                    "Harga tiket harus berupa angka, contoh: 25000");
            return;
        }
        if (harga < 0) {
            UiUtil.alert(Alert.AlertType.ERROR, "Harga Tidak Valid",
                    "Harga tiket tidak boleh negatif.");
            return;
        }

        if (manager.sudahAda(nama, tanggal, sedangDiedit)) {
            UiUtil.alert(Alert.AlertType.WARNING, "Event Sudah Ada",
                    "Event dengan nama dan tanggal yang sama sudah terdaftar.");
            return;
        }

        String poster = posterLama;
        if (posterBaru != null) {
            try {
                poster = manager.salinPoster(posterBaru);
            } catch (IOException ex) {
                UiUtil.alert(Alert.AlertType.ERROR, "Poster Gagal Disimpan", ex.getMessage());
                return;
            }
        }

        Event baru = EventFactory.buat(kategori, nama, tanggal, waktu, kota, lokasi,
                harga, penyelenggara, deskripsi, poster);

        if (sedangDiedit == null) {
            manager.tambahEvent(baru);
            UiUtil.alert(Alert.AlertType.INFORMATION, "Berhasil",
                    "Event \"" + nama + "\" berhasil ditambahkan dan sudah tampil di Jelajahi Event.");
        } else {
            manager.ubahEvent(sedangDiedit, baru);
            UiUtil.alert(Alert.AlertType.INFORMATION, "Berhasil",
                    "Event \"" + nama + "\" berhasil diperbarui.");
        }
        resetForm();
    }

    private void editTerpilih() {
        Event e = tabel.getSelectionModel().getSelectedItem();
        if (e == null) {
            UiUtil.alert(Alert.AlertType.WARNING, "Belum Memilih",
                    "Pilih dulu event yang ingin diedit dari tabel.");
            return;
        }
        sedangDiedit = e;
        posterBaru = null;
        posterLama = e.getPoster();

        tfNama.setText(e.getNama());
        cbKategori.setValue(e.getKategori());
        dpTanggal.setValue(e.getTanggal());
        tfWaktu.setText(e.getWaktu());
        cbKota.setValue(e.getKota());
        tfLokasi.setText(e.getLokasi());
        tfHarga.setText(String.valueOf(e.getHargaTiket()));
        tfPenyelenggara.setText(e.getPenyelenggara());
        taDeskripsi.setText(e.getDeskripsi());
        lblPoster.setText(posterLama.isEmpty() ? "Belum ada poster" : new File(posterLama).getName());

        lblModeForm.setText("Edit Event: " + e.getNama());
        btnSimpan.setText("Simpan Perubahan");
    }

    private void hapusTerpilih() {
        Event e = tabel.getSelectionModel().getSelectedItem();
        if (e == null) {
            UiUtil.alert(Alert.AlertType.WARNING, "Belum Memilih",
                    "Pilih dulu event yang ingin dihapus dari tabel.");
            return;
        }
        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus event \"" + e.getNama() + "\"?", ButtonType.OK, ButtonType.CANCEL);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText(null);
        konfirmasi.getDialogPane().getStylesheets().add(UiUtil.CSS);
        konfirmasi.showAndWait()
                .filter(tombol -> tombol == ButtonType.OK)
                .ifPresent(tombol -> {
                    manager.hapusEvent(e);
                    if (e == sedangDiedit) {
                        resetForm();
                    }
                });
    }

    private void resetForm() {
        sedangDiedit = null;
        posterBaru = null;
        posterLama = "";
        tfNama.clear();
        tfWaktu.clear();
        tfLokasi.clear();
        tfHarga.clear();
        tfPenyelenggara.clear();
        taDeskripsi.clear();
        cbKategori.setValue(null);
        cbKota.setValue(null);
        dpTanggal.setValue(null);
        lblPoster.setText("Belum ada poster");
        lblModeForm.setText("Tambah Event Baru");
        btnSimpan.setText("Simpan Event");
    }

    // ==================== MANAJEMEN KATEGORI ====================

    private Node buatTabKategori() {
        ListView<String> daftar = new ListView<>(manager.getDaftarKategori());
        VBox.setVgrow(daftar, Priority.ALWAYS);

        TextField tfKategori = new TextField();
        tfKategori.setPromptText("Nama kategori baru, contoh: Workshop Jepang");
        HBox.setHgrow(tfKategori, Priority.ALWAYS);
        Button btnTambah = new Button("Tambah Kategori");
        btnTambah.getStyleClass().add("tombol-utama");

        Runnable tambah = () -> {
            if (manager.tambahKategori(tfKategori.getText())) {
                tfKategori.clear();
            } else {
                UiUtil.alert(Alert.AlertType.WARNING, "Kategori Tidak Valid",
                        "Nama kategori kosong atau sudah ada.");
            }
        };
        btnTambah.setOnAction(e -> tambah.run());
        tfKategori.setOnAction(e -> tambah.run());

        Button btnHapus = new Button("Hapus Kategori Terpilih");
        btnHapus.getStyleClass().add("tombol-bahaya");
        btnHapus.setOnAction(e -> {
            String dipilih = daftar.getSelectionModel().getSelectedItem();
            if (dipilih == null) {
                UiUtil.alert(Alert.AlertType.WARNING, "Belum Memilih",
                        "Pilih dulu kategori yang ingin dihapus.");
            } else if (manager.kategoriDipakai(dipilih)) {
                UiUtil.alert(Alert.AlertType.WARNING, "Kategori Masih Dipakai",
                        "Kategori \"" + dipilih + "\" masih dipakai oleh event. "
                                + "Ubah atau hapus event tersebut terlebih dahulu.");
            } else {
                manager.hapusKategori(dipilih);
            }
        });

        VBox isi = new VBox(12, UiUtil.judulSeksi("Manajemen Kategori"),
                new HBox(10, tfKategori, btnTambah), daftar, btnHapus);
        isi.setPadding(new Insets(20));
        isi.setMaxWidth(560);
        return isi;
    }
}
