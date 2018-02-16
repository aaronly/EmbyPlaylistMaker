package us.echols.embyplaylistmaker.ui.playlists

import us.echols.embyplaylistmaker.data.EmbyDataSource
import us.echols.embyplaylistmaker.data.EmbyRepository
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.data.prefs.PreferencesHelper

class PlaylistsPresenter(
        private val embyRepository: EmbyRepository,
        private val preferencesHelper: PreferencesHelper
) : PlaylistsContract.Presenter {

    override var view: PlaylistsContract.View? = null

    override var user: User? = null
        set(user) {
            field = user
            // TODO: 12/29/2017 not sure this should be true
            loadPlaylists(true)
        }

//    override fun attachView(view: PlaylistsContract.View) {
//        super.attachView(view)
//        loadPlaylists(true)
//    }

    override fun loadPlaylists(forceUpdate: Boolean) {
        val user = this.user

        if (user?.token != null) {
            if (forceUpdate) {
                embyRepository.refreshPlaylists()
            }

            embyRepository.getPlaylists(user, object : EmbyDataSource.GetPlaylistsCallback {
                override fun onPlaylistsFound(playlists: MutableList<Playlist>) {
                    view?.showPlaylists(playlists)
                }

                override fun onGetPlaylistsFailed(ex: Exception) {
                    view?.showPlaylists(null)
                }
            })
        }
    }

    override fun onPlaylistClicked(playlist: Playlist) {
        view?.showSnackbar("You clicked on ${playlist.name}")
    }

    override fun onPlaylistLongClicked(playlist: Playlist) {
        view?.showSnackbar("You long clicked on ${playlist.name}")
    }

}
