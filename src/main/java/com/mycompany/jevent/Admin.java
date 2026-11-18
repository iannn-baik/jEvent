package com.mycompany.jevent;

/** Akun admin. Hanya admin yang boleh mengelola data event. */
public class Admin {
    private final String username;
    private final String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean verifikasi(String usernameInput, String passwordInput) {
        return username.equals(usernameInput) && password.equals(passwordInput);
    }

    public String getUsername() {
        return username;
    }
}
