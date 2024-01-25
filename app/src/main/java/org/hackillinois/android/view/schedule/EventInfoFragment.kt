package org.hackillinois.android.view.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_event_info.*
import kotlinx.android.synthetic.main.fragment_event_info.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.viewmodel.EventInfoViewModel

class EventInfoFragment : Fragment() {
    private lateinit var viewModel: EventInfoViewModel

    private val siebelLatLng = LatLng(40.1138356, -88.2249052)
    private lateinit var eventId: String
    private var isAttendeeViewing: Boolean = false
    private var currentEvent: Event? = null

    companion object {
        val EVENT_ID_KEY = "eventId"
        val IS_ATTENDEE_VIEWING = "isAttendeeViewing"
        fun newInstance(eventId: String, isAttendeeViewing: Boolean): EventInfoFragment {
            val fragment = EventInfoFragment()
            val args = Bundle().apply {
                putString(EVENT_ID_KEY, eventId)
                putBoolean(IS_ATTENDEE_VIEWING, isAttendeeViewing)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventId = arguments?.getString(EVENT_ID_KEY) ?: ""
        isAttendeeViewing = arguments?.getBoolean(IS_ATTENDEE_VIEWING) ?: false
        viewModel = ViewModelProviders.of(this).get(EventInfoViewModel::class.java)
        viewModel.init(eventId)
        viewModel.event.observe(
            this,
            Observer { event ->
                currentEvent = event
                updateEventUI(currentEvent)
            }
        )
        viewModel.isFavorited.observe(this, Observer { updateFavoritedUI(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_info, container, false)
        view.exit_button.setOnClickListener { activity?.onBackPressed() }
        view.favorites_button.setOnClickListener {
            if (viewModel.changeFavoritedState()) {
                val toast = Toast.makeText(context, R.string.schedule_snackbar_notifications_on, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        return view
    }

    private fun updateEventUI(event: Event?) {
        event?.let {
            event_name.text = it.name
            event_points.text = "+ ${it.points} pts"
            event_sponsor.text = "Sponsored by ${it.sponsor}"
            event_sponsor.visibility = if (it.sponsor == null) View.GONE else View.VISIBLE
            sponsoredIcon.visibility = if (it.sponsor == null) View.GONE else View.VISIBLE
            favorites_button.visibility = if (!isAttendeeViewing) View.GONE else View.VISIBLE
            if (it.locations.isEmpty()) {
                event_location.text = "N/A"
            } else {
                // add multiple locations
                val locationText = StringBuilder()
                for (i in it.locations.indices) {
                    if (i != 0) locationText.append(",  ")
                    locationText.append(it.locations[i].description)
                }
                event_location.text = locationText
            }
            event_time.text = if (it.isAsync) "Asynchronous event" else "${it.getStartTimeOfDay()} - ${it.getEndTimeOfDay()}"
            event_description.text = it.description
            if (it.eventType == "QNA") {
                event_type.text = "Q&A"
            } else {
                val eventTypeString = it.eventType.lowercase()
                event_type.text = eventTypeString.replaceFirst(eventTypeString.first(), eventTypeString.first().uppercaseChar())
            }
        }
    }

    private fun updateFavoritedUI(isFavorited: Boolean?) {
        isFavorited?.let {
            exit_button.isSelected = isFavorited
            val imageResource =
                if (isFavorited) R.drawable.dark_bookmark_filled else R.drawable.dark_bookmark_hollow
            favorites_button.setImageResource(imageResource)
        }
    }
}
