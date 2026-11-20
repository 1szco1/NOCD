package dev.nocdhotbarfix.mixin;

import dev.nocdhotbarfix.Nocd;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class NocdAdapter implements Nocd.Adapter {
    private final MinecraftClient client;

    public NocdAdapter(MinecraftClient client) {
        this.client = client;
    }

    public int classify(int key, int scancode) {
        for (KeyBinding b : client.options.keysHotbar) if (b.matchesKey(key, scancode)) return Nocd.HOTBAR;
        if (client.options.keyForward.matchesKey(key, scancode)) return Nocd.FWD;
        if (client.options.keyBack.matchesKey(key, scancode)) return Nocd.BACK;
        if (client.options.keyLeft.matchesKey(key, scancode)) return Nocd.LEFT;
        if (client.options.keyRight.matchesKey(key, scancode)) return Nocd.RIGHT;
        for (KeyBinding b : client.options.keysAll) if (b.matchesKey(key, scancode)) return Nocd.ACTION;
        return Nocd.NONE;
    }

    public void release(int key, int scancode, Object token) {
        KeyBinding.setKeyPressed(InputUtil.fromKeyCode(key, scancode), false);
    }
}
