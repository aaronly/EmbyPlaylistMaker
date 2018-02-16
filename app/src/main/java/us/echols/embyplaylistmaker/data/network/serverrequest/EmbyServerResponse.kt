package us.echols.embyplaylistmaker.data.network.serverrequest

import com.google.gson.annotations.SerializedName

class EmbyServerResponse(
        @field:SerializedName("Address") private val address: String,
        @field:SerializedName("Id") val id: String,
        @field:SerializedName("Name") val name: String
) {

    override fun toString(): String {
        return "EmbyServerResponse{address='$address', id='$id', name='$name'}"
    }

}
