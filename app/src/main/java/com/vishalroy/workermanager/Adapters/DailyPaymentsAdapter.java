package com.vishalroy.workermanager.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vishalroy.workermanager.Models.DailyPayments;
import com.vishalroy.workermanager.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DailyPaymentsAdapter extends RecyclerView.Adapter<DailyPaymentsAdapter.ViewHolder> {

    private List<DailyPayments> dailyPaymentsList;
    private Context context;

    public DailyPaymentsAdapter(List<DailyPayments> dailyPaymentsList){
        this.dailyPaymentsList = dailyPaymentsList;
    }

    @NonNull
    @Override
    public DailyPaymentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.daily_payments_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyPaymentsAdapter.ViewHolder holder, int position) {
       holder.setDeails(dailyPaymentsList.get(position).getName(), dailyPaymentsList.get(position).getType(),
               dailyPaymentsList.get(position).getAmount());
       holder.loadImage(dailyPaymentsList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return dailyPaymentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CircleImageView user_image;
        private TextView user_name, user_role, amount;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            user_image = view.findViewById(R.id.user_image);
            user_name = view.findViewById(R.id.user_name);
            user_role = view.findViewById(R.id.user_role);
            amount = view.findViewById(R.id.amount);
        }

        public void setDeails(String name, String type, String amt){
            user_name.setText(name);
            user_role.setText(type);
            amount.setText("₹ " + amt);
        }

        public void loadImage(String url){
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .load(url)
                    .into(user_image);
        }

    }
}
