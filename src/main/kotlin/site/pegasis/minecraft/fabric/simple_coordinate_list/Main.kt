package site.pegasis.minecraft.fabric.simple_coordinate_list

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW
import site.pegasis.minecraft.fabric.simple_coordinate_list.screens.AddCoordinateScreen

object Main : ClientModInitializer {
    override fun onInitializeClient() {
        println("Initializing Simple Coordinate List.....")
        val addToListKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.simple_coordinate_list.add_to_list", GLFW.GLFW_KEY_SEMICOLON, "key.category.simple_coordinate_list"))

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            client.player ?: return@register

            if (addToListKey.wasPressed()) {
                client.setScreen(AddCoordinateScreen(client.player!!.pos))
            }
        }

        println("Initialized Simple Coordinate List!")
    }
}
