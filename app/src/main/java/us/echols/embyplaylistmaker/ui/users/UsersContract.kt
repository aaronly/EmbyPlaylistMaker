package us.echols.embyplaylistmaker.ui.users

import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.ui.common.BaseContract

interface UsersContract {

    interface View : BaseContract.View<Presenter> {

        fun showUsers(users: List<User>)

        fun addUser(user: User)

        fun getActiveUser()

        fun getUserPassword()

        fun tokenFound(user: User)

        fun getNewServerAddress(oldAddress: String)

    }

    interface Presenter : BaseContract.Presenter<View> {

        var activeUser: User?

        fun getAllUsers(ipAddress: String?)

        fun updateActiveUser(user: User)

        fun updateActiveUserPassword(password: String)

        fun updateServerAddress(newAddress: String)

        fun onUserClicked(user: User)

    }

}
