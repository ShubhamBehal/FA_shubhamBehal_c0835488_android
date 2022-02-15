package com.example.fa_shubhambehal_c0835488_android.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fa_shubhambehal_c0835488_android.R;
import com.example.fa_shubhambehal_c0835488_android.interfaces.FavPlaceClickListener;
import com.example.fa_shubhambehal_c0835488_android.model.PlaceInfo;

import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {

    private final List<PlaceInfo> places;
    private final FavPlaceClickListener listener;

    public PlaceListAdapter(List<PlaceInfo> places, FavPlaceClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fav_place_list_child, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        PlaceInfo place = places.get(position);
        viewHolder.tvName.setText(place.placeName);
        viewHolder.ivDelete.setOnClickListener(view -> listener.onPlaceDelete(place.placeId));
        viewHolder.clParent.setOnClickListener(view -> listener.onItemClick(place));
        viewHolder.ivEdit.setOnClickListener(view -> listener.onPlaceEdit(place));
        if (place.isVisited) {
            viewHolder.ivEdit.setVisibility(View.GONE);
            viewHolder.clParent.setOnClickListener(null);
            viewHolder.clParent.setBackgroundColor(ContextCompat.getColor(viewHolder.clParent.getContext(), R.color.grey));
            viewHolder.tvVisitStatus.setText("Already visited");
        } else {
            viewHolder.ivEdit.setVisibility(View.VISIBLE);
            viewHolder.clParent.setBackgroundColor(ContextCompat.getColor(viewHolder.clParent.getContext(), R.color.white));
            viewHolder.tvVisitStatus.setText("Mark as already visited");
            viewHolder.tvVisitStatus.setOnClickListener(view -> listener.onVisitedClick(place.placeId));
        }
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<PlaceInfo> places) {
        this.places.clear();
        this.places.addAll(places);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvVisitStatus;
        private final ConstraintLayout clParent;
        private final ImageView ivDelete;
        private final ImageView ivEdit;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_place_name);
            tvVisitStatus = view.findViewById(R.id.tv_visit_status);
            clParent = view.findViewById(R.id.cl_parent);
            ivDelete = view.findViewById(R.id.iv_delete);
            ivEdit = view.findViewById(R.id.iv_edit);
        }
    }
}