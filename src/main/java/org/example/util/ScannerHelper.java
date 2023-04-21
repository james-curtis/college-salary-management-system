package org.example.util;

import java.util.Scanner;

public class ScannerHelper {

    private static Scanner instance;

    private ScannerHelper() {
    }

    public static synchronized Scanner getInstance() {
        if (ScannerHelper.instance == null) {
            ScannerHelper.instance = new Scanner(System.in);
        }
        return ScannerHelper.instance;
    }


}
