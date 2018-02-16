package us.echols.embyplaylistmaker.data

import android.graphics.Bitmap
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User

interface EmbyDataSource {

    fun refreshAllUsers()

    fun getAllUsers(callback: GetAllUsersCallback)
    interface GetAllUsersCallback {
        fun onAllUsersFound(users: MutableList<User>)
        fun onGetAllUsersFailed(ex: Exception)
    }

    fun getActiveUser(callback: GetActiveUserCallback)
    interface GetActiveUserCallback {
        fun onActiveUserFound(user: User)
        fun onGetActiveUserFailed(ex: Exception)
    }

    fun setActiveUser(user: User)

    fun updateServerAddress()

    fun addUsers(users: List<User>)

    fun getUserImage(user: User, callback: GetUserImageCallback)
    interface GetUserImageCallback {
        fun onUserImageFound(image: Bitmap)
        fun onGetUserImageFailed(ex: Exception)
    }

    fun updateUserImage(user: User, image: Bitmap)

    fun getUserPassword(user: User, callback: GetUserPasswordCallback)
    interface GetUserPasswordCallback {
        fun onUserPasswordFound(password: String)
        fun onGetUserPasswordFailed(ex: Exception)
    }

    fun updateUserPassword(user: User, password: String)

    fun getAccessToken(user: User, callback: GetAccessTokenCallback)
    interface GetAccessTokenCallback {
        fun onAccessTokenFound(token: String)
        fun onGetAccessTokenFailed(ex: Exception)
    }

    fun updateAccessToken(user: User, token: String)

    fun refreshPlaylists()

    fun getPlaylists(user: User, callback: GetPlaylistsCallback)
    interface GetPlaylistsCallback {
        fun onPlaylistsFound(playlists: MutableList<Playlist>)
        fun onGetPlaylistsFailed(ex: Exception)
    }

    fun addPlaylist(user: User, playlist: Playlist, callback: AddPlaylistCallback)
    interface AddPlaylistCallback {
        fun onPlaylistsAdded(id: String)
        fun onAddPlaylistsFailed(ex: Exception)
    }

//    fun getPlaylist(id: Int, callback: GetPlaylistCallback)
//    interface GetPlaylistCallback {
//        fun onPlaylistLoaded(playlist: Playlist)
//        fun onGetActiveUserFailed()
//    }
//
//    fun savePlaylist(playlist: Playlist, callback: SavePlaylistCallback)
//    interface SavePlaylistCallback {
//        fun onPlaylistSaved(playlist: Playlist)
//        fun onGetActiveUserFailed()
//    }
//
//    fun addSongToPlaylist(playlist: Playlist, song: Song, callback: AddSongToPlaylistCallback)
//    fun addSongToPlaylistAt(playlist: Playlist, song: Song, index: Int,
//                            callback: AddSongToPlaylistCallback)
//
//    interface AddSongToPlaylistCallback {
//        fun onSongAddedToPlaylist(playlist: Playlist, song: Song)
//        fun onGetActiveUserFailed()
//    }
//
//    fun changeNameOfPlaylist(playlist: Playlist, newName: String,
//                             callback: ChangeNameOfPlaylistCallback)
//
//    interface ChangeNameOfPlaylistCallback {
//        fun onPlaylistNameChanges(playlist: Playlist)
//        fun onGetActiveUserFailed()
//    }
//
//    fun refreshPlaylists(callback: RefreshPlaylistsCallback)
//    interface RefreshPlaylistsCallback {
//        fun onPlaylistsRefreshed()
//        fun onGetActiveUserFailed()
//    }
//
//    fun deleteAllPlaylists(callback: DeleteAllPlaylistsCallback)
//    interface DeleteAllPlaylistsCallback {
//        fun onPlaylistsDeleted()
//        fun onGetActiveUserFailed()
//    }
//
//    fun deletePlaylist(id: Int, callback: DeletePlaylistCallback)
//    interface DeletePlaylistCallback {
//        fun onPlaylistDeleted(playlist: Playlist)
//        fun onGetActiveUserFailed()
//    }

}
