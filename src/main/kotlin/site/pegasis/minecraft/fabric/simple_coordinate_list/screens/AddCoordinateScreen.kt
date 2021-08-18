package site.pegasis.minecraft.fabric.simple_coordinate_list.screens

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import site.pegasis.minecraft.fabric.simple_coordinate_list.*


class AddCoordinateScreen(private val pos: Vec3d) : Screen(Text.of("Add Coordinate")) {
    override fun init() {
        val labelTextField = kotlin.run {
            val textFieldWidth = 300
            TextFieldWidget(textRenderer, (width - textFieldWidth) / 2, 100, textFieldWidth, 20, Text.of("Label"))
        }
        addDrawableChild(labelTextField)
        setInitialFocus(labelTextField)

        val backButton = ButtonWidget(10, 10, 50, 20, Text.of("< Back")) {
            client!!.setScreen(null)
        }
        addDrawableChild(backButton)

        val addButton = kotlin.run {
            val buttonWidth = 100
            ButtonWidget((width - buttonWidth) / 2, 135, 100, 20, Text.of("Add to List")) {
                DataStore.addCoordinate(WorldIdentifier.from(client!!), CoordinateItem(pos, labelTextField.text))
                client!!.setScreen(null)
            }
        }
        addDrawableChild(addButton)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)

        val posText = "Your coordinates: " + pos.toHumanText()
        val posTextWidth = posText.getWidth(client!!)
        client!!.textRenderer.drawWithShadow(matrices, posText, (width - posTextWidth) / 2, (100 - 20 - client!!.textRenderer.fontHeight).toFloat(), 0xFFFFFF)

        client!!.textRenderer.drawWithShadow(matrices, "Label", (width - 300) / 2f, 100f - 2f - client!!.textRenderer.fontHeight, 0x9E9E9E)

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun renderBackground(matrices: MatrixStack?) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680)
    }
}
