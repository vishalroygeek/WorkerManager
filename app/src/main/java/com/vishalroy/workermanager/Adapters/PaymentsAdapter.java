package com.vishalroy.workermanager.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vishalroy.workermanager.Models.Payments;
import com.vishalroy.workermanager.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.ViewHolder> {

    private List<Payments> paymentsList;
    private Context context;

    public PaymentsAdapter(List<Payments> payments){
        this.paymentsList = payments;
    }

    @NonNull
    @Override
    public PaymentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.payments_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentsAdapter.ViewHolder holder, int position) {
        holder.setDetails(paymentsList.get(position).getAmount(), paymentsList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return paymentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView date, amount;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            date = view.findViewById(R.id.date);
            amount = view.findViewById(R.id.amount);
        }

        public void setDetails(String amt, long dt){
            amount.setText("₹ " + amt);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            date.setText(sdf.format(dt));
        }
    }
}
