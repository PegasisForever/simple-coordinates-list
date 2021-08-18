package site.pegasis.minecraft.fabric.simple_coordinates_list.screens

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import site.pegasis.minecraft.fabric.simple_coordinates_list.*
import site.pegasis.minecraft.fabric.simple_coordinates_list.config.Config

fun getCoordinatesList(client: MinecraftClient) = DataStore.getCoordinatesList(WorldIdentifier.from(client))

fun renderCoordinatesList(matrixStack: MatrixStack, client: MinecraftClient, scaledWidth: Int) {
    val coordinatesList = getCoordinatesList(client)
    if (!client.options.debugEnabled && coordinatesList.isNotEmpty()) {
        RenderSystem.enableBlend()
        val labelLines = coordinatesList.map { (_, label) ->
            if (label.isEmpty()) {
                ""
            } else {
                "$label: "
            }
        }

        val posLines = coordinatesList.map { it.pos.toHumanText() }
        val posLinesWidth = posLines.maxOf { it.getWidth(client) }

        val lineHeight = client.textRenderer.fontHeight + 2

        for (i in labelLines.indices) {
            client.textRenderer.drawWithShadow(
                matrixStack, labelLines[i],
                scaledWidth + Config.xOffset - 2 - posLinesWidth - labelLines[i].getWidth(client) - lineHeight,
                (lineHeight * i + Config.yOffset).toFloat(),
                0xFFFFFF,
            )
            client.textRenderer.drawWithShadow(
                matrixStack, posLines[i],
                scaledWidth + Config.xOffset - 2 - posLinesWidth - lineHeight,
                (lineHeight * i + Config.yOffset).toFloat(),
                0xFFFFFF,
            )

            val currPos = client.player!!.pos
            val targetPos = coordinatesList[i].pos
            val targetDegree = currPos.angleTo(targetPos) - 90
            val currFacingDegree = client.player!!.yaw
            var d = (currFacingDegree - targetDegree + 180) % 360
            if (d < 0) d += 360
            d = 360 - d
            val compassI = (d / 11.25).toInt()
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
            RenderSystem.setShaderTexture(0, Identifier("minecraft", "textures/item/compass_${compassI.toString().padStart(2, '0')}.png"))
            DrawableHelper.drawTexture(matrixStack, scaledWidth + Config.xOffset - lineHeight, lineHeight * i + Config.yOffset - 2, 0f, 0f, lineHeight, lineHeight, lineHeight, lineHeight)
        }
    }
}
