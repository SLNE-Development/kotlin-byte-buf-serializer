# ByteBuf Serialization for kotlinx.serialization

A Kotlin Serialization format for encoding and decoding data into Netty's `ByteBuf`. This library
provides seamless integration with `kotlinx.serialization`, allowing efficient binary serialization
for network communication and other performance-critical applications.

## 🚀 Features
- Serialize and deserialize objects into `ByteBuf`
- Supports primitive types, collections, and custom objects
- Extensible API for adding custom serializers (for example, VarInt, UUID)
- Type-safe error handling for invalid `ByteBuf` types

---

## 📦 Installation (Gradle Kotlin DSL)

Add the dependency to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://repo.slne.dev/repository/maven-public/") { name = "slne-maven-public" }
}

dependencies {
    implementation("dev.slne.surf:kotlin-byte-buf-serializer:1.0.0") // ships netty-all by default – you may want to exclude it
}
```

Enable Kotlin Serialization in your project:

```kotlin
plugins {
    kotlin("plugin.serialization") version "2.1.10"
}
```

---

## 🔧 Usage

### 1️⃣ Basic Serialization

```kotlin
@Serializable
data class Player(val name: String, val score: Int)

val player = Player("Alice", 42)
val buf: ByteBuf = Unpooled.buffer()

// Encode to ByteBuf
Buf.encodeToBuf(buf, player)

// Decode from ByteBuf
val decodedPlayer = Buf.decodeFromBuf<Player>(buf)
println(decodedPlayer) // Player(name="Alice", score=42)
```

### 2️⃣ Using Byte Arrays

```kotlin
val bytes = Buf.encodeToByteArray(player)
val decodedFromBytes = Buf.decodeFromByteArray<Player>(bytes)
```

### 3️⃣ Custom Serializer Example (VarInt)

```kotlin
class MyCustomByteBuf(private val buf: ByteBuf) : ByteBuf() {
    fun writeVarInt(value: Int) {
        // Custom implementation
    }
    
    fun readVarInt(): Int {
        // Custom implementation
    }
    
    // other ByteBuf methods
}

object VarIntSerializer : KBufSerializer<Int, MyCustomByteBuf> {
    override val descriptor = PrimitiveSerialDescriptor("VarInt", PrimitiveKind.INT)
    override val bufClass = MyCustomByteBuf::class

    override fun serialize0(buf: MyCustomByteBuf, value: Int) {
        buf.writeVarInt(value)
    }

    override fun deserialize0(buf: MyCustomByteBuf): Int {
        return buf.readVarInt()
    }
}
```

Register the custom serializer:

```kotlin
val customModule = SerializersModule {
    contextual(VarIntSerializer)
}
```

---

## 🛠 Advanced Configuration

### Customizing `Buf`

You can create a custom instance of `Buf` with a specific `SerializersModule`:

```kotlin
val bufFormat = Buf(customModule)
```

### Type-Safe Serialization with Error Handling

This library provides **explicit type validation** for `ByteBuf`. If an incorrect type is used, a
clear error message is thrown.

```kotlin
val wrongBuf: ByteBuf = Unpooled.directBuffer()
val encoder = BufEncoder(wrongBuf, customModule, true)

assertFailsWith<IllegalArgumentException> {
    VarIntSerializer.serialize(encoder, 42)
}
```

---

## 📝 License

This project is licensed under the GPL-3.0 license.

---

## 👨‍💻 Contributing

Pull requests are welcome! Feel free to open an issue if you find a bug or have a feature request.
