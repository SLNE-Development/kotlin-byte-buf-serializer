package dev.slne.surf.bytebufserializer

import dev.slne.surf.bytebufserializer.util.WrappedByteBuf
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertFailsWith

class CustomTypeWithCustomByteBufTest {

    private val bufFormat = Buf(SerializersModule {
        contextual(CustomIntSerializer)
    })

    @Serializable
    data class Player(val name: String, val score: Int)

    @Test
    fun `test encode and decode to custom ByteBuf`() {
        val player = Player("Alice", 42)

        val buf = CustomByteBuf(Unpooled.buffer())
        bufFormat.encodeToBuf(buf, player)
        val decodedPlayer = bufFormat.decodeFromBuf<Player>(buf)

        assert(player == decodedPlayer)
    }

    @Test
    fun `test encode and decode with wrong ByteBuf`() {
        val player = Player("Alice", 42)

        assertFailsWith<IllegalArgumentException> {
            val buf = CustomByteBuf2(Unpooled.buffer())

            bufFormat.encodeToBuf(buf, player)
            val decodedPlayer = bufFormat.decodeFromBuf<Player>(buf)

            assert(player == decodedPlayer)
        }
    }
}

object CustomIntSerializer : KBufSerializer<Int, CustomByteBuf> {
    override val bufClass: KClass<CustomByteBuf> = CustomByteBuf::class
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CustomIntSerializer", PrimitiveKind.STRING)

    override fun serialize0(buf: CustomByteBuf, value: Int) {
        buf.writeVarInt(value)
    }

    override fun deserialize0(buf: CustomByteBuf): Int {
        return buf.readVarInt()
    }
}

class CustomByteBuf(buf: ByteBuf) : WrappedByteBuf(buf) {
    fun writeVarInt(value: Int) {
        var v = value
        while ((v and -0x80) != 0) {
            writeByte((v and 0x7F) or 0x80)
            v = v ushr 7
        }
        writeByte(v)
    }

    fun readVarInt(): Int {
        var numRead = 0
        var result = 0
        var read: Byte
        do {
            if (!isReadable) throw IllegalStateException("ByteBuf has no readable bytes for VarInt")
            read = readByte()
            val value = (read.toInt() and 0x7F)
            result = result or (value shl (7 * numRead))

            numRead++
            if (numRead > 5) {
                throw IllegalArgumentException("VarInt is too big")
            }
        } while ((read.toInt() and 0x80) != 0)
        return result
    }
}

class CustomByteBuf2(buf: ByteBuf) : WrappedByteBuf(buf)