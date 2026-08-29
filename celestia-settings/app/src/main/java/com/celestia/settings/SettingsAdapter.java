package com.celestia.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingsItem> items;
    private Context context;

    public SettingsAdapter(List<SettingsItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingsItem item = items.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.txtSubtitle.setText(item.getSubtitle());
        holder.imgIcon.setImageResource(item.getIconRes());

        holder.itemView.setOnClickListener(v -> {
            // Navigate to setting detail
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle, txtSubtitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_icon);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtSubtitle = itemView.findViewById(R.id.txt_subtitle);
        }
    }
}
