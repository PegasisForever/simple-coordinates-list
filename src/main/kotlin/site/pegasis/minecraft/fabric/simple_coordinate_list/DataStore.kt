package site.pegasis.minecraft.fabric.simple_coordinate_list

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient
import net.minecraft.util.WorldSavePath
import net.minecraft.util.math.Vec3d

@Serializable
@SerialName("Vec3d")
data class Vec3dSurrogate(val x: Double, val y: Double, val z: Double)

object Vec3dSerializer : KSerializer<Vec3d> {
    override val descriptor: SerialDescriptor = Vec3dSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Vec3d) {
        val surrogate = Vec3dSurrogate(value.x, value.y, value.z)
        encoder.encodeSerializableValue(Vec3dSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Vec3d {
        val surrogate = decoder.decodeSerializableValue(Vec3dSurrogate.serializer())
        return Vec3d(surrogate.x, surrogate.y, surrogate.z)
    }
}


@Serializable
sealed class WorldIdentifier {
    @Serializable
    data class Local(val saveDir: String, val worldName: String) : WorldIdentifier()

    @Serializable
    data class Server(val serverName: String, val serverAddress: String, val worldName: String) : WorldIdentifier()

    companion object {
        fun from(client: MinecraftClient): WorldIdentifier {
            return if (client.isIntegratedServerRunning) {
                Local(
                    client.server!!.getSavePath(WorldSavePath.ROOT).toString(),
                    client.world!!.registryKey.value.path,
                )
            } else {
                val serverInfo = client.currentServerEntry!!
                Server(
                    serverInfo.name,
                    serverInfo.address,
                    client.world!!.registryKey.value.path,
                )
            }
        }
    }
}

@Serializable
data class CoordinateItem(@Serializable(Vec3dSerializer::class) val pos: Vec3d, val label: String)

object DataStore {
    private val serializer = Json {
        allowStructuredMapKeys = true
    }

    var coordinates: HashMap<WorldIdentifier, ArrayList<CoordinateItem>> = kotlin.run {
        serializer.decodeFromString("""[{"type":"site.pegasis.minecraft.fabric.simple_coordinate_list.WorldIdentifier.Local","saveDir":"/home/pegasis/Projects/McProjects/simple-coordinate-list/run/./saves/New World/.","worldName":"overworld"},[{"pos":{"x":-63.92433140697427,"y":71.0,"z":280.79791663017664},"label":"aaa"},{"pos":{"x":-56.87575674104418,"y":70.0,"z":299.3425524517237},"label":"bwbasda"}],{"type":"site.pegasis.minecraft.fabric.simple_coordinate_list.WorldIdentifier.Server","serverName":"Minecraft Server","serverAddress":"localhost","worldName":"overworld"},[{"pos":{"x":198.5,"y":63.0,"z":91.5},"label":"aaa aserver"}]]""")
    }

    fun addCoordinate(identifier: WorldIdentifier, coordinateItem: CoordinateItem) {
        var list = coordinates[identifier]
        if (list == null) {
            list = arrayListOf()
            coordinates[identifier] = list
        }
        list.add(coordinateItem)
        save()
    }

    private fun save() {
        println(serializer.encodeToString(coordinates))
    }
}
