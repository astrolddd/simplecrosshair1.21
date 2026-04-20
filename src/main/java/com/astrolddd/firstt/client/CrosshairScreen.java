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
        super.init();

        int centerX = width / 2;
        int centerY = height / 2;

        // Increased the spacing here to fix the overlap
        int leftPanelX = centerX - 220; // Pushed further left
        int rightPanelX = centerX + 70; // Pushed further right
        int startY = centerY - 80;

        int sliderWidth = 150;
        int spacing = 24;

        // --- LEFT PANEL: COLORS ---
        addDrawableChild(createColorSlider(leftPanelX, startY, "Red", 16));
        addDrawableChild(createColorSlider(leftPanelX, startY + spacing, "Green", 8));
        addDrawableChild(createColorSlider(leftPanelX, startY + (spacing * 2), "Blue", 0));
        addDrawableChild(createColorSlider(leftPanelX, startY + (spacing * 3), "Opacity", 24));

        // --- RIGHT PANEL: SIZES (0-100 Precision) ---
        // Inside your init() method
        addDrawableChild(createScaleSlider(rightPanelX, startY, "Thickness", CrosshairData.thickness, v -> CrosshairData.thickness = v));
        addDrawableChild(createScaleSlider(rightPanelX, startY + spacing, "Width", CrosshairData.width, v -> CrosshairData.width = v));
        addDrawableChild(createScaleSlider(rightPanelX, startY + (spacing * 2), "Length", CrosshairData.length, v -> CrosshairData.length = v));
        // --- BUTTONS ---
        // Using clearGrid() to match your Data class
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), b -> CrosshairData.clearGrid())
                .dimensions(rightPanelX, startY + 85, sliderWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> CrosshairConfig.save())
                .dimensions(rightPanelX, startY + 109, sliderWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Attack Indicator: " + (CrosshairData.reachTintEnabled ? "ON" : "OFF")),
                button -> {
                    CrosshairData.reachTintEnabled = !CrosshairData.reachTintEnabled;
                    button.setMessage(Text.literal("Attack Indicator: " + (CrosshairData.reachTintEnabled ? "ON" : "OFF")));
                }
        ).dimensions(rightPanelX, startY + 133, sliderWidth, 20).build());
    }
    private SliderWidget createScaleSlider(int x, int y, String name, int initial, java.util.function.IntConsumer setter) {
        // initial comes from CrosshairData (0-10), we divide by 10.0 for the 0.0-1.0 slider range
        return new SliderWidget(x, y, 150, 20, Text.literal(name), initial / 10.0) {
            @Override
            protected void updateMessage() {
                int v = Math.max(1, (int) (value * 10));
                setMessage(Text.literal(name + ": " + v));
            }

            @Override
            protected void applyValue() {
                // This saves the 0-10 value directly back to your data
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

        // 1. Use the GRID_SIZE from your Data class
        int gridSize = CrosshairData.GRID_SIZE;
        int editorPixelSize = 8; // Size of the boxes in the editor
        int totalSize = gridSize * editorPixelSize;

        int startX = width / 2 - totalSize / 2;
        int startY = height / 2 - totalSize / 2;

        // 2. Background Panel (The dark box behind the grid)
        context.fill(
                startX - 6,
                startY - 6,
                startX + totalSize + 6,
                startY + totalSize + 6,
                0xCC111111
        );

        int center = gridSize / 2;

        // 3. Draw the Interactive Grid
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                int drawX = startX + x * editorPixelSize;
                int drawY = startY + y * editorPixelSize;

                int color;
                // Check the grid boolean array you provided
                if (CrosshairData.grid[x][y]) {
                    color = CrosshairData.color;
                } else {
                    color = 0xFF333333; // Dark gray for empty slots
                }

                // Draw the pixel box
                context.fill(drawX, drawY, drawX + editorPixelSize - 1, drawY + editorPixelSize - 1, color);

                // Red reference mark for the exact center
                if (x == center && y == center && CrosshairData.centerVisible) {
                    context.fill(drawX + 3, drawY + 3, drawX + editorPixelSize - 3, drawY + editorPixelSize - 3, 0xAAFF0000);
                }
            }
        }

        // 4. PREVIEW BOX (The big square at the bottom)
        int previewX = width / 2 - 15;
        int previewY = startY + totalSize + 20;

        // Black border
        context.fill(previewX - 2, previewY - 2, previewX + 32, previewY + 32, 0xFF000000);
        // Color preview
        context.fill(previewX, previewY, previewX + 30, previewY + 30, CrosshairData.color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 1. Correct the supercall to 2 arguments as required by your IDE
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 2. Fixed math (don't use the 'pixelSize' variable if it's 0)
        int editorPixelSize = 8;
        int totalSize = CrosshairData.GRID_SIZE * editorPixelSize;
        int startX = width / 2 - totalSize / 2;
        int startY = height / 2 - totalSize / 2;

        // 3. Grid boundary check
        if (mouseX >= startX && mouseX < startX + totalSize &&
                mouseY >= startY && mouseY < startY + totalSize) {

            int x = (int)((mouseX - startX) / editorPixelSize);
            int y = (int)((mouseY - startY) / editorPixelSize);

            if (x >= 0 && x < CrosshairData.GRID_SIZE && y >= 0 && y < CrosshairData.GRID_SIZE) {
                CrosshairData.grid[x][y] = !CrosshairData.grid[x][y];
                return true;
            }
        }
        return false;
    }
}