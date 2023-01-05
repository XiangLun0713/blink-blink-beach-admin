package me.xianglun.blinkblinkbeachadmin.ui.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentEventBinding

@AndroidEntryPoint
class EventFragment : Fragment(R.layout.fragment_event) {

    private val viewModel: EventViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEventBinding.bind(view)

        binding.apply {

            fun subscribeRecyclerViewToPastEvent() {
                viewModel.pastEvents.observe(viewLifecycleOwner) { pastEvents ->
                    // save recycler view state
                    val recyclerViewState =
                        eventsRecyclerView.layoutManager?.onSaveInstanceState()

                    // update recycler view
                    eventsRecyclerView.adapter =
                        EventAdapter(
                            pastEvents,
                            requireContext(),
                            findNavController()
                        )

                    // restore recycler view state
                    eventsRecyclerView.layoutManager?.onRestoreInstanceState(
                        recyclerViewState)
                }
            }

            fun subscribeRecyclerViewToFutureEvent() {
                viewModel.futureEvents.observe(viewLifecycleOwner) { futureEvents ->
                    // save recycler view state
                    val recyclerViewState =
                        eventsRecyclerView.layoutManager?.onSaveInstanceState()

                    // update recycler view
                    eventsRecyclerView.adapter =
                        EventAdapter(
                            futureEvents,
                            requireContext(),
                            findNavController()
                        )

                    // restore recycler view state
                    eventsRecyclerView.layoutManager?.onRestoreInstanceState(
                        recyclerViewState)
                }
            }

            subscribeRecyclerViewToFutureEvent()

            eventTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> {
                            subscribeRecyclerViewToFutureEvent()
                        }
                        1 -> {
                            subscribeRecyclerViewToPastEvent()
                        }
                        else -> {}
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
    }
}