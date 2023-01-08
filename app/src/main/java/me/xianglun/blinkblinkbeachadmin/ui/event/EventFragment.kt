package me.xianglun.blinkblinkbeachadmin.ui.event

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentEventBinding
import me.xianglun.blinkblinkbeachadmin.util.APIState

@AndroidEntryPoint
class EventFragment : Fragment(R.layout.fragment_event) {

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.event_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_send_cert) {
            viewModel.onSendCertButtonClicked()
            true
        } else {
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEventBinding.bind(view)

        binding.apply {

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.apiState.collect { event ->
                    when (event) {
                        is APIState.Error -> {
                            Toast.makeText(
                                this@EventFragment.requireActivity(),
                                event.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is APIState.Loading -> {
                            Toast.makeText(
                                this@EventFragment.requireActivity(),
                                "Sending certificate...",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is APIState.Success -> {
                            // navigate back to event list page
                            findNavController().navigateUp()
                            // display success message
                            Toast.makeText(
                                this@EventFragment.requireActivity(),
                                "Certificate sent successfully.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }


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