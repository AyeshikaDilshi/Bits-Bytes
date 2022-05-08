package com.itp.walletguard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.itp.walletguard.R;
import com.itp.walletguard.entity.CategoryEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoCompleteCategoryAdapter extends ArrayAdapter<CategoryEntity> {
    private List<CategoryEntity> categoryListFull;

    public AutoCompleteCategoryAdapter(@NonNull Context context, @NonNull List<CategoryEntity> categoryEntities) {
        super(context, 0, categoryEntities);
        this.categoryListFull = new ArrayList<>(categoryEntities);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return customerFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_items, parent, false);
        }
        TextView txtName = convertView.findViewById(R.id.txt_cat_name);
        TextView txtDesc = convertView.findViewById(R.id.txt_cat_desc);

        CategoryEntity category = getItem(position);
        if (category != null) {
            txtName.setText(category.getCategoryName());
            txtDesc.setText(category.getDesc());
        }
        return convertView;
    }

    private final Filter customerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();
            List<CategoryEntity> suggestion = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestion.addAll(categoryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();
                for (CategoryEntity category : categoryListFull) {
                    if (category.getCategoryName().toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                        suggestion.add(category);
                    }
                }
            }
            result.values = suggestion;
            result.count = suggestion.size();
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((CategoryEntity) resultValue).getCategoryName();
        }
    };
}