package me.xianglun.blinkblinkbeachadmin.ui.createEvent

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.data.model.Event
import me.xianglun.blinkblinkbeachadmin.databinding.FragmentCreateEventBinding
import me.xianglun.blinkblinkbeachadmin.util.APIStateWithValue
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateEventFragment : Fragment(R.layout.fragment_create_event), OnMapReadyCallback {
    private val viewModel: CreateEventViewModel by viewModels()
    private val args: CreateEventFragmentArgs by navArgs()
    private lateinit var event: Event
    private lateinit var reportId: String
    private lateinit var eventMapView: MapView
    private lateinit var scrollView: ScrollView
    private lateinit var markerOptions: MarkerOptions
    private lateinit var marker: Marker
    private lateinit var editTextImageView: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateEventBinding.bind(view)

        scrollView = binding.createEventScrollView
        eventMapView = binding.eventMapView
        editTextImageView = binding.createEventImageView
        event = args.event
        reportId = args.reportId

        // set up google map view
        binding.eventMapView.onCreate(savedInstanceState)
        binding.eventMapView.getMapAsync(this)

        binding.apply {
            viewModel.initializeDateTime(event.startTimeMillis, event.endTimeMillis)
            viewModel.initializeLatLng(event.latitude, event.longitude)
            viewModel.initializeEventName(event.name)
            viewModel.initializeEventImageUrl(event.imageUrl)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { datePicker, year, month, day ->
                    viewModel.onDateSet(dateEditText, year, month, day)
                },
                viewModel.year.value!!,
                viewModel.month.value!!,
                viewModel.day.value!!
            )

            val startTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, hour, minute ->
                    viewModel.onStartTimeSet(startTimeEditText, hour, minute)
                },
                viewModel.startHour.value!!,
                viewModel.startMinute.value!!,
                false
            )

            val endTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, hour, minute ->
                    viewModel.onEndTimeSet(endTimeEditText, hour, minute)
                },
                viewModel.endHour.value!!,
                viewModel.endMinute.value!!,
                false
            )

            Glide.with(createEventImageView.context)
                .load(event.imageUrl)
                .into(createEventImageView)

            createEventHeader.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop()
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }

            viewModel.eventName.observe(viewLifecycleOwner) { eventName ->
                eventHeaderTitleTextView.text = eventName
            }

            viewModel.latLngName.observe(viewLifecycleOwner) { (latlng, name) ->
                addressEditText.setText(getAddressFromLatLng(requireContext(), latlng))
            }

            datePickerButton.setOnClickListener {
                datePickerDialog.show()
            }

            startTimePickerButton.setOnClickListener {
                startTimePicker.show()
            }

            endTimePickerButton.setOnClickListener {
                endTimePicker.show()
            }

            eventNameEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.onEventNameEditTextTextChange(text)
            }

            latitudeEditText.setText(event.latitude.toString())
            latitudeEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.onLatitudeEditTextTextChange(text)
            }

            longitudeEditText.setText(event.longitude.toString())
            longitudeEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.onLongitudeEditTextTextChange(text)
            }

            createEventButton.setOnClickListener {

                val startTimeMillis = getStartTimeMillisFromCalendar(
                    viewModel.year.value!!,
                    viewModel.month.value!!,
                    viewModel.day.value!!,
                    viewModel.startHour.value!!,
                    viewModel.startMinute.value!!
                )

                val endTimeMillis = getStartTimeMillisFromCalendar(
                    viewModel.year.value!!,
                    viewModel.month.value!!,
                    viewModel.day.value!!,
                    viewModel.endHour.value!!,
                    viewModel.endMinute.value!!
                )

                val eventName = eventNameEditText.text.toString()
                val address = addressEditText.text.toString()
                val detail = detailEditText.text.toString()
                val latitude = latitudeEditText.text.toString()
                val longitude = longitudeEditText.text.toString()

                eventNameEditText.error = null
                addressEditText.error = null
                detailEditText.error = null
                latitudeEditText.error = null
                longitudeEditText.error = null
                startTimeEditText.error = null
                endTimeEditText.error = null

                if (startTimeMillis > endTimeMillis) {
                    Toast.makeText(
                        requireContext(),
                        "The start time cannot be later than the end time.",
                        Toast.LENGTH_LONG
                    ).show()
                    startTimeEditText.error = ""
                    endTimeEditText.error = ""
                    return@setOnClickListener
                }
                if (eventName.isEmpty()) {
                    eventNameEditText.error = "Event name cannot be empty."
                    return@setOnClickListener
                }
                if (address.isEmpty()) {
                    addressEditText.error = "Address cannot be empty."
                    return@setOnClickListener
                }
                if (detail.isEmpty()) {
                    detailEditText.error = "Detail cannot be empty."
                    return@setOnClickListener
                }
                if (latitude.isEmpty()) {
                    latitudeEditText.error = "Latitude cannot be empty."
                    return@setOnClickListener
                }
                if (longitude.isEmpty()) {
                    longitudeEditText.error = "Longitude cannot be empty."
                    return@setOnClickListener
                }

                val event = Event(
                    longitude = longitude.toDouble(),
                    latitude = latitude.toDouble(),
                    address = address.trim(),
                    dateOfCreation = event.dateOfCreation,
                    name = eventName.trim(),
                    detail = detail.trim(),
                    startTimeMillis = startTimeMillis,
                    endTimeMillis = endTimeMillis,
                    imageUrl = viewModel.imageUrl.value.toString(),
                    participantIDs = event.participantIDs
                )

                viewModel.createEventAndSendNotificationOnFirestore(event, reportId)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.eventApiState.collect {
                    when (it) {
                        is APIStateWithValue.Error -> {
                            // hide the circular progress indicator
                            eventProgressCardView.visibility = View.INVISIBLE
                            // enable save button
                            createEventButton.isEnabled = true
                            // display error toast message
                            Toast.makeText(
                                context,
                                it.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is APIStateWithValue.Loading -> {
                            // show the circular progress indicator
                            eventProgressCardView.visibility = View.VISIBLE
                            // disable save button
                            createEventButton.isEnabled = false
                        }
                        is APIStateWithValue.Success -> {
                            // hide the circular progress indicator
                            eventProgressCardView.visibility = View.INVISIBLE
                            // enable save button
                            createEventButton.isEnabled = true
                            findNavController().navigate(R.id.reportFragment)
                            // display success message
                            Toast.makeText(
                                context,
                                "Event created succesfully.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.imageUrlApiState.collect {
                    when (it) {
                        is APIStateWithValue.Error -> {
                            // hide the circular progress indicator
                            eventProgressCardView.visibility = View.INVISIBLE
                            // enable save button
                            createEventButton.isEnabled = true
                            // display error toast message
                            Toast.makeText(
                                context,
                                it.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is APIStateWithValue.Loading -> {
                            // show the circular progress indicator
                            eventProgressCardView.visibility = View.VISIBLE
                            // enable save button
                            createEventButton.isEnabled = false
                        }
                        is APIStateWithValue.Success -> {
                            // hide the circular progress indicator
                            eventProgressCardView.visibility = View.INVISIBLE
                            // enable save button
                            createEventButton.isEnabled = true
                            // load image into image view
                            Glide.with(editTextImageView.context)
                                .load(it.result)
                                .into(editTextImageView)
                            // display success message
                            Toast.makeText(
                                context,
                                "Image upload succesfully.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }


    private fun getStartTimeMillisFromCalendar(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hourOfDay: Int,
        minute: Int,
    ): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }

    private fun getDateFromMillis(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        return dateFormat.format(calendar.time)
    }

    private fun getTimeFromMillis(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val dateFormat = SimpleDateFormat("hh:mm aa")
        return dateFormat.format(calendar.time)
    }

    private fun getAddressFromLatLng(context: Context, latLng: LatLng): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) as List<Address>
        return if (addresses.isNotEmpty()) {
            val address = addresses[0]
            val addressLines = address.getAddressLine(0)
            addressLines
        } else {
            null
        }
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
            val initCameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 15f)
            markerOptions = MarkerOptions()
                .position(position)
                .title(event.name)
            // add marker on map
            marker = map.addMarker(markerOptions)!!
            // move the camera to focus on the marker
            map.moveCamera(initCameraUpdate)

            viewModel.latLngName.observe(viewLifecycleOwner) { (latlng, name) ->
                marker.remove()
                markerOptions
                    .position(latlng)
                    .title(name)
                marker = map.addMarker(markerOptions)!!
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15f)
                // move the camera to focus on the marker
                map.animateCamera(cameraUpdate)
            }
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
        if (this::eventMapView.isInitialized) {
            eventMapView.onDestroy()
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    // upload event image
                    viewModel.uploadEventImage(fileUri)
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