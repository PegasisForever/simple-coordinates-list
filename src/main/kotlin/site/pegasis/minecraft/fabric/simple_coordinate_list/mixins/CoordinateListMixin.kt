package site.pegasis.minecraft.fabric.simple_coordinate_list.mixins

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import site.pegasis.minecraft.fabric.simple_coordinate_list.DataStore
import site.pegasis.minecraft.fabric.simple_coordinate_list.angleTo
import site.pegasis.minecraft.fabric.simple_coordinate_list.getWidth
import site.pegasis.minecraft.fabric.simple_coordinate_list.toHumanText

@Environment(EnvType.CLIENT)
@Mixin(value = [InGameHud::class])
open abstract class CoordinateListMixin {
    @Shadow
    lateinit var client: MinecraftClient

    @Shadow
    var scaledWidth: Int = 0

    @Inject(method = ["<init>(Lnet/minecraft/client/MinecraftClient;)V"], at = [At(value = "RETURN")])
    private fun onInit(client: MinecraftClient, ci: CallbackInfo) {
        println("Init CoordinateListMixin")
    }

    @Inject(method = ["render"], at = [At("HEAD")])
    private fun onDraw(matrixStack: MatrixStack, esp: Float, ci: CallbackInfo) {
        if (!client.options.debugEnabled && DataStore.coordinates.isNotEmpty()) {
            RenderSystem.enableBlend()
            val labelLines = DataStore.coordinates.map { (_, label) ->
                if (label.isEmpty()) {
                    ""
                } else {
                    "$label: "
                }
            }

            val posLines = DataStore.coordinates.map { it.pos.toHumanText() }
            val posLinesWidth = posLines.maxOf { it.getWidth(client) }

            val lineHeight = client.textRenderer.fontHeight + 2

            for (i in labelLines.indices) {
                client.textRenderer.drawWithShadow(
                    matrixStack, labelLines[i],
                    scaledWidth - 4 - 2 - posLinesWidth - labelLines[i].getWidth(client) - lineHeight,
                    (lineHeight * i + 4).toFloat(),
                    0xFFFFFF,
                )
                client.textRenderer.drawWithShadow(
                    matrixStack, posLines[i],
                    scaledWidth - 4 - 2 - posLinesWidth - lineHeight,
                    (lineHeight * i + 4).toFloat(),
                    0xFFFFFF,
                )

                val currPos = client.player!!.pos
                val targetPos = DataStore.coordinates[i].pos
                val targetDegree = currPos.angleTo(targetPos) - 90
                val currFacingDegree = client.player!!.yaw
                var d = (currFacingDegree - targetDegree + 180) % 360
                if (d < 0) d += 360
                d = 360 - d
                val compassI = (d / 11.25).toInt()
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
                RenderSystem.setShaderTexture(0, Identifier("minecraft", "textures/item/compass_${compassI.toString().padStart(2, '0')}.png"))
                DrawableHelper.drawTexture(matrixStack, scaledWidth - 4 - lineHeight, lineHeight * i + 2, 0f, 0f, lineHeight, lineHeight, lineHeight, lineHeight)
            }
        }
    }
}
