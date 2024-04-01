package com.example.droiddesign.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;

import java.util.List;
import java.util.Map;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

	private List<Map<String, Object>> announcementList;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView titleTextView;
		public TextView messageTextView;
		public TextView dateTextView;

		public ViewHolder(View view) {
			super(view);
			titleTextView = view.findViewById(R.id.chat_card_title);
			messageTextView = view.findViewById(R.id.chat_card_message);
			dateTextView = view.findViewById(R.id.chat_card_date);
		}
	}

	public AnnouncementAdapter(List<Map<String, Object>> announcementList) {
		this.announcementList = announcementList;
	}

	@Override
	public AnnouncementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_annoucement, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Map<String, Object> announcement = announcementList.get(position);
		holder.titleTextView.setText((String)announcement.get("title"));
		holder.messageTextView.setText((String)announcement.get("message"));
		holder.dateTextView.setText((String)announcement.get("date"));
	}

	@Override
	public int getItemCount() {
		return announcementList.size();
	}
}
