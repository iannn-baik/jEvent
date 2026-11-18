package com.mycompany.jevent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Consumer;

/** Kalender Event: jadwal kegiatan per tanggal. */
public class KalenderView {
    private static final String[] NAMA_HARI = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

    private final EventManager manager;
    private final Consumer<Event> bukaDetail;

    private final BorderPane view = new BorderPane();
    private final GridPane grid = new GridPane();
    private final Label lblBulan = new Label();
    private final Label lblJudulHari = new Label();
    private final ListView<Event> listHari = new ListView<>();

    private YearMonth bulan = YearMonth.now();
    private LocalDate terpilih = LocalDate.now();

    public KalenderView(EventManager manager, Consumer<Event> bukaDetail) {
        this.manager = manager;
        this.bukaDetail = bukaDetail;

        // ----- grid kalender -----
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            grid.getColumnConstraints().add(cc);
        }
        grid.getRowConstraints().add(new RowConstraints());
        for (int i = 0; i < 6; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(72);
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }
        grid.setHgap(4);
        grid.setVgap(4);

        // ----- bar navigasi bulan -----
        Button btnSebelum = new Button("<");
        Button btnSesudah = new Button(">");
        Button btnHariIni = new Button("Hari Ini");
        lblBulan.getStyleClass().add("judul-seksi");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(10, btnSebelum, lblBulan, btnSesudah, spacer, btnHariIni);
        bar.setAlignment(Pos.CENTER_LEFT);

        btnSebelum.setOnAction(e -> pindahBulan(bulan.minusMonths(1)));
        btnSesudah.setOnAction(e -> pindahBulan(bulan.plusMonths(1)));
        btnHariIni.setOnAction(e -> {
            bulan = YearMonth.now();
            terpilih = LocalDate.now();
            gambarKalender();
        });

        VBox kiri = new VBox(10, bar, grid);
        VBox.setVgrow(grid, Priority.ALWAYS);
        kiri.setPadding(new Insets(16));

        // ----- panel event pada tanggal terpilih -----
        lblJudulHari.getStyleClass().add("judul-seksi");
        lblJudulHari.setWrapText(true);
        listHari.setPlaceholder(new Label("Tidak ada event pada tanggal ini"));
        listHari.setCellFactory(lv -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event e, boolean kosong) {
                super.updateItem(e, kosong);
                if (kosong || e == null) {
                    setText(null);
                } else {
                    setText(e.getNama() + "\n" + e.getWaktu() + " WIB | " + e.getKota());
                }
            }
        });
        listHari.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Event e = listHari.getSelectionModel().getSelectedItem();
                if (e != null) {
                    bukaDetail.accept(e);
                }
            }
        });
        Label petunjuk = new Label("Klik dua kali pada event untuk melihat detail.");
        petunjuk.getStyleClass().add("info");
        petunjuk.setWrapText(true);

        VBox kanan = new VBox(10, lblJudulHari, listHari, petunjuk);
        VBox.setVgrow(listHari, Priority.ALWAYS);
        kanan.setPadding(new Insets(16));
        kanan.setPrefWidth(290);

        view.setCenter(kiri);
        view.setRight(kanan);
        gambarKalender();
    }

    private void pindahBulan(YearMonth baru) {
        bulan = baru;
        terpilih = bulan.equals(YearMonth.now()) ? LocalDate.now() : bulan.atDay(1);
        gambarKalender();
    }

    private void gambarKalender() {
        grid.getChildren().clear();
        String teksBulan = UiUtil.FORMAT_BULAN.format(bulan);
        lblBulan.setText(teksBulan.substring(0, 1).toUpperCase() + teksBulan.substring(1));

        for (int i = 0; i < 7; i++) {
            Label h = new Label(NAMA_HARI[i]);
            h.getStyleClass().add("kal-header");
            h.setMaxWidth(Double.MAX_VALUE);
            h.setAlignment(Pos.CENTER);
            grid.add(h, i, 0);
        }

        int offset = bulan.atDay(1).getDayOfWeek().getValue() - 1; // Senin = 0
        for (int d = 1; d <= bulan.lengthOfMonth(); d++) {
            int idx = offset + d - 1;
            grid.add(buatSel(bulan.atDay(d)), idx % 7, idx / 7 + 1);
        }
        tampilkanHari();
    }

    private VBox buatSel(LocalDate tanggal) {
        List<Event> acara = manager.getPadaTanggal(tanggal);

        VBox sel = new VBox(2);
        sel.getStyleClass().add("sel-kalender");
        if (!acara.isEmpty()) {
            sel.getStyleClass().add("sel-ada-event");
        }
        if (tanggal.equals(LocalDate.now())) {
            sel.getStyleClass().add("sel-hari-ini");
        }
        if (tanggal.equals(terpilih)) {
            sel.getStyleClass().add("sel-terpilih");
        }

        Label angka = new Label(String.valueOf(tanggal.getDayOfMonth()));
        angka.getStyleClass().add("sel-angka");
        sel.getChildren().add(angka);

        for (int i = 0; i < Math.min(2, acara.size()); i++) {
            Label l = new Label(acara.get(i).getNama());
            l.getStyleClass().add("sel-event");
            l.setMinWidth(0);
            l.setMaxWidth(Double.MAX_VALUE);
            sel.getChildren().add(l);
        }
        if (acara.size() > 2) {
            Label lagi = new Label("+" + (acara.size() - 2) + " lagi");
            lagi.getStyleClass().add("sel-event");
            sel.getChildren().add(lagi);
        }

        sel.setOnMouseClicked(e -> {
            terpilih = tanggal;
            gambarKalender();
        });
        return sel;
    }

    private void tampilkanHari() {
        lblJudulHari.setText("Event pada " + UiUtil.FORMAT_PANJANG.format(terpilih));
        listHari.getItems().setAll(manager.getPadaTanggal(terpilih));
    }

    public Node getView() {
        return view;
    }
}
