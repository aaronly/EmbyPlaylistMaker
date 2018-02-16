package us.echols.embyplaylistmaker.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "songs")
class Song(
        @PrimaryKey
        @SerializedName("Id")
        val id: String,
        @SerializedName("Name")
        val name: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val song = other as Song?

        return id == song!!.id && name == song.name
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }

    override fun toString(): String {
        return name
    }


}
