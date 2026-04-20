package com.astrolddd.firstt.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.option.AttackIndicator;

public class CrosshairRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> render(drawContext));
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen != null) return;

        // 1. Determine Color (Reach Tint Logic)
        int drawColor = CrosshairData.color;
        if (CrosshairData.reachTintEnabled && client.crosshairTarget instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof PlayerEntity targetPlayer && targetPlayer.isAlive()) {
                double distance = client.getCameraEntity().getEyePos().distanceTo(entityHit.getPos());
                // Survival is 3.0, Creative is higher
                if (distance <= client.player.getEntityInteractionRange()) {
                    drawColor = 0xFFFF0000; // Red when in reach
                }
            }
        }

        // 2. Precision Math (10 on UI = 1.0 Pixel)
        int centerX = context.getScaledWindowWidth() / 2;
        int centerY = context.getScaledWindowHeight() / 2;

        // Remove the division by 10.0f so the scale feels right for the 0-10 range
        // 1. Set up the basic size
        float baseSize = CrosshairData.thickness; // Base thickness (e.g., 1 or 2)
        float stretchWidth = CrosshairData.width;  // How much to stretch horizontally
        float stretchLength = CrosshairData.length; // How much to stretch vertically

        for (int x = 0; x < CrosshairData.GRID_SIZE; x++) {
            for (int y = 0; y < CrosshairData.GRID_SIZE; y++) {
                if (CrosshairData.grid[x][y]) {

                    // 2. POSITIONING: Keep the grid gaps tied to base thickness
                    // This ensures pixels stay touching regardless of how long the lines are
                    float drawX = centerX + (x - CrosshairData.GRID_SIZE / 2.0f) * baseSize * stretchWidth;
                    float drawY = centerY + (y - CrosshairData.GRID_SIZE / 2.0f) * baseSize * stretchLength;

                    // 3. DRAWING: Fill the rectangle based on the stretch
                    context.fill(
                            (int) drawX,
                            (int) drawY,
                            (int) (drawX + (baseSize * stretchWidth)),
                            (int) (drawY + (baseSize * stretchLength)),
                            drawColor
                    );
                }
            }
        }

        // 4. Force vanilla indicator off
        if (client.options.getAttackIndicator().getValue() != AttackIndicator.OFF) {
            client.options.getAttackIndicator().setValue(AttackIndicator.OFF);
        }
    }
}