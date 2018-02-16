package us.echols.embyplaylistmaker.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.Song
import us.echols.embyplaylistmaker.data.model.User

@Database(
        entities = [
            User::class,
            Playlist::class,
            Song::class,
            SongPlaylistInstance::class],
        version = 1
)
abstract class EmbyDb : RoomDatabase() {

    abstract fun embyUserDao(): UserDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun songDao(): SongDao
    abstract fun songPlaylistInstanceDao(): SongPlaylistInstanceDao

}