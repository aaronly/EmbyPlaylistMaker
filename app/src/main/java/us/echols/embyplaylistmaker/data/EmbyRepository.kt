package us.echols.embyplaylistmaker.data

import android.arch.persistence.room.EmptyResultSetException
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.util.MyLogger
import us.echols.embyplaylistmaker.util.verbose
import java.io.FileNotFoundException

class EmbyRepository internal constructor(
        private val remoteDataSource: EmbyDataSource,
        private val localDataSource: EmbyDataSource
) : EmbyDataSource, MyLogger {

    private val finding = "Retrieving"
    private val updating = "Updating"
    private val found = "Found"
    private val notFound = "Could not find"
    private val inMemory = "in memory."
    private val inLocal = "in local source."
    private val inRemote = "in remote source."
    private val outOfDate = "needs to be updated."

    private var userListIsDirty = false
    private var users = mutableListOf<User>()
    private lateinit var activeUser: User
    private var tokenIsOutOfDate = false

    private var playlistsIsDirty = false
    private var playlists = mutableListOf<Playlist>()

    override fun refreshAllUsers() {
        userListIsDirty = true
    }

    private fun refreshUserList(users: MutableList<User>) {
        verbose("$updating ${users.size} users $inMemory")
        this.users = users
        userListIsDirty = false
    }

    override fun getAllUsers(callback: EmbyDataSource.GetAllUsersCallback) {

        if (!userListIsDirty && users.isNotEmpty()) {
            verbose("User list $inMemory")
            callback.onAllUsersFound(users)
            return
        }

        if (userListIsDirty) {
            verbose("User list $outOfDate")
            getAllUsersFromRemoteSource(callback)
        } else {

            verbose("$finding user list $inLocal")
            localDataSource.getAllUsers(object : EmbyDataSource.GetAllUsersCallback {
                override fun onAllUsersFound(users: MutableList<User>) {
                    if (users.size > 0) {
                        verbose("User list $found $inLocal")
                        refreshUserList(users)
                        callback.onAllUsersFound(users)
                    } else {
                        getAllUsersFromRemoteSource(callback)
                    }
                }

                override fun onGetAllUsersFailed(ex: Exception) {
                    verbose("$notFound user list $inLocal")
                    if (ex is SQLiteException) {
                        getAllUsersFromRemoteSource(callback)
                    } else {
                        callback.onGetAllUsersFailed(ex)
                    }
                }
            })

        }
    }

    private fun getAllUsersFromRemoteSource(callback: EmbyDataSource.GetAllUsersCallback) {
        verbose("$finding user list $inRemote")

        remoteDataSource.getAllUsers(object : EmbyDataSource.GetAllUsersCallback {
            override fun onAllUsersFound(users: MutableList<User>) {
                verbose("User list $found $inRemote")
                refreshUserList(users)
                localDataSource.addUsers(users)
                callback.onAllUsersFound(users)
            }

            override fun onGetAllUsersFailed(ex: Exception) {
                verbose("$notFound user list $inRemote")
                callback.onGetAllUsersFailed(ex)
            }
        })
    }

    override fun getUserImage(user: User, callback: EmbyDataSource.GetUserImageCallback) {
        verbose("$finding user image $inLocal")

        localDataSource.getUserImage(user, object : EmbyDataSource.GetUserImageCallback {
            override fun onUserImageFound(image: Bitmap) {
                verbose("$found user image $inLocal")
                callback.onUserImageFound(image)
            }

            override fun onGetUserImageFailed(ex: Exception) {
                verbose("$notFound user image $inLocal")
                if (ex is FileNotFoundException) {
                    getUserImageFromRemoteSource(user, callback)
                } else {
                    callback.onGetUserImageFailed(ex)
                }
            }
        })
    }

    private fun getUserImageFromRemoteSource(
            user: User,
            callback: EmbyDataSource.GetUserImageCallback
    ) {
        verbose("$finding user image $inRemote")

        remoteDataSource.getUserImage(user, object : EmbyDataSource.GetUserImageCallback {
            override fun onUserImageFound(image: Bitmap) {
                verbose("$found user image $inRemote")
                localDataSource.updateUserImage(user, image)
                callback.onUserImageFound(image)
            }

            override fun onGetUserImageFailed(ex: Exception) {
                verbose("$notFound user image $inRemote")
                callback.onGetUserImageFailed(ex)
            }
        })
    }

    override fun updateUserImage(user: User, image: Bitmap) {
        verbose("$updating user image $inLocal")
        localDataSource.updateUserImage(user, image)
    }

    override fun getActiveUser(callback: EmbyDataSource.GetActiveUserCallback) {
        if (::activeUser.isInitialized) {
            verbose("Active user $inMemory")
            callback.onActiveUserFound(activeUser)
        } else {
            verbose("Active user $outOfDate")
            localDataSource.getActiveUser(object : EmbyDataSource.GetActiveUserCallback {
                override fun onActiveUserFound(user: User) {
                    verbose("$found active user $inLocal")
                    activeUser = user
                    callback.onActiveUserFound(user)
                }

                override fun onGetActiveUserFailed(ex: Exception) {
                    verbose("$notFound active user $inLocal")
                    callback.onGetActiveUserFailed(ex)
                }
            })
        }
    }

    override fun setActiveUser(user: User) {
        verbose("$updating active user to ${user.name} $inLocal")
        activeUser = user
        localDataSource.setActiveUser(user)
    }

    override fun addUsers(users: List<User>) {
        verbose("Adding ${users.size} users $inLocal")
        userListIsDirty = true
        localDataSource.addUsers(users)
    }

    override fun getUserPassword(user: User, callback: EmbyDataSource.GetUserPasswordCallback) {
        if (isActiveUserPasswordInMemory()) {
            verbose("User password $inMemory")
            callback.onUserPasswordFound(activeUser.pw ?: "")
        } else if (!activeUser.hasPassword) {
            verbose("Current active user has no password.")
            activeUser.setPasswords("")
            localDataSource.updateUserPassword(activeUser, "")
        } else {
            verbose("$finding user password $inLocal")
            localDataSource.getUserPassword(user, object : EmbyDataSource.GetUserPasswordCallback {
                override fun onUserPasswordFound(password: String) {
                    verbose("$found user password $inLocal")
                    activeUser = user
                    activeUser.setPasswords(password)
                    callback.onUserPasswordFound(password)
                }

                override fun onGetUserPasswordFailed(ex: Exception) {
                    verbose("$notFound user password $inLocal")
                    callback.onGetUserPasswordFailed(ex)
                }
            })
        }
    }

    private fun isActiveUserPasswordInMemory(): Boolean {
        return ::activeUser.isInitialized &&
                activeUser.hasPassword &&
                activeUser.pw != null &&
                !activeUser.pw.equals("")
    }

    override fun updateUserPassword(user: User, password: String) {
        verbose("$updating user password for ${user.name} $inLocal")
        activeUser = user
        activeUser.setPasswords(password)
        localDataSource.updateUserPassword(user, password)
    }

    override fun updateServerAddress() {
        verbose { "$updating server address for remote server." }
        remoteDataSource.updateServerAddress()
    }

    override fun getAccessToken(user: User, callback: EmbyDataSource.GetAccessTokenCallback) {

        if (!tokenIsOutOfDate && ::activeUser.isInitialized) {
            val token = activeUser.token
            if (token != null && token.isNotEmpty()) {
                verbose("Access token $inMemory")
                callback.onAccessTokenFound(token)
            } else {
                verbose("Access token $outOfDate")
                authenticateUserOnServer(user, callback)
            }
            return
        }

        if (tokenIsOutOfDate) {
            verbose("Access token $outOfDate")
            authenticateUserOnServer(user, callback)
        } else {
            verbose("$finding access token $inLocal")

            localDataSource.getAccessToken(user, object : EmbyDataSource.GetAccessTokenCallback {
                override fun onAccessTokenFound(token: String) {
                    verbose("$found access token in $inLocal")
                    refreshToken(user, token)
                    callback.onAccessTokenFound(token)
                }

                override fun onGetAccessTokenFailed(ex: Exception) {
                    verbose("$notFound access token in $inLocal")
                    if (ex is EmptyResultSetException) {
                        authenticateUserOnServer(user, callback)
                    } else {
                        callback.onGetAccessTokenFailed(ex)
                    }
                }
            })
        }
    }

    private fun authenticateUserOnServer(
            user: User,
            callback: EmbyDataSource.GetAccessTokenCallback
    ) {
        verbose("Authenticating user on server...")

        remoteDataSource.getAccessToken(user, object : EmbyDataSource.GetAccessTokenCallback {
            override fun onAccessTokenFound(token: String) {
                verbose("Active user has been authenticated.")
                refreshToken(user, token)
                localDataSource.updateAccessToken(user, token)
                callback.onAccessTokenFound(token)
            }

            override fun onGetAccessTokenFailed(ex: Exception) {
                verbose("Active user could not be authenticated.")
                callback.onGetAccessTokenFailed(ex)
            }
        })
    }

    override fun updateAccessToken(user: User, token: String) {
        verbose("$updating access token for ${user.name} $inLocal")
        localDataSource.updateAccessToken(user, token)
    }

    private fun refreshToken(user: User, token: String) {
        verbose("$updating access token $inMemory")
        activeUser = user
        activeUser.token = token
        tokenIsOutOfDate = false
    }

    override fun getPlaylists(user: User, callback: EmbyDataSource.GetPlaylistsCallback) {
        if (!playlistsIsDirty && playlists.isNotEmpty()) {
            verbose("Playlists $inMemory")
            callback.onPlaylistsFound(playlists)
            return
        }

        if (playlistsIsDirty) {
            verbose("Playlists $outOfDate")
            getPlaylistsFromRemoteSource(user, callback)
        } else {

            verbose("$finding playlists $inLocal")
            localDataSource.getPlaylists(user, object : EmbyDataSource.GetPlaylistsCallback {
                override fun onPlaylistsFound(playlists: MutableList<Playlist>) {
                    if (playlists.size > 0) {
                        verbose("Playlists $found $inLocal")
                        refreshPlaylists(playlists)
                        callback.onPlaylistsFound(playlists)
                    } else {
                        getPlaylistsFromRemoteSource(user, callback)
                    }
                }

                override fun onGetPlaylistsFailed(ex: Exception) {
                    verbose("$notFound playlists $inLocal")
                    if (ex is SQLiteException) {
                        getPlaylistsFromRemoteSource(user, callback)
                    } else {
                        callback.onGetPlaylistsFailed(ex)
                    }
                }
            })

        }
    }

    private fun getPlaylistsFromRemoteSource(
            user: User, callback: EmbyDataSource
            .GetPlaylistsCallback
    ) {
        verbose("$finding playlists $inRemote")

        remoteDataSource.getPlaylists(user, object : EmbyDataSource.GetPlaylistsCallback {
            override fun onPlaylistsFound(playlists: MutableList<Playlist>) {
                verbose("Playlists $found $inRemote")
                refreshPlaylists(playlists)
                playlists.forEach { playlist -> addPlaylistToLocalSource(user, playlist, null) }
                callback.onPlaylistsFound(playlists)
            }

            override fun onGetPlaylistsFailed(ex: Exception) {
                verbose("$notFound playlists $inRemote")
                callback.onGetPlaylistsFailed(ex)
            }

        })
    }

    override fun refreshPlaylists() {
        playlistsIsDirty = true
    }

    private fun refreshPlaylists(playlists: MutableList<Playlist>) {
        verbose("$updating ${playlists.size} playlists $inMemory")
        this.playlists = playlists
        playlistsIsDirty = false
    }

    override fun addPlaylist(
            user: User, playlist: Playlist,
            callback: EmbyDataSource.AddPlaylistCallback
    ) {
        verbose("Adding playlist ($playlist) $inRemote")

        remoteDataSource.addPlaylist(user, playlist, object : EmbyDataSource.AddPlaylistCallback {
            override fun onPlaylistsAdded(id: String) {
                verbose("Added playlist ($playlist) $inRemote")
                addPlaylistToLocalSource(user, Playlist(id, playlist.name), callback)
            }

            override fun onAddPlaylistsFailed(ex: Exception) {
                verbose("Could not add playlist ($playlist) $inRemote")
                callback.onAddPlaylistsFailed(ex)
            }

        })
    }

    private fun addPlaylistToLocalSource(
            user: User, playlist: Playlist,
            callback: EmbyDataSource.AddPlaylistCallback?
    ) {
        verbose("Adding playlist ($playlist) $inLocal")

        localDataSource.addPlaylist(
                user, playlist,
                object : EmbyDataSource.AddPlaylistCallback {
                    override fun onPlaylistsAdded(id: String) {
                        verbose("Added playlist ($playlist) $inLocal")
                        callback?.onPlaylistsAdded(id)
                    }

                    override fun onAddPlaylistsFailed(ex: Exception) {
                        verbose("Could not add playlist ($playlist) $inLocal")
                        callback?.onAddPlaylistsFailed(ex)
                    }

                })

    }
}
