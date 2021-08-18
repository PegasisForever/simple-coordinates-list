package site.pegasis.minecraft.fabric.simple_coordinate_list

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW
import site.pegasis.minecraft.fabric.simple_coordinate_list.config.Config
import site.pegasis.minecraft.fabric.simple_coordinate_list.screens.AddCoordinateScreen
import site.pegasis.minecraft.fabric.simple_coordinate_list.screens.RemoveCoordinateScreen
import site.pegasis.minecraft.fabric.simple_coordinate_list.screens.getCoordinates

object Main : ClientModInitializer {
    val MOD_ID = "simple_coordinate_list"

    override fun onInitializeClient() {
        println("Initializing Simple Coordinate List.....")
        Config.init(MOD_ID, Config::class.java)

        val addToListKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.${MOD_ID}.add_to_list", GLFW.GLFW_KEY_SEMICOLON, "key.category.${MOD_ID}"))
        val removeFromListKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.${MOD_ID}.remove_from_list", GLFW.GLFW_KEY_APOSTROPHE, "key.category.${MOD_ID}"))

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            client.player ?: return@register

            if (addToListKey.wasPressed()) {
                client.setScreen(AddCoordinateScreen(client.player!!.pos))
            }
            if (removeFromListKey.wasPressed() && getCoordinates(client!!).isNotEmpty()) {
                client.setScreen(RemoveCoordinateScreen())
            }
        }

        println("Initialized Simple Coordinate List!")
    }
}
