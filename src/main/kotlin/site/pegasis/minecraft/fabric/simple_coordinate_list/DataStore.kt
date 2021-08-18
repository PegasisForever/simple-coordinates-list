package site.pegasis.minecraft.fabric.simple_coordinate_list

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.util.WorldSavePath
import net.minecraft.util.math.Vec3d
import java.io.File

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
    data class Local(val saveDir: String, val worldName: String) : WorldIdentifier() {
        override fun getJSONFile() = File(saveDir, "simple_coordinate_list/${worldName.toSafeBase64()}.json")
    }

    @Serializable
    data class Server(val serverName: String, val serverAddress: String, val worldName: String) : WorldIdentifier() {
        override fun getJSONFile(): File {
            val name = "${serverName}|||${serverAddress}|||${worldName}".toSafeBase64()
            return File(FabricLoader.getInstance().configDir.toString(), "simple_coordinate_list/servers/${name}.json")
        }
    }

    abstract fun getJSONFile(): File

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

    private var coordinateCache = hashMapOf<WorldIdentifier, ArrayList<CoordinateItem>>()

    fun getCoordinates(identifier: WorldIdentifier): List<CoordinateItem> {
        if (coordinateCache[identifier] == null) {
            try {
                val jsonFile = identifier.getJSONFile()
                if (jsonFile.exists()) {
                    val coordinateList: ArrayList<CoordinateItem> = serializer.decodeFromString(jsonFile.readText())
                    coordinateCache[identifier] = coordinateList
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            if (coordinateCache[identifier] == null) {
                coordinateCache[identifier] = arrayListOf()
            }
        }

        return coordinateCache[identifier]!!
    }

    fun addCoordinate(identifier: WorldIdentifier, coordinateItem: CoordinateItem) {
        var list = coordinateCache[identifier]
        if (list == null) {
            list = arrayListOf()
            coordinateCache[identifier] = list
        }
        list.add(coordinateItem)

        save(identifier)
    }

    fun removeCoordinate(identifier: WorldIdentifier, coordinateItem: CoordinateItem) {
        coordinateCache[identifier]!!.remove(coordinateItem)

        save(identifier)
    }

    private fun save(identifier: WorldIdentifier) {
        val jsonFile = identifier.getJSONFile()
        if (!jsonFile.exists()) {
            jsonFile.parentFile?.mkdirs()
            jsonFile.createNewFile()
        }
        jsonFile.writeText(serializer.encodeToString(coordinateCache[identifier]))
    }
}
