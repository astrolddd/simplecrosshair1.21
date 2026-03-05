package com.astrolddd.firstt.mixin;

import com.astrolddd.firstt.client.CrosshairData;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void cancelCrosshair(CallbackInfo ci) {
        if (hasCustomCrosshair()) {
            ci.cancel();
        }
    }
    private boolean hasCustomCrosshair() {
        for (int x = 0; x < com.astrolddd.firstt.client.CrosshairData.GRID_SIZE; x++) {
            for (int y = 0; y < CrosshairData.GRID_SIZE; y++) {
                if (CrosshairData.grid[x][y]) {
                    return true;
                }
            }
        }
        return false;
    }
}