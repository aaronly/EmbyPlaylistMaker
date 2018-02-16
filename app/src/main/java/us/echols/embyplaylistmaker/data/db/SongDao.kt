package us.echols.embyplaylistmaker.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import us.echols.embyplaylistmaker.data.model.Song

@Dao
interface SongDao {

    @Query("SELECT * FROM songs")
    fun getAllSongs(): Single<MutableList<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSong(songs: List<Song>)

}