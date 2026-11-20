package dev.nocdhotbarfix.mixin;

import dev.nocdhotbarfix.Nocd;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class NocdAdapter implements Nocd.Adapter {
    private final MinecraftClient client;
    private final KeyInput input;

    public NocdAdapter(MinecraftClient client, KeyInput input) {
        this.client = client;
        this.input = input;
    }

    public int classify(int key, int scancode) {
        for (KeyBinding b : client.options.hotbarKeys) if (b.matchesKey(input)) return Nocd.HOTBAR;
        if (client.options.forwardKey.matchesKey(input)) return Nocd.FWD;
        if (client.options.backKey.matchesKey(input)) return Nocd.BACK;
        if (client.options.leftKey.matchesKey(input)) return Nocd.LEFT;
        if (client.options.rightKey.matchesKey(input)) return Nocd.RIGHT;
        for (KeyBinding b : client.options.allKeys) if (b.matchesKey(input)) return Nocd.ACTION;
        return Nocd.NONE;
    }

    public void release(int key, int scancode, Object token) {
        KeyBinding.setKeyPressed(InputUtil.fromKeyCode(new KeyInput(key, scancode, 0)), false);
    }
}
