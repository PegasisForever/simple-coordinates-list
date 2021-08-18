package site.pegasis.minecraft.fabric.simple_coordinates_list

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
        override fun getJSONFile() = File(saveDir, "${Main.MOD_ID}/${worldName.toSafeBase64()}.json")
    }

    @Serializable
    data class Server(val serverName: String, val serverAddress: String, val worldName: String) : WorldIdentifier() {
        override fun getJSONFile(): File {
            val name = "${serverName}|||${serverAddress}|||${worldName}".toSafeBase64()
            return File(FabricLoader.getInstance().configDir.toString(), "${Main.MOD_ID}/servers/${name}.json")
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
data class Coordinates(@Serializable(Vec3dSerializer::class) val pos: Vec3d, val label: String)

object DataStore {
    private val serializer = Json {
        allowStructuredMapKeys = true
    }

    private var coordinateCache = hashMapOf<WorldIdentifier, ArrayList<Coordinates>>()

    fun getCoordinatesList(identifier: WorldIdentifier): List<Coordinates> {
        if (coordinateCache[identifier] == null) {
            try {
                val jsonFile = identifier.getJSONFile()
                if (jsonFile.exists()) {
                    val coordinateList: ArrayList<Coordinates> = serializer.decodeFromString(jsonFile.readText())
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

    fun addCoordinates(identifier: WorldIdentifier, coordinates: Coordinates) {
        var list = coordinateCache[identifier]
        if (list == null) {
            list = arrayListOf()
            coordinateCache[identifier] = list
        }
        list.add(coordinates)

        save(identifier)
    }

    fun removeCoordinates(identifier: WorldIdentifier, coordinates: Coordinates) {
        coordinateCache[identifier]!!.remove(coordinates)

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
