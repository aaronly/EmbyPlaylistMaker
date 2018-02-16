package us.echols.embyplaylistmaker.data.db

import android.arch.persistence.room.*
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.Song

@Entity(
        tableName = "song_playlist_instance",
        foreignKeys = [
            ForeignKey(
                    entity = Song::class,
                    parentColumns = ["id"],
                    childColumns = ["song_id"]
            ),
            ForeignKey(
                    entity = Playlist::class,
                    parentColumns = ["id"],
                    childColumns = ["playlist_id"]
            )],
        indices = [
            Index(name = "song", value = ["song_id"]),
            Index(name = "playlist", value = ["playlist_id"])]
)

class SongPlaylistInstance(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        @ColumnInfo(name = "song_id")
        val songId: String,
        @ColumnInfo(name = "playlist_id")
        val playlistId: String
)