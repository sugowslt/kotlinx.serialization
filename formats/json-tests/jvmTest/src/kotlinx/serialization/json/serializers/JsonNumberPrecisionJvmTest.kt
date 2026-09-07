/*
 * Copyright 2017-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.json.serializers

import kotlinx.serialization.json.*
import kotlin.test.*

class JsonNumberPrecisionJvmTest : JsonTestBase() {
    @Test
    fun testParsedLongNumberIsNotTruncated() = parametrizedTest { mode ->
        val source = "1.000000000000000000000000001"
        val element = default.decodeFromString(JsonElement.serializer(), source, mode)

        assertEquals(source, default.encodeToString(JsonElement.serializer(), element, mode), "mode:$mode")
    }

    @Test
    fun testBigDecimalPrecision() = parametrizedTest { mode ->
        val value = "1.000000000000000000000000001".toBigDecimal()
        val original = buildJsonObject { put("number", value) }
        val encoded = default.encodeToString(JsonObject.serializer(), original, mode)

        assertEquals("{\"number\":$value}", encoded, "mode:$mode")
        assertEquals(original, default.decodeFromString(JsonObject.serializer(), encoded, mode))
    }

    @Test
    fun testBigIntegerBeyondUnsignedLongRange() = parametrizedTest { mode ->
        val value = "18446744073709551617".toBigInteger()
        val original = JsonPrimitive(value)
        val encoded = default.encodeToString(JsonPrimitive.serializer(), original, mode)

        assertEquals(value.toString(), encoded, "mode:$mode")
        assertEquals(original, default.decodeFromString(JsonPrimitive.serializer(), encoded, mode))
    }

    @Test
    fun testBigDecimalUnderflow() = parametrizedTest { mode ->
        val value = "1E-400".toBigDecimal()
        val original = JsonPrimitive(value)
        val encoded = default.encodeToString(JsonPrimitive.serializer(), original, mode)

        assertEquals(value.toString(), encoded, "mode:$mode")
        assertEquals(original, default.decodeFromString(JsonPrimitive.serializer(), encoded, mode))
    }

    @Test
    fun testSpecialFloatingPointValueValidation() {
        val value = JsonPrimitive(Double.NaN)

        assertFailsWith<JsonEncodingException> {
            default.encodeToString(JsonPrimitive.serializer(), value)
        }
        assertEquals("NaN", lenient.encodeToString(JsonPrimitive.serializer(), value))
    }
}
