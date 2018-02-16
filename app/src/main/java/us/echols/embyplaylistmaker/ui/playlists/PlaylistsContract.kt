package us.echols.embyplaylistmaker.ui.playlists

import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.ui.common.BaseContract

interface PlaylistsContract {

    interface View : BaseContract.View<Presenter> {

        fun setUser(user: User)

        fun showPlaylists(playlists: List<Playlist>?)

        fun showSnackbar(message: String)
    }

    interface Presenter : BaseContract.Presenter<View> {

        var user: User?

        fun loadPlaylists(forceUpdate: Boolean)

        fun onPlaylistClicked(playlist: Playlist)

        fun onPlaylistLongClicked(playlist: Playlist)

    }

}
