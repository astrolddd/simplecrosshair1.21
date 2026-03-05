package com.astrolddd.firstt.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CrosshairRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            render(drawContext);
        });
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int centerX = client.getWindow().getScaledWidth() / 2;
        int centerY = client.getWindow().getScaledHeight() / 2;

        int thickness = CrosshairData.thickness;
        int widthScale = CrosshairData.width;
        int lengthScale = CrosshairData.length;

        for (int x = 0; x < CrosshairData.GRID_SIZE; x++) {
            for (int y = 0; y < CrosshairData.GRID_SIZE; y++) {

                if (CrosshairData.grid[x][y]) {

                    int offsetX = (x - CrosshairData.GRID_SIZE / 2) * thickness * widthScale;
                    int offsetY = (y - CrosshairData.GRID_SIZE / 2) * thickness * lengthScale;

                    int drawX = centerX + offsetX;
                    int drawY = centerY + offsetY;

                    context.fill(
                            drawX,
                            drawY,
                            drawX + thickness,
                            drawY + thickness,
                            CrosshairData.color
                    );
                }
            }
        }
        int gridSize = CrosshairData.GRID_SIZE;
        int center = gridSize / 2;

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {

                if (x == center && y == center) {
                    // draw highlighted center pixel
                }

                if (CrosshairData.pixels[x][y]) {
                    int color = CrosshairData.colors[x][y];
                    // draw the pixel
                }
            }
        }
    }
}