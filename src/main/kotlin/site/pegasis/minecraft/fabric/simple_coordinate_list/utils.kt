package site.pegasis.minecraft.fabric.simple_coordinate_list

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.math.atan2

fun Vec3d.toHumanText() = "${x.toInt()}, ${y.toInt()}, ${z.toInt()}"

fun Vec3d.angleTo(target: Vec3d): Double {
    var angle = Math.toDegrees(atan2(target.z - z, target.x - x))
    if (angle < 0) angle += 360
    return angle
}

fun String.getWidth(client: MinecraftClient) = client.textRenderer.textHandler.getWidth(this)

fun String.toSafeBase64(): String = Base64.getUrlEncoder().encodeToString(toByteArray())
