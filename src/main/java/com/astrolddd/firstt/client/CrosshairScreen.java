package com.astrolddd.firstt.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CrosshairScreen extends Screen {

    private final int pixelSize = 12;
    private int sliderWidth = 100;

    private int thicknessSliderY;
    private int widthSliderY;
    private int lengthSliderY;
    private final int gridSize = CrosshairData.SIZE;
    private int sliderX;
    private boolean draggingSlider = false;
    protected CrosshairScreen() {
        super(Text.literal("Draw Crosshair"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xCC000000);

        context.getMatrices().push();
        context.getMatrices().scale(1.0f, 1.0f, 1.0f);
        int totalSize = gridSize * pixelSize;

        int startX = (width - totalSize) / 2;
        int startY = (height - totalSize) / 2;
        // 🔹 Dark panel background
        int boxPadding = 12;
        // Position sliders
        sliderX = width / 2 - sliderWidth / 2;

        // Sliders on right side of grid
        int sliderStartX = startX + totalSize + 30;
        sliderX = sliderStartX;

        thicknessSliderY = startY + 20;
        widthSliderY = thicknessSliderY + 30;
        lengthSliderY = widthSliderY + 30;

// Extend panel to include sliders area
        int panelRight = sliderStartX + sliderWidth + 20;
        int panelBottom = startY + totalSize + 20;

        context.fill(
                startX - 12,
                startY - 12,
                startX + gridSize * pixelSize + boxPadding,
                panelBottom,
                0xCC111111
        );

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {

                int drawX = startX + x * pixelSize;
                int drawY = startY + y * pixelSize;

                boolean isCenter =
                        x == gridSize / 2 &&
                                y == gridSize / 2;

                int color;

                if (CrosshairData.pixels[x][y]) {
                    color = 0xFFFFFFFF; // white drawn pixel
                } else if (isCenter) {
                    color = 0xFF8888AA; // soft bluish center guide
                } else {
                    color = 0xFF444444; // darker background
                }

                context.fill(drawX, drawY,
                        drawX + pixelSize,
                        drawY + pixelSize,
                        color);
                // Top
                context.fill(drawX, drawY, drawX + pixelSize, drawY + 1, 0xFF000000);

// Bottom
                context.fill(drawX, drawY + pixelSize - 1, drawX + pixelSize, drawY + pixelSize, 0xFF000000);

// Left
                context.fill(drawX, drawY, drawX + 1, drawY + pixelSize, 0xFF000000);

// Right
                context.fill(drawX + pixelSize - 1, drawY, drawX + pixelSize, drawY + pixelSize, 0xFF000000);
            }
        }

        drawSlider(context, thicknessSliderY, CrosshairData.thickness, "Thickness");
        drawSlider(context, widthSliderY, CrosshairData.width, "Width");
        drawSlider(context, lengthSliderY, CrosshairData.length, "Length");
        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        int totalSize = gridSize * pixelSize;
        int startX = (width - totalSize) / 2;
        int startY = (height - totalSize) / 2;

        // ===== GRID CLICK DETECTION =====
        if (mouseX >= startX && mouseX < startX + totalSize &&
                mouseY >= startY && mouseY < startY + totalSize) {

            int gridX = (int)((mouseX - startX) / pixelSize);
            int gridY = (int)((mouseY - startY) / pixelSize);

            CrosshairData.pixels[gridX][gridY] =
                    !CrosshairData.pixels[gridX][gridY];

            return true;
        }

        // ===== SLIDERS =====
        if (handleSlider(mouseX, mouseY, thicknessSliderY, "thickness")) return true;
        if (handleSlider(mouseX, mouseY, widthSliderY, "width")) return true;
        if (handleSlider(mouseX, mouseY, lengthSliderY, "length")) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleSlider(double mouseX, double mouseY, int sliderY, String type) {

        if (mouseY >= sliderY - 5 && mouseY <= sliderY + 10) {

            int relative = (int) (mouseX - sliderX);
            int value = relative / 10 + 1;

            if (value >= 1 && value <= 10) {

                if (type.equals("thickness"))
                    CrosshairData.thickness = value;

                if (type.equals("width"))
                    CrosshairData.width = value;

                if (type.equals("length"))
                    CrosshairData.length = value;
            }

            return true;
        }

        return false;

    }
private void drawSlider(DrawContext context, int y, int value, String label) {

    context.fill(sliderX, y,
            sliderX + sliderWidth,
            y + 6,
            0xFF333333);

    int knobX = sliderX + (value - 1) * 10;

    context.fill(knobX, y - 4,
            knobX + 10,
            y + 10,
            0xFFFFFFFF);

    context.drawText(
            textRenderer,
            label + ": " + value,
            sliderX,
            y - 18,
            0xFFFFFF,
            false
      );
    }
}
