package com.example.emercare.find;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emercare.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    Activity mActivity;
    public List<PlaceData> placeData;

    public PlaceAdapter(List<PlaceData> placeData, Activity mActivity) {
        this.mActivity = mActivity;
        this.placeData = placeData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_place_item, parent, false);
        return new ViewHolder(view, mActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (placeData.get(position).getPlacePhoto().equals("No Photo")) {
            switch (placeData.get(position).getPlace()) {
                case "Clinic":
                    holder.imgPlace.setImageResource(R.drawable.clinic);
                    break;
                case "Hospital":
                    holder.imgPlace.setImageResource(R.drawable.hospital);
                    break;
                case "Police Station":
                    holder.imgPlace.setImageResource(R.drawable.police_station);
                    break;
                case "Fire Station":
                    holder.imgPlace.setImageResource(R.drawable.fire_station);
                    break;
            }
        } else {
            Picasso.get().load(placeData.get(position).getPlacePhoto()).into(holder.imgPlace);
        }
        holder.tvName.setText(placeData.get(position).getPlaceName());
        holder.tvRating.setText(placeData.get(position).getPlaceRating());
        String distanceInKm = placeData.get(position).getPlaceDistance();
        @SuppressLint("DefaultLocale") String distanceInMiles = String.format("%.2f", Double.parseDouble(placeData.get(position).getPlaceDistance()) / 1.609344);
        holder.tvDistance.setText(distanceInKm.concat(" km / ").concat(distanceInMiles).concat(" miles"));

        if ((placeData.get(position).getPlaceStatus()).equals("true")) {
            String openNow = "Open Now";
            holder.tvStatus.setText(openNow);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return placeData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Activity mActivity;
        public ImageView imgPlace;
        public TextView tvName, tvRating, tvDistance, tvStatus;

        public ViewHolder(View itemView, Activity mActivity) {
            super(itemView);
            this.mActivity = mActivity;
            imgPlace = itemView.findViewById(R.id.find_list_img_place);
            tvName = itemView.findViewById(R.id.find_list_tv_name);
            tvRating = itemView.findViewById(R.id.find_list_tv_rating);
            tvDistance = itemView.findViewById(R.id.find_list_tv_distance);
            tvStatus = itemView.findViewById(R.id.find_list_tv_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), FindPlace.class);
            intent.putExtra("Current Address", placeData.get(getAdapterPosition()).getCurrentAddress());
            intent.putExtra("Place Photo", placeData.get(getAdapterPosition()).getPlacePhoto());
            intent.putExtra("Place", placeData.get(getAdapterPosition()).getPlace());
            intent.putExtra("Place Id", placeData.get(getAdapterPosition()).getPlaceId());
            intent.putExtra("Place Name", placeData.get(getAdapterPosition()).getPlaceName());
            intent.putExtra("Place Rating", placeData.get(getAdapterPosition()).getPlaceRating());
            intent.putExtra("Place Status", placeData.get(getAdapterPosition()).getPlaceStatus());
            view.getContext().startActivity(intent);
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
