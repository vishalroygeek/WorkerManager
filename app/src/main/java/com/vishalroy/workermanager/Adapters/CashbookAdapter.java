package com.vishalroy.workermanager.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vishalroy.workermanager.Models.Cashbook;
import com.vishalroy.workermanager.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class CashbookAdapter extends RecyclerView.Adapter<CashbookAdapter.ViewHolder> {

    private List<Cashbook> cashbookList;
    private Context context;

    public CashbookAdapter(List<Cashbook> cashbookList){
        this.cashbookList = cashbookList;
    }

    @NonNull
    @Override
    public CashbookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.cashbook_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CashbookAdapter.ViewHolder holder, int position) {
        holder.setDetails(cashbookList.get(position).getName(), cashbookList.get(position).getAmount(),
                cashbookList.get(position).getDate(), cashbookList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return cashbookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView name, amount, date, description;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = view.findViewById(R.id.name);
            amount = view.findViewById(R.id.amount);
            date = view.findViewById(R.id.date);
            description = view.findViewById(R.id.description);
        }

        public void setDetails(String nm, String amt, long dt, final String des){
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

            name.setText("Name : " + nm);
            amount.setText("Amount : ₹ " + amt);
            date.setText("Date : " + sdf.format(dt));

            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDesc(des);
                }
            });
        }

        private void showDesc(String desc){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Description");
            dialog.setIcon(context.getResources().getDrawable(R.drawable.ic_info));
            dialog.setMessage(desc);
            dialog.setPositiveButton("Ok", null);
            dialog.create();
            dialog.show();
        }
    }
}
