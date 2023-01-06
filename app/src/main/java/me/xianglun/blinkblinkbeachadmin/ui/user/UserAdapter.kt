package me.xianglun.blinkblinkbeachadmin.ui.user

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.data.model.User
import me.xianglun.blinkblinkbeachadmin.databinding.ReportCardTemplateBinding
import me.xianglun.blinkblinkbeachadmin.databinding.UserCardTemplateBinding
import me.xianglun.blinkblinkbeachadmin.ui.report.ReportFragmentDirections
import me.xianglun.blinkblinkbeachadmin.util.ReportStatus

class UserAdapter(
    private val userList: List<User>,
    private val context: Context,
    private val navController: NavController
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(val binding: UserCardTemplateBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = UserCardTemplateBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.apply {
            Glide.with(context)
                .load(Uri.parse(user.profileImageUrl))
                .placeholder(R.drawable.profile_placeholder)
                .into(userCardImageView)

            userIdText.text = user.id
            userNameText.text = user.username
            root.setOnClickListener {
                val action =
                    UserFragmentDirections.actionUserFragmentToUserDetailFragment(user)
                navController.navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}