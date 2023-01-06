package me.xianglun.blinkblinkbeachadmin.ui.userDetail

import android.app.Activity
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_user_detail.*
import kotlinx.android.synthetic.main.user_card_template.*
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentUserDetailBinding
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue

@AndroidEntryPoint
class UserDetailFragment : Fragment(R.layout.fragment_user_detail) {
    private val args: UserDetailFragmentArgs by navArgs()
    private val viewModel: UserDetailViewModel by viewModels()
    private var fileUri: Uri? = null
    private lateinit var binding: FragmentUserDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserDetailBinding.bind(view)

        val user = args.user

        binding.apply {
            Glide.with(profileImageView.context)
                .load(user.profileImageUrl)
                .placeholder(R.drawable.profile_placeholder)
                .into(profileImageView)

            emailEditText.setText(user.email)
            usernameEditText.setText(user.username)

            profileImageLayout.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop()
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }

            saveBtn.setOnClickListener {
                if (usernameEditText.text?.isEmpty() == true) {
                    usernameEditText.error = "Username cannot be empty"
                    return@setOnClickListener
                }

                viewModel.saveUserProfile(user.id, fileUri, usernameEditText.text.toString())
                saveBtn.isEnabled = false
                deleteBtn.isEnabled = false
            }

            deleteBtn.setOnClickListener {
                viewModel.deleteUser(user.id)
                saveBtn.isEnabled = false
                deleteBtn.isEnabled = false
            }

            viewModel.user.observe(viewLifecycleOwner) {
                when (it) {
                    is APIStateWithValue.Error -> {
                        saveBtn.isEnabled = true
                        deleteBtn.isEnabled = true
                        Toast.makeText(
                            context,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is APIStateWithValue.Success -> {
                        findNavController().navigateUp()
                        Toast.makeText(
                            context,
                            "User Profile Updated",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {}
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.apiState.collect { state ->
                    when (state) {
                        is APIState.Error -> {
                            saveBtn.isEnabled = true
                            deleteBtn.isEnabled = true
                            Toast.makeText(
                                context,
                                state.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is APIState.Loading -> {
                        }
                        is APIState.Success -> {
                            findNavController().navigateUp()
                            Toast.makeText(
                                context,
                                "User Deleted",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    fileUri = data?.data!!
                    Glide.with(binding.profileImageView.context)
                        .load(fileUri)
                        .into(binding.profileImageView)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
}