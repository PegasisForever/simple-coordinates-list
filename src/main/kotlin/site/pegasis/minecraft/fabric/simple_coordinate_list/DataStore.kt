package site.pegasis.minecraft.fabric.simple_coordinate_list

import net.minecraft.util.math.Vec3d

data class CoordinateItem(val pos:Vec3d,val label:String)

object DataStore {
    val coordinates = arrayListOf<CoordinateItem>()
}
