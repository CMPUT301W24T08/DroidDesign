package com.example.droiddesign.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.User;

import java.util.HashMap;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    private List<User> userList;
    private HashMap<String, Integer> checkInsMap;
    private final OnItemClickListener listener;
    private int guestUserCount = 0;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public UserListAdapter(List<User> userList, HashMap<String, Integer> checkInsMap, OnItemClickListener listener) {
        this.userList = userList;
        this.checkInsMap = checkInsMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        boolean isRegistered = Boolean.parseBoolean(user.getRegistered());

        if (isRegistered) {
            holder.textUserName.setText(user.getUserName());
            holder.textCompany.setText(user.getCompany());

            // Load the actual profile picture
            if (user.getProfilePic() != null) {
                Glide.with(holder.itemView.getContext())
                        .load(user.getProfilePic())
                        .into(holder.profileImageView);
            }
        } else {
            // Increment and display for guest users
            guestUserCount++;
            holder.textUserName.setText("Guest User " + guestUserCount);
            holder.textCompany.setText("");

            // Load a generic avatar image
            String avatarUrl = "https://robohash.org/" + user.getUserId(); // Example URL
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .into(holder.profileImageView);
        }

        // Only show check-in data if checkInsMap is not null
        if (checkInsMap != null) {
            Number checkInsCountNumber = checkInsMap.get(user.getUserId());
            Integer checkInsCount = (checkInsCountNumber != null) ? checkInsCountNumber.intValue() : null;
            holder.numCheckIns.setText(checkInsCount == null ? "0" : checkInsCount.toString());
        } else {
            holder.numCheckIns.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textCompany, numCheckIns;
        ImageView profileImageView;

        public UserViewHolder(View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_user_name);
            textCompany = itemView.findViewById(R.id.text_company);
            numCheckIns = itemView.findViewById(R.id.check_in_no);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
        }
    }
}
