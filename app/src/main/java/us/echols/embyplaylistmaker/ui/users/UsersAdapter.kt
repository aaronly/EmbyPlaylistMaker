package us.echols.embyplaylistmaker.ui.users

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import us.echols.embyplaylistmaker.R
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.ui.common.BaseAdapter

class UsersAdapter constructor(private val context: Context) :
        BaseAdapter<UsersAdapter.ViewHolder>() {

    var users: MutableList<User> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.user_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        val textView = holder.textView
        textView.text = user.name

        val red: Int
        val primaryTextColor: Int
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            red = context.resources.getColor(android.R.color.holo_red_dark, context.theme)
            primaryTextColor = context.resources.getColor(
                    android.R.color.primary_text_light, context.theme
            )
        } else {
            red = context.resources.getColor(android.R.color.holo_red_dark)
            primaryTextColor = context.resources.getColor(android.R.color.primary_text_light)
        }
        val color = if (user.lastActive) {
            red
        } else {
            primaryTextColor
        }

        textView.setTextColor(color)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun addUser(user: User) {
        users.add(user)
        notifyItemInserted(users.indexOf(user))
    }

    fun replaceUsers(users: List<User>) {
        this.users = users.toMutableList()
        notifyDataSetChanged()
    }

    fun getUser(position: Int): User {
        return users[position]
    }

    fun setActiveUser(position: Int) {
        users.forEach { user -> user.lastActive = false }
        users[position].lastActive = true
        notifyDataSetChanged()
    }

    inner class ViewHolder(listView: View) : BaseAdapter<ViewHolder>.ViewHolder(listView) {

        @BindView(R.id.text_user_name)
        lateinit var textView: TextView

        init {
            ButterKnife.bind(this, listView)
        }

    }

}