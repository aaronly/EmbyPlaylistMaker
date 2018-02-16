package us.echols.embyplaylistmaker.ui.users

import io.reactivex.disposables.CompositeDisposable
import us.echols.embyplaylistmaker.data.EmbyDataSource
import us.echols.embyplaylistmaker.data.EmbyRepository
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.data.network.serverrequest.EmbyServerRequest
import us.echols.embyplaylistmaker.data.prefs.PreferencesHelper
import us.echols.embyplaylistmaker.util.MyLogger
import us.echols.embyplaylistmaker.util.info
import java.io.IOException

class UsersPresenter(
        private val embyRepository: EmbyRepository,
        private val preferencesHelper: PreferencesHelper,
        private val serverRequest: EmbyServerRequest
) : UsersContract.Presenter, MyLogger {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private var firstTime: Boolean = true

    override var view: UsersContract.View? = null

    override var activeUser: User? = null
        set(user) {
            user?.lastActive = true
            field = user
            info("Active user has been set to ${user?.name}")
        }

    var allUsers = mutableListOf<User>()

    override fun attachView(view: UsersContract.View) {
        super.attachView(view)

        getAllUsers(preferencesHelper.serverAddress)

    }

    override fun getAllUsers(ipAddress: String?) {
        embyRepository.getAllUsers(
                object : EmbyDataSource.GetAllUsersCallback {
                    override fun onAllUsersFound(users: MutableList<User>) {
                        allUsers = users
                        view?.showUsers(users)
                        findActiveUser()
                    }

                    override fun onGetAllUsersFailed(ex: Exception) {
                        checkServerAddress(ipAddress ?: preferencesHelper.serverAddress)
                    }
                }
        )
    }

    override fun updateActiveUser(user: User) {
        user.lastActive = true
        activeUser = user
        embyRepository.setActiveUser(user)

        if (user.hasPassword) {
            getUserPassword(user)
        } else {
            getAccessToken(user)
        }
    }

    override fun updateActiveUserPassword(password: String) {
        activeUser?.setPasswords(password)
        activeUser?.let { embyRepository.updateUserPassword(it, password) }
        getAccessToken(activeUser)
    }

    override fun onUserClicked(user: User) {
        updateActiveUser(user)
    }

    override fun updateServerAddress(newAddress: String) {
        preferencesHelper.serverAddress = newAddress
        checkServerAddress(newAddress)
    }

    private fun findActiveUser() {
        if (activeUser == null) {
            embyRepository.getActiveUser(
                    object : EmbyDataSource.GetActiveUserCallback {
                        override fun onActiveUserFound(user: User) {
                            updateActiveUser(user)
                        }

                        override fun onGetActiveUserFailed(ex: Exception) {
                            view?.getActiveUser()
                        }
                    }
            )
        }
    }

    private fun getUserPassword(user: User) {
        embyRepository.getUserPassword(
                user, object : EmbyDataSource.GetUserPasswordCallback {
            override fun onUserPasswordFound(password: String) {
                getAccessToken(user)
            }

            override fun onGetUserPasswordFailed(ex: Exception) {
                view?.getUserPassword()
            }
        }
        )
    }

    private fun getAccessToken(user: User?) {
        if (user != null)
            embyRepository.getAccessToken(
                    user, object : EmbyDataSource.GetAccessTokenCallback {
                override fun onAccessTokenFound(token: String) {
                    info("Access token found: $token")
                    activeUser?.token = token
                    activeUser?.let {
                        view?.tokenFound(it)
                    }
                }

                override fun onGetAccessTokenFailed(ex: Exception) {
                    throw ex
                }
            }
            )
    }

    private fun checkServerAddress(ipAddress: String) {
        firstTime = false

        serverRequest.address = ipAddress
        val disposable = serverRequest.getResponse()
                .subscribe(
                        {
                            onServerFound(ipAddress)
                        },
                        { ex ->
                            if (ex is IOException) {
                                view?.getNewServerAddress(ipAddress)
                            } else {
                                throw ex
                            }
                        }
                )
        disposables.add(disposable)
    }

    private fun onServerFound(ipAddress: String) {
        info("Server url has been updated to $ipAddress and Emby Server was found.")
        embyRepository.updateServerAddress()

        if (allUsers.isEmpty()) {
            getAllUsers(ipAddress)
        }
    }
}
