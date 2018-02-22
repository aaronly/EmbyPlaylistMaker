package us.echols.embyplaylistmaker.ui.playlists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import us.echols.embyplaylistmaker.R
import us.echols.embyplaylistmaker.data.model.Playlist
import us.echols.embyplaylistmaker.ui.common.BaseAdapter
import us.echols.embyplaylistmaker.ui.playlists.PlaylistsAdapter.ViewHolder

class PlaylistsAdapter constructor(private val context: Context) : BaseAdapter<ViewHolder>() {

    var playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.playlist_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]

        val textView = holder.textView
        textView.text = playlist.name
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun addPlaylist(playlist: Playlist) {
        playlists.add(playlist)
        notifyItemInserted(playlists.indexOf(playlist))
    }

    fun replacePlaylists(playlists: List<Playlist>) {
        this.playlists = playlists.toMutableList()
        notifyDataSetChanged()
    }

    fun getPlaylist(position: Int): Playlist {
        return playlists[position]
    }

    inner class ViewHolder(listView: View) : BaseAdapter<ViewHolder>.ViewHolder(listView) {

        @BindView(R.id.text_playlist_name)
        lateinit var textView: TextView

        init {
            ButterKnife.bind(this, listView)
        }

    }

}