package me.xianglun.blinkblinkbeachadmin.ui.eventDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentEventDetailBinding

@AndroidEntryPoint
class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEventDetailBinding.bind(view)

        binding.apply {

        }
    }
}