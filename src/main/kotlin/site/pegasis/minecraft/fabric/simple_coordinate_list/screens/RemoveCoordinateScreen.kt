package site.pegasis.minecraft.fabric.simple_coordinate_list.screens

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import site.pegasis.minecraft.fabric.simple_coordinate_list.DataStore
import site.pegasis.minecraft.fabric.simple_coordinate_list.WorldIdentifier
import site.pegasis.minecraft.fabric.simple_coordinate_list.getWidth
import site.pegasis.minecraft.fabric.simple_coordinate_list.toHumanText

class RemoveCoordinateScreen : Screen(Text.of("Remove Coordinate")) {
    private val deleteButtons = arrayListOf<TexturedButtonWidget>()
    private val buttonX by lazy {
        val coordinates = getCoordinates(client!!)
        val labelLinesWidth = coordinates
            .map { (_, label) ->
                if (label.isEmpty()) {
                    ""
                } else {
                    "$label: "
                }
            }.maxOf { it.getWidth(client!!) }
        val posLinesWidth = coordinates
            .map { it.pos.toHumanText() }
            .maxOf { it.getWidth(client!!) }
        val lineHeight = client!!.textRenderer.fontHeight + 2
        (width - 4 - labelLinesWidth - posLinesWidth - 10 - lineHeight - 8).toInt()
    }

    override fun isPauseScreen() = false

    override fun init() {
        val backButton = ButtonWidget(10, 10, 50, 20, Text.of("< Back")) {
            client!!.setScreen(null)
        }
        addDrawableChild(backButton)

        createDeleteButtons()
    }

    private fun createDeleteButtons() {
        val coordinates = getCoordinates(client!!)
        if (coordinates.isEmpty()) {
            client!!.setScreen(null)
            return
        }

        deleteButtons.forEach {
            remove(it)
        }
        deleteButtons.clear()

        val lineHeight = client!!.textRenderer.fontHeight + 2
        val identifier = WorldIdentifier.from(client!!)
        for (i in coordinates.indices) {
            val coordinate = coordinates[i]
            val deleteButton = TexturedButtonWidget(
                buttonX, lineHeight * i + 4,
                9, 9,
                0, 0,
                9,
                Identifier("simple_coordinate_list", "textures/delete.png"),
                9,
                18
            ) {
                println("delete $i")
                DataStore.removeCoordinate(identifier, coordinate)
                createDeleteButtons()
            }
            addDrawableChild(deleteButton)
            deleteButtons.add(deleteButton)
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        renderCoordinateList(matrices, client!!, width)
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun renderBackground(matrices: MatrixStack?) {
        fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680)
    }
}
