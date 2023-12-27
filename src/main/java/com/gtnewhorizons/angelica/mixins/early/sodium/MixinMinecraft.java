package com.gtnewhorizons.angelica.mixins.early.sodium;

import com.gtnewhorizons.angelica.mixins.interfaces.IRenderGlobalExt;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    public RenderGlobal renderGlobal;

    @Inject(method="resize", at=@At("TAIL"))
    private void sodium$resize(int width, int height, CallbackInfo ci) {
        if(this.renderGlobal != null) {
            ((IRenderGlobalExt)this.renderGlobal).scheduleTerrainUpdate();
        }
    }

    @Redirect(method="runGameLoop", at=@At(value="FIELD", target="Lnet/minecraft/client/renderer/WorldRenderer;chunksUpdated:I", ordinal=0))
    private int sodium$chunksUpdated() {
        return ((IRenderGlobalExt)this.renderGlobal).getChunksSubmitted();
    }

    @Redirect(method = "runGameLoop", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;fancyGraphics:Z"))
    private boolean sodium$overrideFancyGrass(GameSettings gameSettings) {
        return SodiumClientMod.options().quality.grassQuality.isFancy();
    }

    @Inject(method = "checkGLError", at = @At(value = "HEAD"), cancellable = true)
    private void sodium$checkGLError(CallbackInfo ci) {
        if(SodiumClientMod.options().performance.useNoErrorGLContext)
            ci.cancel();
    }
}