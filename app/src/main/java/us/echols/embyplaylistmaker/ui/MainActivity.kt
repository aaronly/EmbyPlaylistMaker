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

class MainActivity : Activity(), MyLogger {

    @BindView(R.id.bottom_nav_bar)
    lateinit var bottomNavBar: BottomNavigationView
    private var navBarSelectedItem: Int = 0

    private lateinit var viewUnbinder: Unbinder

    private val fragments = mutableListOf<Fragment>()
    private val usersFragment: UsersContract.View by inject()
    private val playlistsFragment: PlaylistsContract.View by inject()
    private val settingsFragment: SettingsFragment by inject()

    private val usersFragmentKey = "users_fragment"
    private val playlistsFragmentKey = "playlists_fragment"
    private val settingsFragmentKey = "settings_fragment"

    private var activeUser: User? = null

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
        } else {
            //bottomNavBar.selectedItemId = navBarSelectedItem
//            val fragment = fragmentManager.getFragment(savedInstanceState, "activeFragment")
//            replaceFragment(R.id.content_frame, fragment)
//            bottomNavBar.selectedItemId = savedInstanceState.getInt("bottom_nav_position")
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

    fun userSelected(user: User) {
        activeUser = user
        bottomNavBar.selectedItemId = R.id.nav_playlists
    }
//    override fun onPause() {
////        presenter.detachView()
//        super.onPause()
//    }

//    override fun onResume() {
//        super.onResume()
//
////        hideSystemUI()
//
////        if (presenter.view == null) {
////            presenter.attachView(this)
////        }
//    }

//    private fun hideSystemUI() {
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or // hide status bar
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or // show bars with swipe
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or // don't resize status bar area
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE) // stabilize the UI
//    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        //navBarSelectedItem = bottomNavBar.selectedItemId
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        val fragment = fragmentManager.getFragment(savedInstanceState, "activeFragment")
//        replaceFragment(R.id.content_frame, fragment)
//    }

    override fun onDestroy() {
        viewUnbinder.unbind()
        super.onDestroy()
    }
}


