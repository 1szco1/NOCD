package dev.nocdhotbarfix.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import dev.nocdhotbarfix.Nocd;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

public final class NocdAdapter implements Nocd.Adapter {
    private final Minecraft client;
    private final KeyEvent event;

    public NocdAdapter(Minecraft client, KeyEvent event) {
        this.client = client;
        this.event = event;
    }

    public int classify(int key, int scancode) {
        for (KeyMapping b : client.options.keyHotbarSlots) if (b.matches(event)) return Nocd.HOTBAR;
        if (client.options.keyUp.matches(event)) return Nocd.FWD;
        if (client.options.keyDown.matches(event)) return Nocd.BACK;
        if (client.options.keyLeft.matches(event)) return Nocd.LEFT;
        if (client.options.keyRight.matches(event)) return Nocd.RIGHT;
        for (KeyMapping b : client.options.keyMappings) if (b.matches(event)) return Nocd.ACTION;
        return Nocd.NONE;
    }

    public void release(int key, int scancode, Object token) {
        KeyMapping.set(InputConstants.getKey((KeyEvent) token), false);
    }
}
