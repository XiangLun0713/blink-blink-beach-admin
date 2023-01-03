package me.xianglun.blinkblinkbeachadmin.ui.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentReportBinding

@AndroidEntryPoint
class ReportFragment : Fragment(R.layout.fragment_report) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentReportBinding.bind(view)

        binding.apply {

        }
    }
}