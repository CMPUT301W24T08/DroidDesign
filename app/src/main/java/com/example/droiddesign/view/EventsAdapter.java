package com.example.droiddesign.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;

import java.util.List;

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
	public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
		return new EventViewHolder(view);
	}

	@Override
	public void onBindViewHolder(EventViewHolder holder, int position) {
		Event event = eventsList.get(position);
//		holder.eventTitle.setText(event.getTitle());
//		holder.eventDate.setText(event.getDate());
		holder.bind(event, listener);
	}

	@Override
	public int getItemCount() {
		return eventsList.size();// request database
	}

	public static class EventViewHolder extends RecyclerView.ViewHolder {
		// Views in your item_event.xml

		public EventViewHolder(View itemView) {
			super(itemView);
			// Find views by ID
		}

		public void bind(Event event, OnItemClickListener listener) {
			// Bind event data to the views
			TextView eventTitle;
			TextView eventDate;
			itemView.setOnClickListener(v -> listener.onItemClick(event));

		}
	}
}

