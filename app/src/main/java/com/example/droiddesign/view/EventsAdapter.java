package com.example.droiddesign.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

	private List<Event> eventsList;
	private final OnItemClickListener listener;

	public interface OnItemClickListener {
		void onItemClick(Event event);
	}

	public EventsAdapter(List<Event> eventsList, OnItemClickListener listener) {
		this.eventsList = eventsList;
		this.listener = listener;
	}

	@NonNull
	@Override
	public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
		return new EventViewHolder(view);
	}



	@Override
	public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
		Event event = eventsList.get(position);
		holder.textEventName.setText(event.getEventName());
		holder.textLocation.setText(event.getGeolocation());

		// Adjusted date parsing to handle strings like "08 Mar" or "14 March"
		String dateString = event.getEventDate();
		if (dateString != null && !dateString.isEmpty()) {
			// Define format based on your input string
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
			Calendar cal = Calendar.getInstance();
			try {
				Date date = dateFormat.parse(dateString);
				if (date != null) {
					cal.setTime(date);
					// Assume the current year if no year is provided in the dateString
					cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
					int day = cal.get(Calendar.DAY_OF_MONTH);
					String month = new SimpleDateFormat("MMM", Locale.getDefault()).format(cal.getTime());
					int year = cal.get(Calendar.YEAR);

					holder.dateDay.setText(String.valueOf(day));
					holder.dateMonth.setText(month);
					holder.dateYear.setText(String.valueOf(year));
				}
			} catch (ParseException e) {
				Log.e("EventAdapter", "Error parsing date string: " + dateString);
				holder.dateDay.setText("");
				holder.dateMonth.setText("");
				holder.dateYear.setText("");
			}
		} else {
			Log.e("EventAdapter", "Date string is null or empty for event: " + event.getEventName());
			holder.dateDay.setText("");
			holder.dateMonth.setText("");
			holder.dateYear.setText("");
		}

		holder.bind(event, listener);
	}

	public void setEvents(List<Event> events) {
		this.eventsList.clear();
		this.eventsList.addAll(events);
		notifyDataSetChanged();
	}




	@Override
	public int getItemCount() {
		return eventsList.size();// request database
	}

	public static class EventViewHolder extends RecyclerView.ViewHolder {
		TextView textEventName, textLocation;
		TextView dateDay;
		TextView dateMonth;
		TextView dateYear;

		public EventViewHolder(View itemView) {
			super(itemView);
			// Find views by ID
			textEventName = itemView.findViewById(R.id.text_event_name);
			textLocation = itemView.findViewById(R.id.text_location);
			dateDay = itemView.findViewById(R.id.date_day);
			dateMonth = itemView.findViewById(R.id.date_month);
			dateYear = itemView.findViewById(R.id.date_year);
		}

		public void bind(Event event, OnItemClickListener listener) {
			// Bind event data to the views
			itemView.setOnClickListener(v -> listener.onItemClick(event));

		}
	}
}

