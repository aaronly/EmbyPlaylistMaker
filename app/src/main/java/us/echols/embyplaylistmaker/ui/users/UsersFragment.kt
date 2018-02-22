package us.echols.embyplaylistmaker.ui.users

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindString
import butterknife.BindView
import org.jetbrains.anko.*
import org.koin.android.ext.android.inject
import us.echols.embyplaylistmaker.R
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.ui.MainActivity
import us.echols.embyplaylistmaker.ui.common.BaseAdapter
import us.echols.embyplaylistmaker.ui.common.BaseFragment
import us.echols.embyplaylistmaker.util.KoinComponent
import us.echols.embyplaylistmaker.util.MyLogger
import us.echols.embyplaylistmaker.util.inflate

class UsersFragment : BaseFragment(), UsersContract.View, KoinComponent, MyLogger {

    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView

    //    @BindString(R.string.login_dialog_username_hint)
//    lateinit var usernameHint: String
    @BindString(R.string.login_dialog_password_hint)
    lateinit var passwordHint: String
//    @BindString(R.string.login_dialog_no_id)
//    lateinit var noId: String

    @BindString(R.string.select_active_user)
    lateinit var selectUserText: String

    @BindString(R.string.server_emby_server_address)
    lateinit var serverDialogMessage: String
    @BindString(R.string.setting_server_default)
    lateinit var serverDefault: String

    @BindString(R.string.bundle_key_user_list)
    lateinit var userListBundleKey: String

    override val presenter: UsersContract.Presenter by inject()
    private val adapter: UsersAdapter by inject()
    private val layoutManager: RecyclerView.LayoutManager by inject()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.users_fragment)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelableArray(userListBundleKey, adapter.users.toTypedArray())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val userList = savedInstanceState?.getParcelableArray(userListBundleKey)
        userList?.forEach { user ->
            if (user is User) adapter.addUser(user)
        }
    }

    override fun onResume() {
        super.onResume()

        initializeRecyclerView()

        if (presenter.view == null) {
            presenter.attachView(this)
        }
    }

    override fun onPause() {
        super.onPause()
        recyclerView.layoutManager = null
    }

    override fun showUsers(users: List<User>) {
        adapter.replaceUsers(users)
    }

    override fun addUser(user: User) {
        adapter.addUser(user)
    }

    override fun getActiveUser() {
        activity.toast(selectUserText)
    }

    override fun getUserPassword() {
        alert {
            var editText: EditText? = null
            customView {
                linearLayout {
                    padding = dip(16)
                    editText = editText {
                        singleLine = true
                        hint = passwordHint
                        transformationMethod = PasswordTransformationMethod.getInstance()
                    }.lparams(width = matchParent)
                }
            }
            okButton { dialog ->
                val password = editText?.text.toString()
                presenter.updateActiveUserPassword(password)
                dialog.dismiss()
            }
            cancelButton { dialog -> dialog.cancel() }
        }.show()
    }

    override fun tokenFound(user: User) {
        if (activity is MainActivity) {
            (activity as MainActivity).userSelected(user, false)
        }
    }

    override fun getNewServerAddress(oldAddress: String) {
        alert(serverDialogMessage) {
            var editText: EditText? = null
            customView {
                linearLayout {
                    editText = editText(oldAddress) {
                        singleLine = true
                        hint = serverDefault
                    }.lparams(width = matchParent)
                    padding = dip(16)
                }
            }
            okButton { dialog ->
                val address = editText?.text.toString()
                presenter.updateServerAddress(address)
                dialog.dismiss()
            }
            cancelButton { dialog -> dialog.cancel() }
        }.show()
    }

    private fun initializeRecyclerView() {
        adapter.setOnItemClickListener(
                object : BaseAdapter.OnItemClickListener {
                    override fun onClick(position: Int) = onItemClick(position)
                }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )
    }

    private fun onItemClick(position: Int) {
        val user = adapter.getUser(position)
        adapter.setActiveUser(position)
        presenter.onUserClicked(user)
        if (activity is MainActivity) {
            (activity as MainActivity).userSelected(user, true)
        }
    }


//    private fun getUsername() {
//        alert {
//            var editText: EditText? = null
//            customView {
//                linearLayout {
//                    padding = dip(16)
//                    editText = editText {
//                        singleLine = true
//                        hint = usernameHint
//                    }.lparams(width = matchParent)
//                }
//            }
//            okButton { dialog ->
//                val username = editText?.text.toString()
//                val user = User(noId, username, false, null)
//                user.lastActive = true
////                usersFragment.addUser(user)
//                presenter.updateActiveUser(user)
//                dialog.dismiss()
//            }
//            cancelButton { dialog -> dialog.cancel() }
//        }.show()
//    }
}
