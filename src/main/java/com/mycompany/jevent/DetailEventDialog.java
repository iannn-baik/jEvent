package com.mycompany.jevent;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Jendela detail event: poster, nama, tanggal, waktu, lokasi, deskripsi, penyelenggara. */
public final class DetailEventDialog {

    private DetailEventDialog() { }

    public static void tampil(Event e, EventManager manager) {
        manager.catatDilihat(e);

        Label badge = new Label(e.getKategori());
        badge.getStyleClass().add("badge");

        Label nama = new Label(e.getNama());
        nama.setWrapText(true);
        nama.getStyleClass().add("detail-judul");

        GridPane info = new GridPane();
        info.setHgap(14);
        info.setVgap(8);
        baris(info, 0, "Tanggal", UiUtil.FORMAT_PANJANG.format(e.getTanggal()));
        baris(info, 1, "Waktu", e.getWaktu() + " WIB");
        baris(info, 2, "Lokasi", e.getLokasi() + ", " + e.getKota());
        baris(info, 3, "Harga Tiket", e.getHargaTampil());
        baris(info, 4, "Penyelenggara", e.getPenyelenggara());
        baris(info, 5, "Status", e.getStatus());

        Label judulDeskripsi = new Label("Deskripsi");
        judulDeskripsi.getStyleClass().add("info-label");
        Label deskripsi = new Label(e.getDeskripsi());
        deskripsi.setWrapText(true);
        deskripsi.setMaxWidth(380);

        VBox kanan = new VBox(10, badge, nama, info, judulDeskripsi, deskripsi);
        kanan.setPrefWidth(400);

        HBox isi = new HBox(20, UiUtil.poster(e, 260, 340), kanan);
        isi.setPadding(new Insets(10));

        ScrollPane scroll = new ScrollPane(isi);
        scroll.setPrefViewportWidth(720);
        scroll.setPrefViewportHeight(400);
        scroll.setFitToWidth(true);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detail Event");
        dialog.getDialogPane().setContent(scroll);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().getStylesheets().add(UiUtil.CSS);
        dialog.showAndWait();
    }

    private static void baris(GridPane grid, int baris, String label, String nilai) {
        Label l = new Label(label);
        l.getStyleClass().add("info-label");
        Label v = new Label(nilai);
        v.setWrapText(true);
        v.setMaxWidth(270);
        grid.add(l, 0, baris);
        grid.add(v, 1, baris);
    }
}
