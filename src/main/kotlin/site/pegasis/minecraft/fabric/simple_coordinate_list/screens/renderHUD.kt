package site.pegasis.minecraft.fabric.simple_coordinate_list.screens

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import site.pegasis.minecraft.fabric.simple_coordinate_list.*

fun renderHUD(matrixStack: MatrixStack,client: MinecraftClient,scaledWidth: Int){
    val coordinates = DataStore.coordinates[WorldIdentifier.from(client)]
    if (!client.options.debugEnabled && coordinates?.isNotEmpty() == true) {
        RenderSystem.enableBlend()
        val labelLines = coordinates.map { (_, label) ->
            if (label.isEmpty()) {
                ""
            } else {
                "$label: "
            }
        }

        val posLines = coordinates.map { it.pos.toHumanText() }
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
            val targetPos = coordinates[i].pos
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
