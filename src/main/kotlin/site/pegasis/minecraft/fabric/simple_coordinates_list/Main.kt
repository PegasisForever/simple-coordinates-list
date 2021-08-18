package site.pegasis.minecraft.fabric.simple_coordinates_list

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW
import site.pegasis.minecraft.fabric.simple_coordinates_list.config.Config
import site.pegasis.minecraft.fabric.simple_coordinates_list.screens.AddCoordinatesScreen
import site.pegasis.minecraft.fabric.simple_coordinates_list.screens.ManageCoordinatesScreen
import site.pegasis.minecraft.fabric.simple_coordinates_list.screens.getCoordinatesList

object Main : ClientModInitializer {
    const val MOD_ID = "simple_coordinates_list"
    private val LOGGER = LogManager.getLogger()

    override fun onInitializeClient() {
        LOGGER.info("Initializing Simple Coordinate List.....")
        Config.init(MOD_ID, Config::class.java)

        val addToListKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.${MOD_ID}.add_to_list", GLFW.GLFW_KEY_SEMICOLON, "key.category.${MOD_ID}"))
        val removeFromListKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.${MOD_ID}.remove_from_list", GLFW.GLFW_KEY_APOSTROPHE, "key.category.${MOD_ID}"))

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            client.player ?: return@register

            if (addToListKey.wasPressed()) {
                client.setScreen(AddCoordinatesScreen(client.player!!.pos))
            }
            if (removeFromListKey.wasPressed() && getCoordinatesList(client!!).isNotEmpty()) {
                client.setScreen(ManageCoordinatesScreen())
            }
        }

        LOGGER.info("Initialized Simple Coordinate List!")
    }
}
