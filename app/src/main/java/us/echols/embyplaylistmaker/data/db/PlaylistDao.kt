package us.echols.embyplaylistmaker.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import us.echols.embyplaylistmaker.data.model.Playlist

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Single<MutableList<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlaylist(playlist: Playlist)

}