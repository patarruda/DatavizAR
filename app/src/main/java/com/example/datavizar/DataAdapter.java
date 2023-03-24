package com.example.datavizar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    Context context;
    List<DataModel> dataSlice;

    public DataAdapter(Context context, List<DataModel> dataSlice) {
        this.context = context;
        this.dataSlice = dataSlice;
    }

    public void setDataSlice(List<DataModel> dataSlice) {
        this.dataSlice = dataSlice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
        if (dataSlice != null && dataSlice.size() > 0) {
            DataModel model = dataSlice.get(position);
            holder.tv_nome.setText(model.getNome());
            holder.tv_valor.setText(String.valueOf(model.getValor()));
            View rowView = holder.itemView;

            if (model.isSelected()) {
                rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_grey_100));
            } else {
                rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_grey_400));
            }

            // set the click listener for selecting/deselecting items
            holder.itemView.setOnClickListener(v -> {
                model.setSelected(!model.isSelected());
                notifyItemChanged(position);
            });
        }

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (dataSlice != null) {
            count = dataSlice.size();
        }
        return count;
    }

    public List<DataModel> getSelectedItems() {
        List<DataModel> selectedItems = new ArrayList<>();

        for (DataModel model : dataSlice) {
            if (model.isSelected()) {
                selectedItems.add(model);
            }
        }

        return selectedItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_nome;
        TextView tv_valor;
        //boolean isSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_nome = itemView.findViewById(R.id.tv_nome);
            tv_valor = itemView.findViewById(R.id.tv_valor);
            //isSelected = false;
            //itemView.setOnClickListener(this);
        }


    }
}
