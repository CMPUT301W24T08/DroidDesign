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

/**
 * Adapter class for the user list RecyclerView.
 * This class is responsible for binding user data to the user card view.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    private final List<User> userList;
    private final HashMap<String, Integer> checkInsMap; // This can now be null
    private final OnItemClickListener listener;

    /**
     * Interface for handling item clicks.
     */
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    /**
     * Constructor for the UserListAdapter class.
     * @param userList The list of users to display.
     * @param checkInsMap A map of user IDs to check-in counts.
     * @param listener The listener for item clicks.
     */
    public UserListAdapter(List<User> userList, HashMap<String, Integer> checkInsMap, OnItemClickListener listener) {
        this.userList = userList;
        this.checkInsMap = checkInsMap;
        this.listener = listener;
    }

    /**
     * Method called when a new view holder is created.
     * @param parent The parent view group.
     * @param viewType The view type.
     * @return The created view holder.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Method called when a view holder is bound to a position.
     * @param holder The view holder.
     * @param position The position of the view holder.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textUserName.setText(user.getUserName());
        holder.textCompany.setText(user.getCompany());

        // Only show check-in data if checkInsMap is not null
        if (checkInsMap != null) {
            Number checkInsCountNumber = checkInsMap.get(user.getUserId());
            Integer checkInsCount = (checkInsCountNumber != null) ? checkInsCountNumber.intValue() : null;
            holder.numCheckIns.setText(checkInsCount == null ? "0" : checkInsCount.toString());
        } else {
            holder.numCheckIns.setVisibility(View.GONE); // or set to an empty string or some default text
        }

        if (user.getProfilePic() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfilePic())
                    .into(holder.profileImageView);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
    }

    /**
     * Method called to get the number of items in the list.
     * @return The number of items in the list.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * View holder class for the user list.
     */
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
