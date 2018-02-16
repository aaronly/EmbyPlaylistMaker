package us.echols.embyplaylistmaker.di

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.koin.dsl.module.applicationContext
import us.echols.embyplaylistmaker.ui.playlists.PlaylistsAdapter
import us.echols.embyplaylistmaker.ui.playlists.PlaylistsContract
import us.echols.embyplaylistmaker.ui.playlists.PlaylistsFragment
import us.echols.embyplaylistmaker.ui.playlists.PlaylistsPresenter
import us.echols.embyplaylistmaker.ui.settings.SettingsFragment
import us.echols.embyplaylistmaker.ui.users.UsersAdapter
import us.echols.embyplaylistmaker.ui.users.UsersContract
import us.echols.embyplaylistmaker.ui.users.UsersFragment
import us.echols.embyplaylistmaker.ui.users.UsersPresenter

val appModule = applicationContext {

    factory { PlaylistsFragment() as PlaylistsContract.View }
    factory { PlaylistsPresenter(get(), get()) as PlaylistsContract.Presenter }
    factory { PlaylistsAdapter(get()) }

    factory { UsersFragment() as UsersContract.View }
    factory { UsersPresenter(get(), get(), get()) as UsersContract.Presenter }
    factory { UsersAdapter(get()) }

    factory { SettingsFragment() }

    factory { LinearLayoutManager(get()) as RecyclerView.LayoutManager }
}

