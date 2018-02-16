package us.echols.embyplaylistmaker.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

@Dao
interface SongPlaylistInstanceDao {

    @Query("SELECT * FROM song_playlist_instance")
    fun getAllSongs(): Single<MutableList<SongPlaylistInstance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSong(songs: List<SongPlaylistInstance>)

}