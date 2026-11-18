package com.mycompany.jevent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Halaman login khusus admin. */
public class LoginView {
    private final StackPane view = new StackPane();

    public LoginView(Admin admin, Runnable berhasil) {
        Label judul = new Label("Login Admin");
        judul.getStyleClass().add("judul-seksi");
        Label ket = new Label("Hanya admin yang dapat menambah, mengubah, dan menghapus event.");
        ket.setWrapText(true);
        ket.getStyleClass().add("info");

        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");
        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");

        Label lblError = new Label();
        lblError.getStyleClass().add("teks-error");

        Button btnMasuk = new Button("Masuk");
        btnMasuk.getStyleClass().add("tombol-utama");
        btnMasuk.setMaxWidth(Double.MAX_VALUE);

        Runnable proses = () -> {
            String user = tfUser.getText().trim();
            String pass = pfPass.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("Username dan password wajib diisi.");
            } else if (admin.verifikasi(user, pass)) {
                berhasil.run();
            } else {
                lblError.setText("Username atau password salah.");
                pfPass.clear();
            }
        };
        btnMasuk.setOnAction(e -> proses.run());
        pfPass.setOnAction(e -> proses.run());
        tfUser.setOnAction(e -> pfPass.requestFocus());

        VBox kartu = new VBox(12, judul, ket, tfUser, pfPass, lblError, btnMasuk);
        kartu.getStyleClass().add("kartu-login");
        kartu.setMaxWidth(340);
        kartu.setPadding(new Insets(24));
        kartu.setAlignment(Pos.CENTER_LEFT);

        view.getChildren().add(kartu);
        view.setPadding(new Insets(30));
    }

    public Node getView() {
        return view;
    }
}
