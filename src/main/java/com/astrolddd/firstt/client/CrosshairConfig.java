package com.astrolddd.firstt.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class CrosshairConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            "firstt_crosshair.json"
    );

    public int thickness = 2;
    public int width = 2;
    public int length = 2;
    public int color = 0xFFFFFFFF;
    public boolean[][] pixels = new boolean[CrosshairData.GRID_SIZE][CrosshairData.GRID_SIZE];
    public boolean attackIndicatorEnabled = false;
    public int attackIndicatorColor = 0xFFFFFFFF;
    public static void save() {
        try {
            CrosshairConfig config = new CrosshairConfig();

            config.thickness = CrosshairData.thickness;
            config.width = CrosshairData.width;
            config.length = CrosshairData.length;
            config.color = CrosshairData.color;
            config.pixels = CrosshairData.grid;
            config.attackIndicatorEnabled = CrosshairData.attackIndicatorEnabled;
            config.attackIndicatorColor = CrosshairData.attackIndicatorColor;
            FileWriter writer = new FileWriter(FILE);
            GSON.toJson(config, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            if (!FILE.exists()) return;

            FileReader reader = new FileReader(FILE);
            CrosshairConfig config = GSON.fromJson(reader, CrosshairConfig.class);
            reader.close();
            CrosshairData.attackIndicatorEnabled = config.attackIndicatorEnabled;
            CrosshairData.attackIndicatorColor = config.attackIndicatorColor;
            CrosshairData.thickness = config.thickness;
            CrosshairData.width = config.width;
            CrosshairData.length = config.length;
            CrosshairData.color = config.color;
            CrosshairData.grid = config.pixels;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}