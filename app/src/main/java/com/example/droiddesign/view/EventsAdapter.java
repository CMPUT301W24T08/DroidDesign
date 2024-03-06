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

	private final List<Event> eventsList;
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
		holder.textLocation.setText((CharSequence) event.getGeolocation());
		// Parse the date string
		String dateString = event.getEventDate();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		try {
			Date date = dateFormat.parse(dateString);
			Calendar cal = Calendar.getInstance();
			if (date != null) {
				cal.setTime(date);
				int day = cal.get(Calendar.DAY_OF_MONTH);
				// For month, to display it as a word (e.g., Jan, Feb).
				String month = new SimpleDateFormat("MMM", Locale.getDefault()).format(cal.getTime());
				int year = cal.get(Calendar.YEAR);

				holder.dateDay.setText(String.valueOf(day));
				holder.dateMonth.setText(month);
				holder.dateYear.setText(String.valueOf(year));
			}
		}catch (ParseException e) {
//			e.printStackTrace();
			// Handle the case where the date string cannot be parsed
			Log.e("EventAdapter", "Error parsing date string: " + dateString);
		}
		holder.bind(event, listener);
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

