package com.bracketcove.graphsudoku.persistence

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.bracketcove.graphsudoku.GameSettings
import com.bracketcove.graphsudoku.Statistics
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * GameSettings Datastore
 * Use DataStore instead of SharedPreferences
 * Create a DataStore object taking in ProtoCol buffer generated java class [GameSettings]
 * Creates a reference for which we can use store/retrieve our buffer.
 */
internal val Context.settingsDataStore: DataStore<GameSettings> by dataStore(
    fileName = "game_settings.pb",
    serializer = GameSettingsSerializer,
)

/**
 * Serializer - serialization in this context means translating JVM classes and
 * fields (Kotlin or Java) into a serialization language like JSON or ProtoBuff
 * */
private object GameSettingsSerializer : Serializer<GameSettings> {
    override val defaultValue: GameSettings
        get() = GameSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): GameSettings {
        try {
            return GameSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: GameSettings, output: OutputStream) = t.writeTo(output)
}

internal val Context.statisticsDataStore: DataStore<Statistics> by dataStore(
    fileName = "game_statistics.pb",
    serializer = StatisticsSerializer,
)

/**
 * Statistics DataStore
 * */
private object StatisticsSerializer : Serializer<Statistics> {
    override val defaultValue: Statistics
        get() = Statistics.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Statistics {
        try {
            return Statistics.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Statistics, output: OutputStream) = t.writeTo(output)
}