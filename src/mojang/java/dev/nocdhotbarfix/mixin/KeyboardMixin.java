package dev.nocdhotbarfix.mixin;

import dev.nocdhotbarfix.Nocd;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("TAIL"))
    private void nocd$keys(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (window != minecraft.getWindow().handle()) return;
        int key = event.key();
        if (action == 2) {
            for (KeyMapping k : minecraft.options.keyHotbarSlots) {
                if (k.matches(event)) ((KeyBindingAccessor) k).set(0);
            }
            if (minecraft.options.keyInventory.matches(event)) {
                ((KeyBindingAccessor) minecraft.options.keyInventory).set(0);
            }
        } else if (action == 0) {
            Nocd.unpress(key);
        } else {
            Nocd.press(key, 0, event, new NocdAdapter(minecraft, event));
        }
    }
}
