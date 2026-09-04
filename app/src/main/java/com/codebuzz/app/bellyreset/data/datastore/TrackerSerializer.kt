package com.codebuzz.app.bellyreset.data.datastore

import androidx.datastore.core.Serializer
import com.codebuzz.app.bellyreset.data.model.TrackerState
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object TrackerSerializer : Serializer<TrackerState> {
    override val defaultValue: TrackerState = TrackerState()

    override suspend fun readFrom(input: InputStream): TrackerState {
        return try {
            Json.decodeFromString(
                deserializer = TrackerState.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            serialization.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: TrackerState, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = TrackerState.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}
