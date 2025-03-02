package dev.slne.surf.bytebufserializer

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BufTest {

    @Serializable
    data class Player(val name: String, val score: Int)

    private val bufFormat = Buf.Default

    @Test
    fun `test encode and decode to ByteArray`() {
        val player = Player("Alice", 42)

        val bytes = bufFormat.encodeToByteArray(player)
        val decodedPlayer = bufFormat.decodeFromByteArray<Player>(bytes)

        assertEquals(player, decodedPlayer)
    }

    @Test
    fun `test encode and decode to ByteBuf`() {
        val player = Player("Bob", 99)
        val buf: ByteBuf = Unpooled.buffer()

        bufFormat.encodeToBuf(buf, player)
        val decodedPlayer = bufFormat.decodeFromBuf<Player>(buf)

        assertEquals(player, decodedPlayer)
    }

    @Test
    fun `test decode from empty ByteArray should fail`() {
        assertFailsWith<Exception> {
            val emptyBytes = byteArrayOf()
            bufFormat.decodeFromByteArray<Player>(emptyBytes)
        }
    }

    @Test
    fun `test decode from empty ByteBuf should fail`() {
        assertFailsWith<Exception> {
            val emptyBuf: ByteBuf = Unpooled.buffer()
            bufFormat.decodeFromBuf<Player>(emptyBuf)
        }
    }

}