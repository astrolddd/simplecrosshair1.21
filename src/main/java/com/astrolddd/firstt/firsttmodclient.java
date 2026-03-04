package com.astrolddd.firstt;
import com.astrolddd.firstt.client.CrosshairRenderer;
import com.astrolddd.firstt.client.KeybindHandler;

import net.fabricmc.api.ClientModInitializer;

public class firsttmodclient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CrosshairRenderer.register();
        KeybindHandler.register();
    }
}
