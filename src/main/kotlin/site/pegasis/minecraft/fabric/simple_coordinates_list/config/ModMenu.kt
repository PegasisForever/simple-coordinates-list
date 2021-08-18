package site.pegasis.minecraft.fabric.simple_coordinates_list.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import site.pegasis.minecraft.fabric.simple_coordinates_list.Main

@Environment(EnvType.CLIENT)
class ModMenu : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent ->
            Config.getScreen(parent, Main.MOD_ID)
        }
    }
}
