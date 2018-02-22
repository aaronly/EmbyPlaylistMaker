package us.echols.embyplaylistmaker.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "playlists")
class Playlist(
        @PrimaryKey
        @SerializedName("Id")
        override val id: String,
        @SerializedName("Name")
        override val name: String
) : EmbyItem, Parcelable {

    @Ignore
    private var songs: MutableList<Song> = mutableListOf()

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()
    )

//    private fun copySongList(songs: List<Song>): List<Song> {
//        val output = ArrayList<Song>()
//        Collections.copy(output, songs)
//        return output
//    }
//
//    fun addSong(song: Song): List<Song> {
//        songs.add(song)
//        return copySongList(songs)
//    }
//
//    fun addSongAt(index: Int, song: Song): List<Song> {
//        var fixedIndex = index
//        if (index > songs.size - 1) {
//            fixedIndex = songs.size
//        } else if (index < 0) {
//            fixedIndex = 0
//        }
//        songs.add(fixedIndex, song)
//
//        return copySongList(songs)
//    }
//
//    fun removeSong(song: Song): List<Song>? {
//        songs.remove(song)
//        return copySongList(songs)
//    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Playlist

        if (id != other.id) return false
        if (name != other.name) return false
        if (songs != other.songs) return false

        return true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(parcel: Parcel): Playlist {
            return Playlist(parcel)
        }

        override fun newArray(size: Int): Array<Playlist?> {
            return arrayOfNulls(size)
        }
    }
}
