package lijek.lightoverlay.mixin;

import lijek.lightoverlay.LightOverlayMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow private static Minecraft instance;

    @Inject(at = @At(value = "RETURN"), method = "tick")
    private void tick(CallbackInfo ci){
        LightOverlayMod.INSTANCE.tick(instance);
    }

}
