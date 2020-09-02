package com.example.menu.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.menu.R;
import com.example.menu.model.DataModel;

import java.util.ArrayList;
import java.util.List;

public class CityRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DataModel> dataModels = new ArrayList<>();
    private OnDeleteListener onDeleteListener;
    private Context context;

    public CityRecyclerAdapter(Context context, List<DataModel> dataModels) {
        this.context = context;
        this.dataModels = dataModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_some_data, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final NewsViewHolder viewHolder = (NewsViewHolder) holder;
        viewHolder.city.setText(dataModels.get(position).getCity());
        viewHolder.temp.setText(dataModels.get(position).getTemp());
        viewHolder.date.setText(dataModels.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        public TextView city;
        public TextView temp;
        public TextView delete;
        public TextView date;


        public NewsViewHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            city = itemView.findViewById(R.id.city);
            temp = itemView.findViewById(R.id.temp);
            date = itemView.findViewById(R.id.date);

            delete.setOnClickListener(view -> {
                onDeleteListener.onDelete(dataModels.get(getAdapterPosition()));
                dataModels.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });
        }
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public interface OnDeleteListener {
        void onDelete(DataModel dataModel);
    }
}
