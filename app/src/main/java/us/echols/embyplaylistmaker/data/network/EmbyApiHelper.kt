package us.echols.embyplaylistmaker.data.network

import android.graphics.Bitmap
import io.reactivex.Observable
import io.reactivex.Single
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User

interface EmbyApiHelper {

    var baseUrl: String

    val allUsers: Observable<MutableList<User>>

    fun updateUrl()

    fun getUserImage(user: User): Observable<Bitmap>

    fun authenticateUser(user: User): Single<User>

    fun getPlaylists(user: User): Observable<MutableList<Playlist>>

    fun addPlaylist(user: User, playlist: Playlist): Single<String>
}
