package us.echols.embyplaylistmaker.ui

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import org.koin.android.ext.android.inject
import us.echols.embyplaylistmaker.R
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.ui.playlists.PlaylistsContract
import us.echols.embyplaylistmaker.ui.settings.SettingsFragment
import us.echols.embyplaylistmaker.ui.users.UsersContract
import us.echols.embyplaylistmaker.ui.users.UsersFragment
import us.echols.embyplaylistmaker.util.MyLogger
import us.echols.embyplaylistmaker.util.addFragment
import us.echols.embyplaylistmaker.util.replaceFragment

@Suppress("MemberVisibilityCanBePrivate")
class MainActivity : Activity(), MyLogger {

    @BindView(R.id.bottom_nav_bar)
    lateinit var bottomNavBar: BottomNavigationView

    private lateinit var viewUnbinder: Unbinder

    private val fragments = mutableListOf<Fragment>()
    private val usersFragment: UsersContract.View by inject()
    private val playlistsFragment: PlaylistsContract.View by inject()
    private val settingsFragment: SettingsFragment by inject()

    var activeUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        ButterKnife.setDebug(false)
        viewUnbinder = ButterKnife.bind(this)

        if (savedInstanceState == null) {
            fragments.add(usersFragment as Fragment)
            fragments.add(playlistsFragment as Fragment)
            fragments.add(settingsFragment)
            addFragment(R.id.content_frame, usersFragment as Fragment)
        }

        bottomNavBar.setOnNavigationItemSelectedListener listener@ { item ->
            when (item.itemId) {
                R.id.nav_users -> {
                    replaceFragment(R.id.content_frame, usersFragment as Fragment)
                }
                R.id.nav_playlists -> {
                    if (activeUser != null) {
                        replaceFragment(R.id.content_frame, playlistsFragment as Fragment)
                        activeUser?.let { user -> playlistsFragment.setUser(user) }
                    } else {
                        (fragments[0] as UsersFragment).getActiveUser()
                    }
                }
                R.id.nav_settings -> {
                    replaceFragment(R.id.content_frame, settingsFragment)
                }
                else -> return@listener false
            }
            return@listener true
        }
    }

    fun userSelected(user: User, redirectToPlaylists: Boolean) {
        activeUser = user
        if (redirectToPlaylists) {
            bottomNavBar.selectedItemId = R.id.nav_playlists
        }
    }

    override fun onDestroy() {
        viewUnbinder.unbind()
        super.onDestroy()
    }
}


