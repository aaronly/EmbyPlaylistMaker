package us.echols.embyplaylistmaker.ui.playlists

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindString
import butterknife.BindView
import butterknife.OnClick
import org.jetbrains.anko.design.longSnackbar
import org.koin.android.ext.android.inject
import us.echols.embyplaylistmaker.R
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.ui.common.BaseAdapter
import us.echols.embyplaylistmaker.ui.common.BaseFragment
import us.echols.embyplaylistmaker.util.KoinComponent
import us.echols.embyplaylistmaker.util.inflate

class PlaylistsFragment : BaseFragment(), PlaylistsContract.View, KoinComponent {

    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView

    @BindString(R.string.playlists_none_found)
    lateinit var noPlaylistsFound: String

    override val presenter: PlaylistsContract.Presenter by inject()
    private val adapter: PlaylistsAdapter by inject()
    private val layoutManager: RecyclerView.LayoutManager by inject()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.playlists_fragment)
    }

    override fun onResume() {
        super.onResume()
        if (presenter.view == null) {
            presenter.attachView(this)
        }

        initializeRecyclerView()

    }

    override fun onPause() {
        super.onPause()
        recyclerView.layoutManager = null
    }

    @OnClick(R.id.fab)
    fun onFabClicked() {
        showSnackbar("You clicked the FAB!")
    }

    override fun setUser(user: User) {
        presenter.user = user
    }

    private fun initializeRecyclerView() {
        adapter.setOnItemClickListener(object : BaseAdapter.OnItemClickListener {
            override fun onClick(position: Int) = onItemClick(position)
        })

        adapter.setOnItemLongClickListener(object : BaseAdapter.OnItemLongClickListener {
            override fun onLongClick(position: Int) = onItemLongClick(position)
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
    }

    private fun onItemClick(position: Int) {
        presenter.onPlaylistClicked(adapter.getPlaylist(position))
    }

    private fun onItemLongClick(position: Int) {
        presenter.onPlaylistClicked(adapter.getPlaylist(position))
    }

    override fun showPlaylists(playlists: List<Playlist>?) {
        if (playlists != null) {
            adapter.replacePlaylists(playlists)
        } else {
            showSnackbar(noPlaylistsFound)
        }
    }

    override fun showSnackbar(message: String) {
        longSnackbar(view, message)
    }

}
