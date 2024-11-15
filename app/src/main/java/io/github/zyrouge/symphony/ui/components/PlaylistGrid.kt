package io.github.zyrouge.symphony.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.zyrouge.symphony.services.groove.Groove
import io.github.zyrouge.symphony.services.groove.repositories.PlaylistRepository
import io.github.zyrouge.symphony.ui.helpers.ViewContext

@Composable
fun PlaylistGrid(
    context: ViewContext,
    playlistIds: List<String>,
    playlistsCount: Int? = null,
    leadingContent: @Composable () -> Unit = {},
) {
    val sortBy by context.symphony.settings.lastUsedPlaylistsSortBy.flow.collectAsState()
    val sortReverse by context.symphony.settings.lastUsedPlaylistsSortReverse.flow.collectAsState()
    val sortedPlaylistIds by remember(playlistIds, sortBy, sortReverse) {
        derivedStateOf {
            context.symphony.groove.playlist.sort(playlistIds, sortBy, sortReverse)
        }
    }

    MediaSortBarScaffold(
        mediaSortBar = {
            Column {
                leadingContent()
                MediaSortBar(
                    context,
                    reverse = sortReverse,
                    onReverseChange = {
                        context.symphony.settings.lastUsedPlaylistsSortReverse.setValue(it)
                    },
                    sort = sortBy,
                    sorts = PlaylistRepository.SortBy.entries
                        .associateWith { x -> ViewContext.parameterizedFn { x.label(it) } },
                    onSortChange = {
                        context.symphony.settings.lastUsedPlaylistsSortBy.setValue(it)
                    },
                    label = {
                        Text(
                            context.symphony.t.XPlaylists(
                                (playlistsCount ?: playlistIds.size).toString()
                            )
                        )
                    },
                )
            }
        },
        content = {
            when {
                playlistIds.isEmpty() -> IconTextBody(
                    icon = { modifier ->
                        Icon(
                            Icons.AutoMirrored.Filled.QueueMusic,
                            null,
                            modifier = modifier,
                        )
                    },
                    content = {
                        Text(context.symphony.t.DamnThisIsSoEmpty)
                    }
                )

                else -> ResponsiveGrid {
                    itemsIndexed(
                        sortedPlaylistIds,
                        key = { i, x -> "$i-$x" },
                        contentType = { _, _ -> Groove.Kind.PLAYLIST }
                    ) { _, playlistId ->
                        context.symphony.groove.playlist.get(playlistId)?.let { playlist ->
                            PlaylistTile(context, playlist)
                        }
                    }
                }
            }
        }
    )
}

private fun PlaylistRepository.SortBy.label(context: ViewContext) = when (this) {
    PlaylistRepository.SortBy.CUSTOM -> context.symphony.t.Custom
    PlaylistRepository.SortBy.TITLE -> context.symphony.t.Title
    PlaylistRepository.SortBy.TRACKS_COUNT -> context.symphony.t.TrackCount
}
