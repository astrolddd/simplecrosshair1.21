package com.astrolddd.firstt.client;

public class CrosshairData {

    public static int thickness = 1;
    public static int width = 2;
    public static int length = 2;
    public static boolean attackIndicatorEnabled = false;
    public static int attackIndicatorColor = 0xFFFFFFFF;
    public static int color = 0xFFFFFFFF;

    public static final int GRID_SIZE = 15;
    public static boolean centerVisible = true;
    // 2D grid for editor
    public static boolean[][] grid = new boolean[GRID_SIZE][GRID_SIZE];
    public static boolean[][] pixels = new boolean[GRID_SIZE][GRID_SIZE];
    public static int[][] colors = new int[GRID_SIZE][GRID_SIZE];

    // Clears the drawing board
    public static void clearGrid() {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                grid[x][y] = false;
            }
        }
    }
}