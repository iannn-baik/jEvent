package com.mycompany.jevent;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Admin admin = new Admin();
        int pilihan = 0;

        while (pilihan != 3) {
            System.out.println("\n=== J-EVENT LAMPUNG ===");
            System.out.println("1. Tambah Event");
            System.out.println("2. Lihat Event");
            System.out.println("3. Keluar");
            System.out.print("Pilih menu: ");
            pilihan = input.nextInt();
            input.nextLine();
            switch (pilihan) {
                case 1:
                    System.out.print("Nama event: ");
                    String nama = input.nextLine();
                    System.out.print("Tanggal: ");
                    String tanggal = input.nextLine();
                    System.out.print("Lokasi: ");
                    String lokasi = input.nextLine();
                    System.out.print("Kategori: ");
                    String kategori = input.nextLine();
                    CosplayEvent event = new CosplayEvent(
                        nama, tanggal, lokasi, kategori
                    );

                    admin.tambahEvent(event);
                    break;
                case 2:
                    admin.tampilkanEvent();
                    break;
                case 3:
                    System.out.println("Program selesai.");
                    break;
                default:
                    System.out.println("Pilihan tidak tersedia.");
            }
        }
        input.close();
    }
}