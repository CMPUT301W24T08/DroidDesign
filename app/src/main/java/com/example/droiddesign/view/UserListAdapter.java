package com.example.droiddesign.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
	private List<String> userList;

	public class ViewHolder extends RecyclerView.ViewHolder {
		public TextView textView;

		public ViewHolder(View itemView) {
			super(itemView);
			textView = itemView.findViewById(R.id.recy);
		}
	}

	public UserListAdapter(List<String> userList) {
		this.userList = userList;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		String item = userList.get(position);
		holder.textView.setText(item);
	}

	@Override
	public int getItemCount() {
		return userList.size();
	}
}