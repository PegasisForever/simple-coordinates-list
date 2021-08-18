package site.pegasis.minecraft.fabric.simple_coordinates_list.screens

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import site.pegasis.minecraft.fabric.simple_coordinates_list.*
import site.pegasis.minecraft.fabric.simple_coordinates_list.config.Config

class ManageCoordinatesScreen : Screen(TranslatableText("${Main.MOD_ID}.gui.manage_coordinates")) {
    private val deleteButtons = arrayListOf<TexturedButtonWidget>()
    private val buttonX by lazy {
        val coordinatesList = getCoordinatesList(client!!)
        val labelLinesWidth = coordinatesList
            .map { (_, label) ->
                if (label.isEmpty()) {
                    ""
                } else {
                    "$label: "
                }
            }.maxOf { it.getWidth(client!!) }
        val posLinesWidth = coordinatesList
            .map { it.pos.toHumanText() }
            .maxOf { it.getWidth(client!!) }
        val lineHeight = client!!.textRenderer.fontHeight + 2
        (width + Config.xOffset - labelLinesWidth - posLinesWidth - 10 - lineHeight - 8).toInt()
    }

    override fun isPauseScreen() = false

    override fun init() {
        val backButton = ButtonWidget(10, 10, 50, 20, TranslatableText("${Main.MOD_ID}.gui.back")) {
            client!!.setScreen(null)
        }
        addDrawableChild(backButton)

        createDeleteButtons()
    }

    private fun createDeleteButtons() {
        val coordinatesList = getCoordinatesList(client!!)
        if (coordinatesList.isEmpty()) {
            client!!.setScreen(null)
            return
        }

        deleteButtons.forEach {
            remove(it)
        }
        deleteButtons.clear()

        val lineHeight = client!!.textRenderer.fontHeight + 2
        val identifier = WorldIdentifier.from(client!!)
        for (i in coordinatesList.indices) {
            val coordinates = coordinatesList[i]
            val deleteButton = TexturedButtonWidget(
                buttonX, lineHeight * i + Config.yOffset - 1,
                9, 9,
                0, 0,
                9,
                Identifier(Main.MOD_ID, "textures/delete.png"),
                9,
                18
            ) {
                DataStore.removeCoordinates(identifier, coordinates)
                createDeleteButtons()
            }
            addDrawableChild(deleteButton)
            deleteButtons.add(deleteButton)
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        renderCoordinatesList(matrices, client!!, width)
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun renderBackground(matrices: MatrixStack?) {
        fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680)
    }
}
