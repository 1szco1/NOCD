package dev.nocdhotbarfix.mixin;

import dev.nocdhotbarfix.Nocd;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("TAIL"))
    private void nocd$keys(long window, int action, KeyInput input, CallbackInfo ci) {
        if (window != client.getWindow().getHandle()) return;
        int key = input.key();
        if (action == 2) {
            for (KeyBinding kb : client.options.hotbarKeys) {
                if (kb.matchesKey(input)) ((KeyBindingAccessor) kb).set(0);
            }
            if (client.options.inventoryKey.matchesKey(input)) {
                ((KeyBindingAccessor) client.options.inventoryKey).set(0);
            }
        } else if (action == 0) {
            Nocd.unpress(key);
        } else {
            Nocd.press(key, input.scancode(), null, new NocdAdapter(client, input));
        }
    }
}
