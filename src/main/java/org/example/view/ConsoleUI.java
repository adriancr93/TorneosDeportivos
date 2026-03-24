package org.example.view;

/**
 * Utilidad para formateo visual en consola con bordes, tablas y colores ANSI.
 */
public class ConsoleUI {

    // Colores ANSI
    public static final String RESET   = "\u001B[0m";
    public static final String BOLD    = "\u001B[1m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    // Caracteres de caja
    private static final char TL = '╔'; // top-left
    private static final char TR = '╗'; // top-right
    private static final char BL = '╚'; // bottom-left
    private static final char BR = '╝'; // bottom-right
    private static final char H  = '═'; // horizontal
    private static final char V  = '║'; // vertical
    private static final char ML = '╠'; // middle-left
    private static final char MR = '╣'; // middle-right
    private static final char MT = '╦'; // middle-top
    private static final char MB = '╩'; // middle-bottom
    private static final char MC = '╬'; // middle-cross

    private static final int DEFAULT_WIDTH = 60;

    // ─── Cajas de texto ────────────────────────────

    public static void printHeader(String title) {
        printHeader(title, DEFAULT_WIDTH);
    }

    public static void printHeader(String title, int width) {
        int inner = width - 2;
        System.out.println();
        System.out.println(CYAN + BOLD + TL + repeat(H, inner) + TR + RESET);
        System.out.println(CYAN + BOLD + V + center(title, inner) + V + RESET);
        System.out.println(CYAN + BOLD + BL + repeat(H, inner) + BR + RESET);
    }

    public static void printSubHeader(String title) {
        printSubHeader(title, DEFAULT_WIDTH);
    }

    public static void printSubHeader(String title, int width) {
        int inner = width - 2;
        System.out.println();
        System.out.println(YELLOW + "┌" + repeat('─', inner) + "┐" + RESET);
        System.out.println(YELLOW + "│" + center(title, inner) + "│" + RESET);
        System.out.println(YELLOW + "└" + repeat('─', inner) + "┘" + RESET);
    }

    public static void printSeparator() {
        printSeparator(DEFAULT_WIDTH);
    }

    public static void printSeparator(int width) {
        System.out.println(CYAN + repeat('─', width) + RESET);
    }

    // ─── Menús ─────────────────────────────────────

    public static void printMenuOption(int number, String description) {
        System.out.println(CYAN + "  [" + BOLD + WHITE + String.format("%2d", number) + RESET + CYAN + "] " + RESET + description);
    }

    public static void printMenuExit() {
        System.out.println(RED + "  [ 0] " + RESET + "Salir / Volver");
    }

    // ─── Tablas ────────────────────────────────────

    public static void printTable(String[] headers, String[][] rows) {
        int cols = headers.length;
        int[] widths = new int[cols];

        for (int c = 0; c < cols; c++) {
            widths[c] = headers[c].length();
        }
        for (String[] row : rows) {
            for (int c = 0; c < cols; c++) {
                String val = c < row.length ? row[c] : "";
                widths[c] = Math.max(widths[c], val.length());
            }
        }
        // padding
        for (int c = 0; c < cols; c++) {
            widths[c] += 2;
        }

        // top border
        System.out.println(CYAN + topBorder(widths) + RESET);
        // header row
        System.out.print(CYAN + V + RESET);
        for (int c = 0; c < cols; c++) {
            System.out.print(BOLD + WHITE + center(headers[c], widths[c]) + RESET + CYAN + V + RESET);
        }
        System.out.println();
        // header separator
        System.out.println(CYAN + midBorder(widths) + RESET);
        // data rows
        for (String[] row : rows) {
            System.out.print(CYAN + V + RESET);
            for (int c = 0; c < cols; c++) {
                String val = c < row.length ? row[c] : "";
                System.out.print(padRight(val, widths[c]) + CYAN + V + RESET);
            }
            System.out.println();
        }
        // bottom border
        System.out.println(CYAN + bottomBorder(widths) + RESET);
    }

    // ─── Mensajes ──────────────────────────────────

    public static void printSuccess(String message) {
        System.out.println(GREEN + " ✔ " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + " ✘ " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + " ⚠ " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(BLUE + " ℹ " + message + RESET);
    }

    // ─── Entrada de datos ─────────────────────────

    public static String leerTexto(java.util.Scanner scanner, String label) {
        System.out.print(MAGENTA + " ▸ " + RESET + label);
        return scanner.nextLine();
    }

    public static int leerEntero(java.util.Scanner scanner, String label) {
        System.out.print(MAGENTA + " ▸ " + RESET + label);
        while (!scanner.hasNextInt()) {
            printWarning("Valor inválido. Ingrese un número entero.");
            System.out.print(MAGENTA + " ▸ " + RESET + label);
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    // ─── Detail box (key-value) ────────────────────

    public static void printDetail(String label, String value) {
        System.out.println("   " + BOLD + CYAN + label + ": " + RESET + value);
    }

    // ─── Internal helpers ──────────────────────────

    private static String repeat(char ch, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(ch);
        return sb.toString();
    }

    private static String center(String text, int width) {
        if (text.length() >= width) return text.substring(0, width);
        int left = (width - text.length()) / 2;
        int right = width - text.length() - left;
        return repeat(' ', left) + text + repeat(' ', right);
    }

    private static String padRight(String text, int width) {
        if (text.length() >= width) return text.substring(0, width);
        return " " + text + repeat(' ', width - text.length() - 1);
    }

    private static String topBorder(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append(TL);
        for (int i = 0; i < widths.length; i++) {
            sb.append(repeat(H, widths[i]));
            sb.append(i < widths.length - 1 ? MT : TR);
        }
        return sb.toString();
    }

    private static String midBorder(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append(ML);
        for (int i = 0; i < widths.length; i++) {
            sb.append(repeat(H, widths[i]));
            sb.append(i < widths.length - 1 ? MC : MR);
        }
        return sb.toString();
    }

    private static String bottomBorder(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append(BL);
        for (int i = 0; i < widths.length; i++) {
            sb.append(repeat(H, widths[i]));
            sb.append(i < widths.length - 1 ? MB : BR);
        }
        return sb.toString();
    }
}
