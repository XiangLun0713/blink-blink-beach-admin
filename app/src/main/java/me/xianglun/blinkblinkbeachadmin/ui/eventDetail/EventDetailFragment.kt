package me.xianglun.blinkblinkbeachadmin.ui.eventDetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.databinding.DialogConfirmDeletionBinding
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentEventDetailBinding
import me.xianglun.blinkblinkbeachadmin.util.APIState
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EventDetailFragment : Fragment(R.layout.fragment_event_detail), OnMapReadyCallback {

    private lateinit var eventMapView: MapView
    private lateinit var scrollView: ScrollView
    private lateinit var event: Event
    private val args: EventDetailFragmentArgs by navArgs()
    private val viewModel: EventDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEventDetailBinding.bind(view)

        scrollView = binding.eventScrollView
        eventMapView = binding.eventMapView
        event = args.event

        // set up google map view
        binding.eventMapView.onCreate(savedInstanceState)
        binding.eventMapView.getMapAsync(this)

        binding.apply {
            viewModel.loadEventImage(eventImageView, event.imageUrl)
            eventHeaderTitleTextView.text = event.name
            eventDateTextView.text = requireContext().resources.getString(
                R.string.date_with_comma,
                getDateFromMillis(event.startTimeMillis)
            )
            eventTimeTextView.text = requireContext().resources.getString(
                R.string.time_with_comma,
                getEventTime(event.startTimeMillis, event.endTimeMillis)
            )
            eventTitleTextView.text = event.name
            eventAddressTextView.text = event.address
            eventDetailTextView.text = event.detail.replace("\\n", "\n")
            deleteEventButton.setOnClickListener {
                // inflate alert dialog box
                val customDialogBinding = DialogConfirmDeletionBinding.inflate(layoutInflater)
                // build and show alert dialog box
                val builder = AlertDialog.Builder(activity)
                    .setView(customDialogBinding.root)
                val alertDialog = builder.show()
                // view binding
                customDialogBinding.apply {
                    dialogCancelButton.setOnClickListener {
                        alertDialog.dismiss()
                    }
                    dialogDeleteButton.setOnClickListener {
                        alertDialog.dismiss()
                        viewModel.onDeleteEventButtonClick(event.eventID)
                    }
                }
            }
            editEventButton.setOnClickListener {
                val action = EventDetailFragmentDirections.actionGlobalEditEventFragment(event)
                findNavController().navigate(action)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.apiState.collect { event ->
                    when (event) {
                        is APIState.Error -> {
                            // hide the circular progress indicator
                            eventDetailProgressCardView.visibility = View.INVISIBLE
                            // display error toast message
                            Toast.makeText(
                                this@EventDetailFragment.requireActivity(),
                                event.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is APIState.Loading -> {
                            // show the circular progress indicator
                            eventDetailProgressCardView.visibility = View.VISIBLE
                        }
                        is APIState.Success -> {
                            // hide the circular progress indicator
                            eventDetailProgressCardView.visibility = View.INVISIBLE
                            // navigate back to event list page
                            findNavController().navigateUp()
                            // display success message
                            Toast.makeText(
                                this@EventDetailFragment.requireActivity(),
                                "Event deleted successfully.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun getDateFromMillis(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        return dateFormat.format(calendar.time)
    }

    private fun getEventTime(startTime: Long, endTime: Long) =
        "${convertMillisecondsToTimeString(startTime)} - ${convertMillisecondsToTimeString(endTime)}"

    private fun convertMillisecondsToTimeString(milliseconds: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val amPm = if (calendar[Calendar.AM_PM] == Calendar.AM) "AM" else "PM"

        val hours = calendar[Calendar.HOUR]
        val minutes = calendar[Calendar.MINUTE]

        return "$hours:${minutes.toString().padStart(2, '0')} $amPm"
    }

    override fun onMapReady(p0: GoogleMap) {
        p0.let { map ->
            // temporary disable the scroll listener of the scroll view when the user is interacting with the map
            map.setOnCameraMoveStartedListener {
                scrollView.requestDisallowInterceptTouchEvent(true)
            }
            map.setOnCameraIdleListener {
                scrollView.requestDisallowInterceptTouchEvent(false)
            }

            val position = LatLng(event.latitude, event.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 15f)
            val markerOptions = MarkerOptions()
                .position(position)
                .title(event.name)
            // add marker on map
            map.addMarker(markerOptions)
            // move the camera to focus on the marker
            map.moveCamera(cameraUpdate)
        }
    }

    override fun onResume() {
        super.onResume()
        eventMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        eventMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventMapView.onDestroy()
    }
}