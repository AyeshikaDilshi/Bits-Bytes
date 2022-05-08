package com.itp.walletguard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.itp.walletguard.R;
import com.itp.walletguard.entity.CategoryEntity;
import com.itp.walletguard.entity.TransactionEntity;

import java.util.List;
import java.util.Locale;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryListVM> {
    private List<CategoryEntity> categoryEntities;
    private Context context;

    public CategoryListAdapter(List<CategoryEntity> categoryEntities, Context context) {
        this.categoryEntities = categoryEntities;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryListVM onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View catListView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_items, parent, false);
        return new CategoryListAdapter.CategoryListVM(catListView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListVM holder, int position) {
        CategoryEntity categoryEntity = categoryEntities.get(position);

        if (categoryEntity != null) {
            holder.txtCatName.setText(
                    String.format(Locale.getDefault(), "%03d", categoryEntity.getCategoryID())
                            .concat(" ) ").concat(categoryEntity.getCategoryName()));
            holder.txtCatDesc.setText(categoryEntity.getDesc());

        }
    }

    @Override
    public int getItemCount() {
        if (categoryEntities == null)
            return 0;
        return categoryEntities.size();
    }

    public CategoryEntity getCategoryEntity(int position) {
        return categoryEntities.get(position);
    }

    public void setCategoryList(List<CategoryEntity> categoryEntities) {
        this.categoryEntities = categoryEntities;
        notifyDataSetChanged();
    }

    public class CategoryListVM extends RecyclerView.ViewHolder {
        private final CardView view;
        private final ImageView catImg;
        private final TextView txtCatName;
        private final TextView txtCatDesc;


        public CategoryListVM(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.cat_lst_rec_view);
            catImg = itemView.findViewById(R.id.img_categ);
            txtCatName = itemView.findViewById(R.id.txt_cat_name);
            txtCatDesc = itemView.findViewById(R.id.txt_cat_desc);

        }
    }
}