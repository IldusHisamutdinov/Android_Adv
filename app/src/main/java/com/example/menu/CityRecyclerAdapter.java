package com.example.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.menu.model.DataModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

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

        @BindView(R.id.city)
        public TextView city;
        @BindView(R.id.temp)
        public TextView temp;
        @BindView(R.id.delete)
        public TextView delete;
        @BindView(R.id.date)
        public TextView date;


        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
