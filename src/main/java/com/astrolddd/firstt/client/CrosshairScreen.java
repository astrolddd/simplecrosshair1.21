package com.astrolddd.firstt.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class CrosshairScreen extends Screen {

    private final int gridSize = 15;
    private int pixelSize;

    protected CrosshairScreen() {
        super(Text.literal("Crosshair Editor"));
    }

    @Override
    protected void init() {

        pixelSize = Math.max(8, width / 160);

        int totalSize = gridSize * pixelSize;

        int gridX = (width - totalSize) / 2;
        int gridY = (height - totalSize) / 2;

        int leftPanelX = gridX - 180;
        int rightPanelX = gridX + totalSize + 25;

        int sliderWidth = 150;

        // RGB PANEL (LEFT)

        addDrawableChild(createColorSlider(leftPanelX, gridY + 10, "Red", 16));
        addDrawableChild(createColorSlider(leftPanelX, gridY + 40, "Green", 8));
        addDrawableChild(createColorSlider(leftPanelX, gridY + 70, "Blue", 0));

        // RIGHT PANEL

        addDrawableChild(createSlider(
                rightPanelX,
                gridY,
                "Thickness",
                CrosshairData.thickness,
                v -> CrosshairData.thickness = v
        ));

        addDrawableChild(createSlider(
                rightPanelX,
                gridY + 30,
                "Width",
                CrosshairData.width,
                v -> CrosshairData.width = v
        ));

        addDrawableChild(createSlider(
                rightPanelX,
                gridY + 60,
                "Length",
                CrosshairData.length,
                v -> CrosshairData.length = v
        ));

        // ATTACK INDICATOR

        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Attack Indicator: " +
                                (CrosshairData.attackIndicatorEnabled ? "ON" : "OFF")),
                        button -> {

                            CrosshairData.attackIndicatorEnabled =
                                    !CrosshairData.attackIndicatorEnabled;

                            button.setMessage(Text.literal(
                                    "Attack Indicator: " +
                                            (CrosshairData.attackIndicatorEnabled ? "ON" : "OFF")
                            ));
                        })
                .dimensions(gridX + totalSize / 2 - 70, gridY - 30, 140, 20)
                .build());

        // CLEAR

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Clear"),
                b -> CrosshairData.clearGrid()
        ).dimensions(rightPanelX, gridY + 110, sliderWidth, 20).build());

        // SAVE

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Save"),
                b -> CrosshairConfig.save()
        ).dimensions(rightPanelX, gridY + 140, sliderWidth, 20).build());
    }

    private SliderWidget createSlider(int x, int y, String name, int initial,
                                      java.util.function.IntConsumer setter) {

        return new SliderWidget(
                x, y, 150, 20,
                Text.literal(name),
                initial / 10.0
        ) {

            protected void updateMessage() {
                int v = (int) (value * 10);
                setMessage(Text.literal(name + ": " + v));
            }

            protected void applyValue() {
                setter.accept((int) (value * 10));
            }
        };
    }

    private SliderWidget createColorSlider(int x, int y, String name, int shift) {

        double initial = ((CrosshairData.color >> shift) & 255) / 255.0;

        return new SliderWidget(x, y, 150, 20,
                Text.literal(name), initial) {

            protected void updateMessage() {
                int v = (int) (value * 255);
                setMessage(Text.literal(name + ": " + v));
            }

            protected void applyValue() {

                int r = (CrosshairData.color >> 16) & 255;
                int g = (CrosshairData.color >> 8) & 255;
                int b = CrosshairData.color & 255;

                int nv = (int) (value * 255);

                if (shift == 16) r = nv;
                if (shift == 8) g = nv;
                if (shift == 0) b = nv;

                int selectedColor =
                        (255 << 24) | (r << 16) | (g << 8) | b;

                CrosshairData.color = selectedColor;
                CrosshairData.attackIndicatorColor = selectedColor;
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

        context.fill(
                startX - 12,
                startY - 12,
                startX + totalSize + 12,
                startY + totalSize + 12,
                0xCC111111
        );

        int center = gridSize / 2;

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {

                int drawX = startX + x * pixelSize;
                int drawY = startY + y * pixelSize;

                int color;

                if (x == center && y == center && CrosshairData.centerVisible) {
                    color = 0x55FF0000;
                }
                else if (CrosshairData.grid[x][y]) {
                    color = CrosshairData.color;
                }
                else {
                    color = 0xFF333333;
                }

                context.fill(drawX, drawY,
                        drawX + pixelSize - 1,
                        drawY + pixelSize - 1,
                        color);

                context.drawBorder(drawX, drawY, pixelSize, pixelSize, 0xFF000000);
            }
        }

        // PREVIEW

        int previewX = startX + totalSize / 2 - 15;
        int previewY = startY + totalSize + 25;

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

                    int center = gridSize / 2;

                    if (x == center && y == center) {
                        CrosshairData.centerVisible =
                                !CrosshairData.centerVisible;
                    }
                    else {
                        CrosshairData.grid[x][y] =
                                !CrosshairData.grid[x][y];
                    }

                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}