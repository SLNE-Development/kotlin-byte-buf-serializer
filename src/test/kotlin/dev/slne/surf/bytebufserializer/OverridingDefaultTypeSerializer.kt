package dev.slne.surf.bytebufserializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.test.Test
import kotlin.test.assertFailsWith

class OverridingDefaultTypeSerializer {

    private val bufFormat = Buf(SerializersModule {
        contextual(CustomStringSerializer)
    })

    @Serializable
    data class Player(val name: String, val score: Int)

    @Test
    fun `test encode using custom serializer`() {
        val player = Player("Alice", 42)

        assertFailsWith<AssertionError> {
            val bytes = bufFormat.encodeToByteArray(player)
            println(bytes.joinToString())
        }
    }

    @Test
    fun `test decode using custom serializer`() {
        val bytes = byteArrayOf(0, 0, 0, 5, 65, 108, 105, 99, 101, 0, 0, 0, 42)

        assertFailsWith<AssertionError> {
            bufFormat.decodeFromByteArray<Player>(bytes)
        }
    }
}

object CustomStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CustomStringSerializer", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(
        encoder: Encoder,
        value: String
    ) {
        throw AssertionError("This exception is expected")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): String {
        throw AssertionError("This exception is expected")
    }
}