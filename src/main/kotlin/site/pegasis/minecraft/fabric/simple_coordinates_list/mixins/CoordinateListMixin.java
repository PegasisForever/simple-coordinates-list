package site.pegasis.minecraft.fabric.simple_coordinates_list.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.pegasis.minecraft.fabric.simple_coordinates_list.screens.ManageCoordinatesScreen;
import site.pegasis.minecraft.fabric.simple_coordinates_list.screens.RenderCoordinatesListKt;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public abstract class CoordinateListMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int scaledWidth;

    @Inject(method = "render", at = @At("HEAD"))
    private void onDraw(MatrixStack matrixStack, float esp, CallbackInfo ci) {
        if (!(client.currentScreen instanceof ManageCoordinatesScreen)) {
            RenderCoordinatesListKt.renderCoordinatesList(matrixStack, client, scaledWidth);
        }
    }
}

