package com.mycompany.jevent;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/** Beranda: event akan datang, populer, dan terbaru. */
public class BerandaView {
    private final ScrollPane view = new ScrollPane();

    public BerandaView(EventManager manager, Consumer<Event> bukaDetail, Runnable keJelajahi) {
        Label judul = new Label("Temukan Event Jejepangan di Lampung");
        judul.getStyleClass().add("banner-judul");
        Label sub = new Label("Cosplay, anime gathering, festival budaya Jepang, dan kompetisi cosplay "
                + "dalam satu tempat.");
        sub.getStyleClass().add("banner-sub");
        sub.setWrapText(true);
        Button btn = new Button("Jelajahi Event");
        btn.getStyleClass().add("tombol-utama");
        btn.setOnAction(e -> keJelajahi.run());
        VBox banner = new VBox(8, judul, sub, btn);
        banner.getStyleClass().add("banner");

        VBox isi = new VBox(22, banner,
                seksi("Event Akan Datang", manager.getMendatang(4), bukaDetail),
                seksi("Event Populer (paling sering dilihat)", manager.getPopuler(4), bukaDetail),
                seksi("Baru Ditambahkan", manager.getTerbaru(4), bukaDetail));
        isi.setPadding(new Insets(20));

        view.setContent(isi);
        view.setFitToWidth(true);
    }

    private VBox seksi(String judul, List<Event> data, Consumer<Event> bukaDetail) {
        VBox kotak = new VBox(10, UiUtil.judulSeksi(judul));
        if (data.isEmpty()) {
            kotak.getChildren().add(new Label("Belum ada event."));
        } else {
            FlowPane flow = new FlowPane(14, 14);
            for (Event e : data) {
                flow.getChildren().add(UiUtil.kartuEvent(e, bukaDetail));
            }
            kotak.getChildren().add(flow);
        }
        return kotak;
    }

    public Node getView() {
        return view;
    }
}
