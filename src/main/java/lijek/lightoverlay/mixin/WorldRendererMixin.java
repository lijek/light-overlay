package lijek.lightoverlay.mixin;

import lijek.lightoverlay.LightOverlayMod;
import net.minecraft.client.render.WorldRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(at = @At("HEAD"), method = "method_1552")
    private void renderClouds(float f, CallbackInfo ci){
        if(LightOverlayMod.enabled){
            GL11.glDisable(2912);
            LightOverlayMod.INSTANCE.render(f);
            GL11.glEnable(2912);
        }
    }
}
