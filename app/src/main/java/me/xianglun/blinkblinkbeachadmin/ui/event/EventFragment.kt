package me.xianglun.blinkblinkbeachadmin.ui.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentEventBinding

@AndroidEntryPoint
class EventFragment : Fragment(R.layout.fragment_event) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEventBinding.bind(view)
        binding.apply {

        }
    }
}