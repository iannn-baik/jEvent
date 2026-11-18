package com.mycompany.jevent;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Jelajahi Event: pencarian + filter kategori, tanggal, dan lokasi. */
public class JelajahView {
    private static final String SEMUA_KATEGORI = "Semua Kategori";
    private static final String SEMUA_LOKASI = "Semua Lokasi";

    private final EventManager manager;
    private final Consumer<Event> bukaDetail;

    private final VBox view = new VBox(10);
    private final TextField tfCari = new TextField();
    private final ComboBox<String> cbKategori = new ComboBox<>();
    private final ComboBox<String> cbKota = new ComboBox<>();
    private final DatePicker dpTanggal = new DatePicker();
    private final CheckBox chkMendatang = new CheckBox("Hanya yang akan datang");
    private final FlowPane hasilPane = new FlowPane(14, 14);
    private final Label lblJumlah = new Label();

    public JelajahView(EventManager manager, Consumer<Event> bukaDetail) {
        this.manager = manager;
        this.bukaDetail = bukaDetail;

        tfCari.setPromptText("Cari nama event, tempat, atau penyelenggara...");
        tfCari.setPrefWidth(300);

        cbKategori.getItems().add(SEMUA_KATEGORI);
        cbKategori.getItems().addAll(manager.getDaftarKategori());
        cbKategori.setValue(SEMUA_KATEGORI);

        cbKota.getItems().add(SEMUA_LOKASI);
        cbKota.getItems().addAll(EventFactory.DAFTAR_KOTA);
        cbKota.setValue(SEMUA_LOKASI);

        dpTanggal.setPromptText("Semua tanggal");

        Button btnReset = new Button("Reset Filter");
        btnReset.setOnAction(e -> {
            tfCari.clear();
            cbKategori.setValue(SEMUA_KATEGORI);
            cbKota.setValue(SEMUA_LOKASI);
            dpTanggal.setValue(null);
            chkMendatang.setSelected(false);
        });

        FlowPane filter = new FlowPane(10, 8,
                new Label("Cari:"), tfCari,
                new Label("Kategori:"), cbKategori,
                new Label("Lokasi:"), cbKota,
                new Label("Tanggal:"), dpTanggal,
                chkMendatang, btnReset);

        ScrollPane scroll = new ScrollPane(hasilPane);
        scroll.setFitToWidth(true);
        hasilPane.setPadding(new Insets(4));
        VBox.setVgrow(scroll, Priority.ALWAYS);

        view.setPadding(new Insets(16));
        view.getChildren().addAll(UiUtil.judulSeksi("Jelajahi Event"), filter, lblJumlah, scroll);

        tfCari.textProperty().addListener((obs, lama, baru) -> tampilkan());
        cbKategori.setOnAction(e -> tampilkan());
        cbKota.setOnAction(e -> tampilkan());
        dpTanggal.valueProperty().addListener((obs, lama, baru) -> tampilkan());
        chkMendatang.setOnAction(e -> tampilkan());

        tampilkan();
    }

    private void tampilkan() {
        String kata = tfCari.getText() == null ? "" : tfCari.getText().trim().toLowerCase();
        String kategori = cbKategori.getValue();
        String kota = cbKota.getValue();
        LocalDate tanggal = dpTanggal.getValue();
        boolean hanyaMendatang = chkMendatang.isSelected();

        List<Event> hasil = manager.getDaftarEvent().stream()
                .filter(e -> kategori == null || kategori.equals(SEMUA_KATEGORI) || e.getKategori().equals(kategori))
                .filter(e -> kota == null || kota.equals(SEMUA_LOKASI) || e.getKota().equals(kota))
                .filter(e -> tanggal == null || e.getTanggal().equals(tanggal))
                .filter(e -> !hanyaMendatang || !e.isSudahLewat())
                .filter(e -> kata.isEmpty()
                        || e.getNama().toLowerCase().contains(kata)
                        || e.getLokasi().toLowerCase().contains(kata)
                        || e.getPenyelenggara().toLowerCase().contains(kata))
                .sorted(Comparator.comparing(Event::getTanggal))
                .collect(Collectors.toList());

        hasilPane.getChildren().clear();
        for (Event e : hasil) {
            hasilPane.getChildren().add(UiUtil.kartuEvent(e, bukaDetail));
        }
        lblJumlah.setText(hasil.size() + " event ditemukan");
    }

    public Node getView() {
        return view;
    }
}
