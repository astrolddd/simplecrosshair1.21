package com.astrolddd.firstt.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    private static KeyBinding openGuiKey;

    public static void register() {

        openGuiKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.firstt.open_gui",
                        GLFW.GLFW_KEY_O,
                        "category.firstt"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.setScreen(new CrosshairScreen());
            }
        });
    }
}