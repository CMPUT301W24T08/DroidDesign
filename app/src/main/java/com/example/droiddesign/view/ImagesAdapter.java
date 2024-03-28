package com.example.droiddesign.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

	private List<ImageItem> imageItemList;
	private Context context;

	public ImagesAdapter(List<ImageItem> imageItemList) {
		this.imageItemList = imageItemList;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		context = parent.getContext();
		View view = LayoutInflater.from(context).inflate(R.layout.card_image_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ImageItem imageItem = imageItemList.get(position);
		Glide.with(context)
				.load(imageItem.getImageUrl())
				.into(holder.imageView);
	}

	@Override
	public int getItemCount() {
		return imageItemList.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView;

		ViewHolder(View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.image_view);
		}
	}
}

