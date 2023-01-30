package io.github.zyrouge.symphony.services.database

import io.github.zyrouge.symphony.Symphony
import io.github.zyrouge.symphony.services.database.adapters.FileDatabaseAdapter
import io.github.zyrouge.symphony.services.groove.Playlist
import io.github.zyrouge.symphony.utils.toList
import org.json.JSONArray
import org.json.JSONObject
import java.nio.file.Paths

class PlaylistsBox(val symphony: Symphony) {
    data class Data(
        val custom: List<Playlist>,
        val local: List<Playlist.Local>,
    ) {
        fun toJSONObject() = JSONObject().apply {
            put(CUSTOM, JSONArray(custom.map { it.toJSONObject() }))
            put(LOCAL, JSONArray(local.map { it.toJSONObject() }))
        }

        companion object {
            private const val CUSTOM = "0"
            private const val LOCAL = "1"

            fun fromJSONObject(json: JSONObject) = json.run {
                Data(
                    custom = json.getJSONArray(CUSTOM)
                        .toList { Playlist.fromJSONObject(getJSONObject(it)) },
                    local = json.getJSONArray(LOCAL)
                        .toList { Playlist.Local.fromJSONObject(getJSONObject(it)) },
                )
            }
        }
    }

    private val adapter = FileDatabaseAdapter(
        Paths
            .get(symphony.applicationContext.dataDir.absolutePath, "playlists.json")
            .toFile()
    )

    fun read(): Data {
        val content = adapter.read()
        return Data.fromJSONObject(JSONObject(content))
    }

    fun update(value: Data) {
        adapter.overwrite(value.toJSONObject().toString())
    }
}

