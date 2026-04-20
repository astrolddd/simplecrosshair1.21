package com.astrolddd.firstt.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    private static KeyBinding openGuiKey;

    public static void register() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.firstt.open_gui",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_X,
                        "key.categories.misc" // Use 'misc' or 'ui'—standard Yarn 1.21.4 uses Strings here
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.setScreen(new CrosshairScreen());
            }
        });
    }
}