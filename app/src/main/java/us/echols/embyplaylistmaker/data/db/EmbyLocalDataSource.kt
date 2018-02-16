package us.echols.embyplaylistmaker.data.db

import android.graphics.Bitmap
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import us.echols.embyplaylistmaker.data.EmbyDataSource
import us.echols.embyplaylistmaker.data.FileHelper
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.util.MyLogger
import us.echols.embyplaylistmaker.util.info
import java.io.FileNotFoundException

class EmbyLocalDataSource(private val db: EmbyDb, private val fileHelper: FileHelper) :
        EmbyDataSource, MyLogger {

    private val notFoundInDatabase = "could not be found in the local database."

    override fun refreshAllUsers() {
        /* no-op */
    }

    override fun getAllUsers(callback: EmbyDataSource.GetAllUsersCallback) {
        db.embyUserDao().getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { users ->
                            callback.onAllUsersFound(users)
                        },
                        {
                            info("Users $notFoundInDatabase")
                            callback.onGetAllUsersFailed(it as Exception)
                        })
    }

    override fun getActiveUser(callback: EmbyDataSource.GetActiveUserCallback) {
        db.embyUserDao().getActiveUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { user ->
                            callback.onActiveUserFound(user)
                        },
                        {
                            info("Active user $notFoundInDatabase")
                            callback.onGetActiveUserFailed(it as Exception)
                        })
    }

    override fun setActiveUser(user: User) {
        Observable.just(user).singleOrError()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ activeUser ->
                               db.embyUserDao().setAllUserInactive()
                               db.embyUserDao().setActiveUser(activeUser)
                           })

    }

    override fun getUserImage(user: User, callback: EmbyDataSource.GetUserImageCallback) {
        try {
            if (user.imageId.isNullOrEmpty()) {
                callback.onGetUserImageFailed(FileNotFoundException())
            } else {
                val image = fileHelper.getUserImage(user)
                if (image != null) {
                    callback.onUserImageFound(image)
                } else {
                    callback.onGetUserImageFailed(FileNotFoundException())
                }
            }
        } catch (ex: Exception) {
            callback.onGetUserImageFailed(ex)
        }
    }

    override fun updateUserImage(user: User, image: Bitmap) {
        fileHelper.saveUserImage(user, image)
    }

    override fun getUserPassword(user: User, callback: EmbyDataSource.GetUserPasswordCallback) {
        db.embyUserDao().getUserPassword(user.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { password -> callback.onUserPasswordFound(password) },
                        { ex ->
                            info("User password $notFoundInDatabase")
                            callback.onGetUserPasswordFailed(ex as Exception)
                        })
    }

    override fun updateUserPassword(user: User, password: String) {
        val userToken: Pair<String, String> = Pair(user.id, password)
        Observable.just(userToken).singleOrError()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ p ->
                               db.embyUserDao().updateUserPassword(p.first, p.second)
                           })
    }

    override fun addUsers(users: List<User>) {
        Observable.fromArray(users).singleOrError()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ newUsers ->
                               db.embyUserDao().addUsers(newUsers)
                           })
    }

    override fun updateServerAddress() {
        /* no-op: remote database method only */
    }

    override fun getAccessToken(user: User, callback: EmbyDataSource.GetAccessTokenCallback) {
        db.embyUserDao().getAccessToken(user.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { token -> callback.onAccessTokenFound(token) },
                        { ex ->
                            info("Access token $notFoundInDatabase")
                            callback.onGetAccessTokenFailed(ex as Exception)
                        })
    }

    override fun updateAccessToken(user: User, token: String) {
        val userToken: Pair<String, String> = Pair(user.id, token)
        Observable.just(userToken).singleOrError()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ p ->
                               db.embyUserDao().updateAccessToken(p.first, p.second)
                           })
    }

    override fun getPlaylists(user: User, callback: EmbyDataSource.GetPlaylistsCallback) {
        db.playlistDao().getAllPlaylists()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { playlists -> callback.onPlaylistsFound(playlists) },
                        {
                            info("Playlists $notFoundInDatabase")
                            callback.onGetPlaylistsFailed(it as Exception)
                        })
    }

    override fun refreshPlaylists() {
        /* no-op */
    }

    override fun addPlaylist(
            user: User, playlist: Playlist,
            callback: EmbyDataSource.AddPlaylistCallback
    ) {
        Observable.just(playlist).singleOrError()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                               db.playlistDao().addPlaylist(playlist)
                               callback.onPlaylistsAdded(playlist.id)
                           }, {
                               callback.onAddPlaylistsFailed(it as Exception)
                           })
    }
}
