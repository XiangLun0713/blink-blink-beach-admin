package me.xianglun.blinkblinkbeachadmin.ui.user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_report.*
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentUserBinding
import me.xianglun.blinkblinkbeachadmin.ui.report.ReportAdapter
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue

@AndroidEntryPoint
class UserFragment : Fragment(R.layout.fragment_user) {
    private val viewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentUserBinding.bind(view)

        viewModel.populateUserList()

        binding.apply {
            viewModel.userList.observe(viewLifecycleOwner) {
                when (it) {
                    is APIStateWithValue.Error -> {
                        Toast.makeText(
                            context,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is APIStateWithValue.Success -> {
                        userRecyclerView.adapter =
                            UserAdapter(
                                it.result,
                                requireContext(),
                                findNavController()
                            )
                    }
                    else -> {}
                }
            }
        }
    }
}