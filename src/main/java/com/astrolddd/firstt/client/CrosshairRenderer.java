package com.astrolddd.firstt.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.Entity;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.entity.player.PlayerEntity;


public class CrosshairRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            render(drawContext);
        });
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        int color = CrosshairData.color;

        /*if (CrosshairData.attackIndicatorEnabled &&
                client.crosshairTarget instanceof EntityHitResult hit &&
                client.player != null &&
                hit.getEntity() instanceof PlayerEntity target &&
                hit.getEntity() != client.player) {

            double reach = 3.0;

            if (client.interactionManager != null &&
                    client.interactionManager.getCurrentGameMode().isCreative()) {
                reach = 5.0;
            }

            double distance = hit.getPos().distanceTo(client.player.getCameraPosVec(1.0f));

            if (distance <= reach) {
                color = CrosshairData.attackIndicatorColor;
            }
        }*/



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
                            color
                    );
                }
            }
        }


        if (client.options.getAttackIndicator().getValue() != AttackIndicator.OFF) {
            client.options.getAttackIndicator().setValue(AttackIndicator.OFF);
        }
    }
}