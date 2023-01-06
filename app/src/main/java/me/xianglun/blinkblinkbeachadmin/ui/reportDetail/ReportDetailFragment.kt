package me.xianglun.blinkblinkbeachadmin.ui.reportDetail

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.report_card_template.*
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentReportBinding
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentReportDetailBinding
import me.xianglun.blinkblinkbeachadmin.ui.eventDetail.EventDetailFragmentArgs
import me.xianglun.blinkblinkbeachadmin.ui.main.MainActivity
import me.xianglun.blinkblinkbeachadmin.ui.report.ReportAdapter
import me.xianglun.blinkblinkbeachadmin.util.APIState
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ReportDetailFragment : Fragment(R.layout.fragment_report_detail) {
    private val args: ReportDetailFragmentArgs by navArgs()
    private val viewModel: ReportDetailViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentReportDetailBinding.bind(view)

        val report = args.report
        viewModel.fillReporterName(report.reporterID)

        (requireActivity() as MainActivity).supportActionBar?.title = report.id

        binding.apply {

            Glide.with(reportImageView.context)
                .load(report.imageUrl)
                .into(reportImageView)

            reportDetailEditText.setText(report.description)
            reportLocationEditText.setText("${report.latitude}, ${report.longitude}")

            VerifyAndCreateBtn.setOnClickListener {
                val event = Event(
                    latitude = report.latitude,
                    longitude = report.longitude,
                    imageUrl = report.imageUrl
                )
                val action =
                    ReportDetailFragmentDirections.actionReportDetailFragmentToCreateEventFragment(
                        event,
                        report.id
                    )
                findNavController().navigate(action)
            }

            RejectBtn.setOnClickListener {
                viewModel.rejectEvent(report.id)
            }

            viewModel.reporter.observe(viewLifecycleOwner) {
                when (it) {
                    is APIStateWithValue.Error -> {
                        Toast.makeText(
                            context,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is APIStateWithValue.Success -> {
                        reportUsernameEditText.setText(it.result.username)
                    }
                    else -> {}
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.apiState.collect { state ->
                    when (state) {
                        is APIState.Error -> {
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
                                "Report Rejected",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

}