package com.mycompany.jevent;

import java.util.ArrayList;

public class Admin {
    private ArrayList<Event> daftarEvent = new ArrayList<>();

    public void tambahEvent(Event event) {
        daftarEvent.add(event);
        System.out.println("Event berhasil ditambahkan!");
    }

    public void tampilkanEvent() {
        if (daftarEvent.isEmpty()) {
            System.out.println("Belum ada event.");
        } else {
            for (Event event : daftarEvent) {
                event.tampilkanInfo();
                System.out.println("----------------");
            }
        }
    }
}