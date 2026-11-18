package com.mycompany.jevent;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/** Pembantu tampilan yang dipakai banyak layar. */
public final class UiUtil {

    private static final Locale ID = Locale.forLanguageTag("id-ID");
    public static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd MMM yyyy", ID);
    public static final DateTimeFormatter FORMAT_PANJANG = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", ID);
    public static final DateTimeFormatter FORMAT_BULAN = DateTimeFormatter.ofPattern("MMMM yyyy", ID);

    public static final String CSS = UiUtil.class.getResource("style.css").toExternalForm();

    private UiUtil() { }

    public static void alert(Alert.AlertType tipe, String judul, String pesan) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.getDialogPane().getStylesheets().add(CSS);
        alert.showAndWait();
    }

    public static Label judulSeksi(String teks) {
        Label l = new Label(teks);
        l.getStyleClass().add("judul-seksi");
        return l;
    }

    /** Menampilkan poster event; kalau tidak ada, tampil kotak polos berisi kategori. */
    public static Node poster(Event e, double lebar, double tinggi) {
        StackPane kotak = new StackPane();
        kotak.setPrefSize(lebar, tinggi);
        kotak.setMinSize(lebar, tinggi);
        kotak.setMaxSize(lebar, tinggi);
        kotak.getStyleClass().add("poster-kotak");

        File file = (e.getPoster() == null || e.getPoster().isBlank()) ? null : new File(e.getPoster());
        if (file != null && file.isFile()) {
            Image gambar = new Image(file.toURI().toString(), lebar, tinggi, true, true);
            ImageView iv = new ImageView(gambar);
            iv.setFitWidth(lebar);
            iv.setFitHeight(tinggi);
            iv.setPreserveRatio(true);
            kotak.getChildren().add(iv);
        } else {
            Label l = new Label(e.getKategori());
            l.setWrapText(true);
            l.getStyleClass().add("poster-kosong");
            kotak.getChildren().add(l);
        }
        return kotak;
    }

    /** Kartu ringkas event (dipakai di Beranda dan Jelajahi Event). */
    public static VBox kartuEvent(Event e, Consumer<Event> saatDiklik) {
        Label badge = new Label(e.getKategori());
        badge.getStyleClass().add("badge");

        Label nama = new Label(e.getNama());
        nama.setWrapText(true);
        nama.getStyleClass().add("kartu-judul");

        Label waktu = new Label(FORMAT_TANGGAL.format(e.getTanggal()) + " | " + e.getWaktu() + " WIB");
        waktu.setWrapText(true);

        Label tempat = new Label(e.getLokasi() + ", " + e.getKota());
        tempat.setWrapText(true);

        Label harga = new Label(e.getHargaTampil() + (e.isSudahLewat() ? "  |  Selesai" : ""));
        harga.getStyleClass().add("kartu-harga");

        VBox kartu = new VBox(6, poster(e, 200, 120), badge, nama, waktu, tempat, harga);
        kartu.getStyleClass().add("kartu");
        kartu.setPrefWidth(224);
        kartu.setMinWidth(224);
        kartu.setMaxWidth(224);
        kartu.setAlignment(Pos.TOP_LEFT);
        kartu.setOnMouseClicked(ev -> saatDiklik.accept(e));
        return kartu;
    }
}
