package com.astrolddd.firstt.client;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.Click;

public class CrosshairScreen extends Screen {

    private final int gridSize = 15;
    private int pixelSize;

    protected CrosshairScreen() {
        super(Text.literal("Crosshair Editor"));
    }

    @Override
    protected void init() {

        // ✅ Smaller clean grid like before
        pixelSize = 10;

        int totalSize = gridSize * pixelSize;

        int gridX = width / 2 - totalSize / 2;
        int gridY = height / 2 - totalSize / 2;

        int leftPanelX = gridX - 180;
        int rightPanelX = gridX + totalSize + 25;

        int sliderWidth = 150;
        addDrawableChild(createColorSlider(leftPanelX, gridY + 100, "Opacity", 24));
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
    private SliderWidget createSlider(int x, int y, String name, int initial, java.util.function.IntConsumer setter) {
        return new SliderWidget(x, y, 150, 20, Text.literal(name), initial / 10.0) {
            @Override
            protected void updateMessage() {
                int v = (int) (value * 10);
                setMessage(Text.literal(name + ": " + v));
            }

            @Override
            protected void applyValue() {
                setter.accept((int) (value * 10));
            }
        };
    }

    private SliderWidget createColorSlider(int x, int y, String name, int shift) {
        double initial = ((CrosshairData.color >> shift) & 255) / 255.0;

        return new SliderWidget(x, y, 150, 20, Text.literal(name), initial) {
            protected void updateMessage() {
                int v = (int) (value * 255);
                setMessage(Text.literal(name + ": " + v));
            }

            protected void applyValue() {
                int a = (CrosshairData.color >> 24) & 255;
                int r = (CrosshairData.color >> 16) & 255;
                int g = (CrosshairData.color >> 8) & 255;
                int b = CrosshairData.color & 255;

                int nv = (int) (value * 255);

                if (shift == 24) a = nv;
                if (shift == 16) r = nv;
                if (shift == 8) g = nv;
                if (shift == 0) b = nv;

                CrosshairData.color = (a << 24) | (r << 16) | (g << 8) | b;
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

        int startX = width / 2 - totalSize / 2;
        int startY = height / 2 - totalSize / 2;

        // background panel
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
                if (CrosshairData.grid[x][y]) {
                    color = CrosshairData.color; // The color the user picked
                } else {
                    color = 0xFF333333; // Dark gray for "off" pixels
                }

                // Draw the base pixel
                context.fill(drawX, drawY, drawX + pixelSize - 1, drawY + pixelSize - 1, color);

                // Draw the red reference mark ONLY if it's the center
                if (x == center && y == center && CrosshairData.centerVisible) {
                    // Draw a thin red outline or a small dot in the middle
                    // This stays visible even if the pixel is "off"
                    context.fill(drawX + 2, drawY + 2, drawX + pixelSize - 3, drawY + pixelSize - 3, 0xAAFF0000);
                }
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
    public boolean mouseClicked(Click click, boolean handled) {
        // 1. Call super and store the result
        boolean parentHandled = super.mouseClicked(click, handled);

        // 2. If a button or slider already handled it, we return true immediately
        if (parentHandled || handled) return true;

        // 3. Use fields .x and .y from the Click object
        double mouseX = click.x();
        double mouseY = click.y();

        int totalSize = gridSize * pixelSize;
        int startX = this.width / 2 - totalSize / 2;
        int startY = this.height / 2 - totalSize / 2;

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                int drawX = startX + x * pixelSize;
                int drawY = startY + y * pixelSize;

                if (mouseX >= drawX && mouseX < drawX + pixelSize &&
                        mouseY >= drawY && mouseY < drawY + pixelSize) {

                    int center = gridSize / 2;


                        CrosshairData.grid[x][y] = !CrosshairData.grid[x][y];


                    // 4. Return true because we handled the click on our grid
                    return true;
                }
            }
        }

        // 5. Return false if nothing was clicked
        return false;


    }
}