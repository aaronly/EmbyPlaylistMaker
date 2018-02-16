package us.echols.embyplaylistmaker.data.network

import android.graphics.Bitmap
import com.androidnetworking.error.ANError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import us.echols.embyplaylistmaker.data.EmbyDataSource
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User
import java.io.FileNotFoundException

class EmbyRemoteDataSource(private val embyApi: EmbyApiHelper) : EmbyDataSource, AnkoLogger {

    private val notFoundOnRemoteServer = "could not be found on the Emby server."

    override fun refreshAllUsers() {
        /* no-op */
    }

    override fun getAllUsers(callback: EmbyDataSource.GetAllUsersCallback) {
        embyApi.allUsers
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { users -> callback.onAllUsersFound(users) },
                        { ex ->
                            if (ex is ANError) {
                                info("Users $notFoundOnRemoteServer")
                                callback.onGetAllUsersFailed(ex)
                            } else {
                                throw ex
                            }
                        })
    }

    override fun getUserImage(user: User, callback: EmbyDataSource.GetUserImageCallback) {
        if (!user.imageId.isNullOrEmpty()) {
            embyApi.getUserImage(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { image -> callback.onUserImageFound(image) },
                            { ex ->
                                if (ex is ANError) {
                                    info("User image $notFoundOnRemoteServer")
                                    callback.onGetUserImageFailed(ex)
                                } else {
                                    throw ex
                                }
                            })
        } else {
            callback.onGetUserImageFailed(
                    FileNotFoundException("That user has no image assigned.")
            )
        }
    }

    override fun updateUserImage(user: User, image: Bitmap) {
        /* no-op local database method only */
    }

    override fun getActiveUser(callback: EmbyDataSource.GetActiveUserCallback) {
        /* no-op: local database method only */
    }

    override fun setActiveUser(user: User) {
        /* no-op: local database method only */
    }

    override fun addUsers(users: List<User>) {
        /* no-op: local database method only */
    }

    override fun getUserPassword(user: User, callback: EmbyDataSource.GetUserPasswordCallback) {
        /* no-op: local database method only */
    }

    override fun updateUserPassword(user: User, password: String) {
        /* no-op: local database method only */
    }

    override fun updateServerAddress() {
        embyApi.updateUrl()
    }

    override fun getAccessToken(user: User, callback: EmbyDataSource.GetAccessTokenCallback) {
        embyApi.authenticateUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ newUser ->
                               val token = newUser.token
                               if (token != null) {
                                   callback.onAccessTokenFound(token)
                               } else {
                                   info("Access token $notFoundOnRemoteServer")
                                   callback.onGetAccessTokenFailed(
                                           Exception("Access token $notFoundOnRemoteServer")
                                   )
                               }
                           },
                           { ex ->
                               info("Access token $notFoundOnRemoteServer")
                               if (ex is ANError) {
                                   callback.onGetAccessTokenFailed(ex)
                               } else {
                                   throw ex
                               }
                           })
    }

    override fun updateAccessToken(user: User, token: String) {
        /* no-op: local database method only */
    }

    override fun getPlaylists(user: User, callback: EmbyDataSource.GetPlaylistsCallback) {
        embyApi.getPlaylists(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { playlists -> callback.onPlaylistsFound(playlists) },
                        { ex ->
                            info("Playlists $notFoundOnRemoteServer")
                            if (ex is ANError) {
                                callback.onGetPlaylistsFailed(ex)
                            } else {
                                throw ex
                            }
                        })
    }

    override fun refreshPlaylists() {
        /* no-op */
    }

    override fun addPlaylist(
            user: User, playlist: Playlist,
            callback: EmbyDataSource.AddPlaylistCallback
    ) {
        embyApi.addPlaylist(user, playlist)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ id -> callback.onPlaylistsAdded(id) },
                           { ex ->
                               info("Could not add playlist to remote server.")
                               if (ex is ANError) {
                                   callback.onAddPlaylistsFailed(ex)
                               } else {
                                   throw ex
                               }
                           })
    }
}
