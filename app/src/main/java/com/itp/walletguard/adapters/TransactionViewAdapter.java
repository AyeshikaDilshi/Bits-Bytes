package com.itp.walletguard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.itp.walletguard.R;
import com.itp.walletguard.entity.TransactionEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionViewAdapter extends RecyclerView.Adapter<TransactionViewAdapter.TransViewVH> {
    private List<TransactionEntity> transactionEntities;
    private final SimpleDateFormat dateFormat;
    private Context context;

    public TransactionViewAdapter(List<TransactionEntity> transactionEntities, Context context) {
        this.transactionEntities = transactionEntities;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.context = context;
    }

    @NonNull
    @Override
    public TransViewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View transLstView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_lst_items, parent, false);
        return new TransViewVH(transLstView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransViewVH holder, int position) {
        TransactionEntity transactionEntity = transactionEntities.get(position);
        if (transactionEntity != null) {
            holder.view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rec_anim));

            holder.txtName.setText(transactionEntity.getName());
            holder.txtDesc.setText(transactionEntity.getDesc());
            holder.txtDate.setText(dateFormat.format(transactionEntity.getTransactionDate()));
            holder.txtValue.setText(String.format(Locale.getDefault(), "%.2f", transactionEntity.getValue()));
        }
    }

    @Override
    public int getItemCount() {
        if (transactionEntities == null)
            return 0;
        return transactionEntities.size();
    }

    public void setTransactionEntities(List<TransactionEntity> transactionEntitiesLst) {
        this.transactionEntities = transactionEntitiesLst;
        notifyDataSetChanged();
    }

    public TransactionEntity getTransactionEnt(int position) {
        return transactionEntities.get(position);
    }

    public class TransViewVH extends RecyclerView.ViewHolder {
        private final CardView view;
        private final TextView txtName;
        private final TextView txtDesc;
        private final TextView txtDate;
        private final TextView txtValue;

        public TransViewVH(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.crd_view);
            txtName = itemView.findViewById(R.id.txt_trans_name);
            txtDesc = itemView.findViewById(R.id.txt_trans_desc);
            txtDate = itemView.findViewById(R.id.txt_trans_date);
            txtValue = itemView.findViewById(R.id.txt_trans_value);

        }
    }
}