package us.echols.embyplaylistmaker.data.network

import android.graphics.Bitmap
import com.google.gson.Gson
import com.rx2androidnetworking.BuildConfig
import com.rx2androidnetworking.Rx2ANRequest
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONObject
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.data.prefs.PreferencesHelper

class EmbyApiClient(private val preferencesHelper: PreferencesHelper) : EmbyApiHelper {

    override lateinit var baseUrl: String

    override val allUsers: Observable<MutableList<User>>
        get() = Rx2AndroidNetworking
                .get("$baseUrl/Users/Public")
                .build()
                .getObjectListObservable(User::class.java)

    init {
        updateUrl()
    }

    override fun updateUrl() {
        val ipAddress = preferencesHelper.serverAddress
        val port = preferencesHelper.serverPort
        baseUrl = "http://$ipAddress:$port"
    }

    override fun getUserImage(user: User): Observable<Bitmap> {
        return if (user.id.isNotEmpty()) {
            Rx2AndroidNetworking
                    .get("$baseUrl/Users/${user.id}/Images/Primary")
                    .build()
                    .bitmapObservable
        } else {
            Observable.empty()
        }
    }

    override fun authenticateUser(user: User): Single<User> {
        val pwJson = JSONObject()
        pwJson.put("pw", user.pw ?: "")
        pwJson.put("Username", user.name)

        var request = Rx2AndroidNetworking
                .post("$baseUrl/Users/AuthenticateByName")
                .addJSONObjectBody(pwJson)

        request = addAuth(user, request)

        return request.build()
                .getObjectObservable(User::class.java)
                .singleOrError()
    }

    override fun getPlaylists(user: User): Observable<MutableList<Playlist>> {
        var request = Rx2AndroidNetworking
                .get("$baseUrl/Users/${user.id}/Items?Recursive=true&IncludeItemTypes=Playlist")

        request = addToken(user.token ?: "", request)

        return request.build().jsonObjectObservable
                .map { itemsJson -> itemsJson.getJSONArray("Items") }
                .map { itemList ->
                    return@map (0..(itemList.length() - 1))
                            .map { i -> itemList[i].toString() }
                            .map { jsonString -> Gson().fromJson(jsonString, Playlist::class.java) }
                            .toMutableList()
                }
    }

    override fun addPlaylist(user: User, playlist: Playlist): Single<String> {
        var request = Rx2AndroidNetworking
                .post(
                        "$baseUrl/Playlists?UserId=${user.id}&" +
                                "Name=${playlist.name}&MediaType=Audio"
                )

        request = addAuth(user, request)

        return request.build().jsonObjectObservable
                .map { jsonId -> jsonId.get("Id").toString() }
                .singleOrError()
    }

    private fun addAuth(user: User, request: Rx2ANRequest.PostRequestBuilder)
            : Rx2ANRequest.PostRequestBuilder {

        val device = android.os.Build.DEVICE
        val deviceId = android.os.Build.ID
        val version = BuildConfig.VERSION_NAME

        val authHeader = "MediaBrowser UserId=\"${user.id}\",Client=\"Android\"," +
                "Device=\"$device\",DeviceId=\"$deviceId\",Version=\"$version\""

        return request
                .addHeaders("Authorization", authHeader)
                .setContentType("application/json")
    }

    private fun addToken(token: String, request: Rx2ANRequest.GetRequestBuilder)
            : Rx2ANRequest.GetRequestBuilder {
        return request.addHeaders("X-MediaBrowser-Token", token)
    }

}
