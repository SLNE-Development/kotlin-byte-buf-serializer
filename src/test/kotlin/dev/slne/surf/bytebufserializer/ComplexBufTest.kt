package dev.slne.surf.bytebufserializer

import io.netty.buffer.Unpooled
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class ComplexBufTest {

    @Serializable
    data class ComplexPlayer(
        val name: String,
        val score: Int,
        val isOnline: Boolean,
        val playTime: PlayTime,
        val games: List<Game>,
        val map: Map<String, Int>,
    )

    @Serializable
    data class PlayTime(val hours: Int = 0, val minutes: Int = 0, val seconds: Int = 10)

    @Serializable
    data class Game(val player: ComplexPlayer, val playTime: PlayTime)

    private val bufFormat = Buf.Default

    @Test
    fun `test encode and decode to ByteArray`() {
        val player = ComplexPlayer(
            "Alice",
            42,
            true,
            PlayTime(1, 2, 3),
            listOf(
                Game(
                    ComplexPlayer("Bob", 99, false, PlayTime(4, 5, 6), emptyList(), emptyMap()),
                    PlayTime(7, 8, 9)
                ),
                Game(
                    ComplexPlayer(
                        "Charlie",
                        100,
                        true,
                        PlayTime(10, 11, 12),
                        emptyList(),
                        emptyMap()
                    ), PlayTime()
                )
            ),
            mapOf("a" to 1, "b" to 2, "c" to 3),
        )

        val bytes = bufFormat.encodeToByteArray(player)
        val decodedPlayer = bufFormat.decodeFromByteArray<ComplexPlayer>(bytes)

        assertEquals(player, decodedPlayer)
    }

    @Test
    fun `test encode and decode to ByteBuf`() {
        val player = ComplexPlayer(
            "Alice",
            42,
            true,
            PlayTime(1, 2, 3),
            listOf(
                Game(
                    ComplexPlayer("Bob", 99, false, PlayTime(4, 5, 6), emptyList(), emptyMap()),
                    PlayTime(7, 8, 9)
                ),
                Game(
                    ComplexPlayer(
                        "Charlie",
                        100,
                        true,
                        PlayTime(10, 11, 12),
                        emptyList(),
                        emptyMap()
                    ), PlayTime()
                )
            ),
            mapOf("a" to 1, "b" to 2, "c" to 3),
        )

        val buf = Unpooled.buffer()
        bufFormat.encodeToBuf(buf, player)
        val decodedPlayer = bufFormat.decodeFromBuf<ComplexPlayer>(buf)

        assertEquals(player, decodedPlayer)
    }
}