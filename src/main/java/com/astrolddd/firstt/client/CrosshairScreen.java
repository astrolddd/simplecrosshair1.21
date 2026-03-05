package com.astrolddd.firstt.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class CrosshairScreen extends Screen {

    private final int gridSize = 15;
    private final int pixelSize = 14;

    protected CrosshairScreen() {
        super(Text.literal("Crosshair Editor"));
    }

    @Override
    protected void init() {

        int totalSize = gridSize * pixelSize;
        int startX = (width - totalSize) / 2;
        int startY = (height - totalSize) / 2;

        int rightX = startX + totalSize + 30;
        int sliderWidth = 150;

        // THICKNESS
        addDrawableChild(new SliderWidget(
                rightX, startY, sliderWidth, 20,
                Text.literal("Thickness"),
                CrosshairData.thickness / 10.0
        ) {
            protected void updateMessage() {
                int v = (int)(value * 10);
                setMessage(Text.literal("Thickness: " + v));
            }
            protected void applyValue() {
                CrosshairData.thickness = (int)(value * 10);
            }
        });

        // WIDTH
        addDrawableChild(new SliderWidget(
                rightX, startY + 30, sliderWidth, 20,
                Text.literal("Width"),
                CrosshairData.width / 10.0
        ) {
            protected void updateMessage() {
                int v = (int)(value * 10);
                setMessage(Text.literal("Width: " + v));
            }
            protected void applyValue() {
                CrosshairData.width = (int)(value * 10);
            }
        });

        // LENGTH
        addDrawableChild(new SliderWidget(
                rightX, startY + 60, sliderWidth, 20,
                Text.literal("Length"),
                CrosshairData.length / 10.0
        ) {
            protected void updateMessage() {
                int v = (int)(value * 10);
                setMessage(Text.literal("Length: " + v));
            }
            protected void applyValue() {
                CrosshairData.length = (int)(value * 10);
            }
        });

        // CLEAR
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Clear"),
                b -> CrosshairData.clearGrid()
        ).dimensions(rightX, startY + 100, sliderWidth, 20).build());

        // SAVE
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Save"),
                b -> CrosshairConfig.save()
        ).dimensions(rightX, startY + 130, sliderWidth, 20).build());

        // RGB BELOW GRID
        int centerX = width / 2;
        int rgbY = startY + totalSize + 30;

        addDrawableChild(createColorSlider(centerX - 160, rgbY, "Red", 16));
        addDrawableChild(createColorSlider(centerX + 10, rgbY, "Green", 8));
        addDrawableChild(createColorSlider(centerX - 75, rgbY + 30, "Blue", 0));
    }

    private SliderWidget createColorSlider(int x, int y, String name, int shift) {

        double initial = ((CrosshairData.color >> shift) & 255) / 255.0;

        return new SliderWidget(x, y, 150, 20,
                Text.literal(name), initial) {

            protected void updateMessage() {
                int v = (int)(value * 255);
                setMessage(Text.literal(name + ": " + v));
            }

            protected void applyValue() {

                int r = (CrosshairData.color >> 16) & 255;
                int g = (CrosshairData.color >> 8) & 255;
                int b = CrosshairData.color & 255;

                int nv = (int)(value * 255);

                if (shift == 16) r = nv;
                if (shift == 8) g = nv;
                if (shift == 0) b = nv;

                CrosshairData.color =
                        (255 << 24) | (r << 16) | (g << 8) | b;
            }
        };
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xCC000000);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int totalSize = gridSize * pixelSize;
        int startX = (width - totalSize) / 2;
        int startY = (height - totalSize) / 2;

        // PANEL
        context.fill(
                startX - 12,
                startY - 12,
                startX + totalSize + 12,
                startY + totalSize + 12,
                0xCC111111
        );

        // GRID
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {

                int drawX = startX + x * pixelSize;
                int drawY = startY + y * pixelSize;

                int center = gridSize / 2;

                int color;
                if (CrosshairData.grid[x][y]) {
                    color = CrosshairData.color;
                } else {
                    color = 0xFF333333;
                }

                context.fill(drawX, drawY,
                        drawX + pixelSize - 1,
                        drawY + pixelSize - 1,
                        color);
            }
        }
        int center = CrosshairData.GRID_SIZE / 2;

        if (CrosshairData.centerVisible) {



            int cx = startX + center * pixelSize;
            int cy = startY + center * pixelSize;

            int translucentRed = 0x55FF0000;

            context.fill(
                    cx,
                    cy,
                    cx + pixelSize - 1,
                    cy + pixelSize - 1,
                    translucentRed
            );
        }

        // COLOR PREVIEW
        int previewX = startX + totalSize + 100;
        int previewY = startY + totalSize + 50;

        context.fill(previewX - 2, previewY - 2,
                previewX + 32, previewY + 32,
                0xFF000000);

        context.fill(previewX, previewY,
                previewX + 30, previewY + 30,
                CrosshairData.color);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        int totalSize = gridSize * pixelSize;
        int startX = (width - totalSize) / 2;
        int startY = (height - totalSize) / 2;

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {

                int drawX = startX + x * pixelSize;
                int drawY = startY + y * pixelSize;

                if (mouseX >= drawX && mouseX < drawX + pixelSize &&
                        mouseY >= drawY && mouseY < drawY + pixelSize) {
                    int center = CrosshairData.GRID_SIZE / 2;

                    if (x == center && y == center && CrosshairData.centerVisible) {
                        CrosshairData.centerVisible = false;
                        return true;
                    }
                    CrosshairData.grid[x][y] =
                            !CrosshairData.grid[x][y];

                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}