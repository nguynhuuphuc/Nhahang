package com.example.nhahang.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nhahang.Models.TableModel;
import com.example.nhahang.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectOrderTableAdapter extends ArrayAdapter<TableModel> implements Filterable{
    private LayoutInflater inflater;
    private List<String> tableNames;
    private List<TableModel> originalItems;

    private List<TableModel> filteredItems;

    public SelectOrderTableAdapter(@NonNull Context context, @NonNull List<TableModel> objects) {
        super(context, R.layout.item_order_table_selection, objects);
        this.inflater = LayoutInflater.from(context);
        this.originalItems = new ArrayList<>(objects);
        this.filteredItems = new ArrayList<>(objects);
    }



    @Nullable
    @Override
    public TableModel getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // This method is called to create the view for the selected item
        // Customize the appearance of the selected item view if needed
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_order_table_selection,parent,false );
        }
        // Set the text of the item view
        TableModel model = getItem(position);
        if (model != null) {
            TextView textView = convertView.findViewById(R.id.tableNameTv);
            textView.setText(model.getTable_name()); // Replace with the actual method to get the display text
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<TableModel> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalItems);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (TableModel item : originalItems) {
                        if (item.getTable_name().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems.clear();
                filteredItems.addAll((List<TableModel>) results.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((TableModel)resultValue).getTable_name();
            }
        };
    }


}
