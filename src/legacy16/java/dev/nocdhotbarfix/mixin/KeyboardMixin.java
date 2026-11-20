package dev.nocdhotbarfix.mixin;

import dev.nocdhotbarfix.Nocd;
import dev.nocdhotbarfix.NocdAdapter;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
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
    private void nocd$keys(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        if (window != client.getWindow().getHandle()) return;
        if (action == 2) {
            for (KeyBinding kb : client.options.keysHotbar) {
                if (kb.matchesKey(key, scancode)) ((KeyBindingAccessor) kb).set(0);
            }
            if (client.options.keyInventory.matchesKey(key, scancode)) {
                ((KeyBindingAccessor) client.options.keyInventory).set(0);
            }
        } else if (action == 0) {
            Nocd.unpress(key);
        } else {
            Nocd.press(key, scancode, null, new NocdAdapter(client));
        }
    }
}
