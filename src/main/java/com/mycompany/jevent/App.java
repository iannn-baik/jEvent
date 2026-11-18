package com.mycompany.jevent;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private final EventManager manager = new EventManager();

    private final Admin admin = new Admin("aku admin kau member", "admin123");

    private final BorderPane root = new BorderPane();
    private final ToggleGroup grupNav = new ToggleGroup();
    private final ToggleButton btnBeranda = nav("Beranda");
    private final ToggleButton btnJelajahi = nav("Jelajahi Event");
    private final ToggleButton btnKalender = nav("Kalender Event");
    private final ToggleButton btnAdmin = nav("Admin");

    private boolean adminMasuk = false;

    @Override
    public void start(Stage stage) {
        Label judul = new Label("J-EVENT LAMPUNG");
        judul.getStyleClass().add("judul");
        Label sub = new Label("Pusat Informasi Jadwal Event Jejepangan");
        sub.getStyleClass().add("subjudul");
        VBox teks = new VBox(2, judul, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(14, teks, spacer, btnBeranda, btnJelajahi, btnKalender, btnAdmin);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(14, 20, 14, 20));

        // Satu tombol menu harus selalu terpilih
        grupNav.selectedToggleProperty().addListener((obs, lama, baru) -> {
            if (baru == null && lama != null) {
                lama.setSelected(true);
            }
        });
        btnBeranda.setOnAction(e -> tampilBeranda());
        btnJelajahi.setOnAction(e -> tampilJelajahi());
        btnKalender.setOnAction(e -> tampilKalender());
        btnAdmin.setOnAction(e -> tampilAdmin());

        root.setTop(header);
        btnBeranda.setSelected(true);
        tampilBeranda();

        Scene scene = new Scene(root, 1080, 720);
        scene.getStylesheets().add(UiUtil.CSS);
        stage.setTitle("J-EVENT Lampung");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    private ToggleButton nav(String teks) {
        ToggleButton b = new ToggleButton(teks);
        b.setToggleGroup(grupNav);
        b.getStyleClass().add("nav-btn");
        return b;
    }

    private void bukaDetail(Event e) {
        DetailEventDialog.tampil(e, manager);
    }

    private void tampilBeranda() {
        root.setCenter(new BerandaView(manager, this::bukaDetail, () -> {
            btnJelajahi.setSelected(true);
            tampilJelajahi();
        }).getView());
    }

    private void tampilJelajahi() {
        root.setCenter(new JelajahView(manager, this::bukaDetail).getView());
    }

    private void tampilKalender() {
        root.setCenter(new KalenderView(manager, this::bukaDetail).getView());
    }

    private void tampilAdmin() {
        if (!adminMasuk) {
            root.setCenter(new LoginView(admin, () -> {
                adminMasuk = true;
                tampilAdmin();
            }).getView());
        } else {
            root.setCenter(new AdminView(manager, admin, () -> {
                adminMasuk = false;
                tampilAdmin();
            }).getView());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
