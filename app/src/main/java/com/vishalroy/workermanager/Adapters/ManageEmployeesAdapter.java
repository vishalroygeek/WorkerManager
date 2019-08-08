package com.vishalroy.workermanager.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vishalroy.workermanager.Activities.AddEmployeeActivity;
import com.vishalroy.workermanager.Activities.AttendanceRangeActivity;
import com.vishalroy.workermanager.Activities.CalculatePaymentActivity;
import com.vishalroy.workermanager.Activities.EmployeeInfoActivity;
import com.vishalroy.workermanager.Helpers.CollectionRef;
import com.vishalroy.workermanager.Helpers.DailyPaymentRef;
import com.vishalroy.workermanager.Helpers.EmployeeRefCredentials;
import com.vishalroy.workermanager.Activities.PaymentsActivity;
import com.vishalroy.workermanager.Models.Employees;
import com.vishalroy.workermanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageEmployeesAdapter extends RecyclerView.Adapter<ManageEmployeesAdapter.ViewHolder> {

    private List<Employees> employeesList;
    private Context context;
    private FirebaseFirestore mDatabse;
    private static final int REQUEST_CODE = 0;
    private int position = 0;

    public ManageEmployeesAdapter(List<Employees> employeesList) {
        this.employeesList = employeesList;
    }

    @NonNull
    @Override
    public ManageEmployeesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        mDatabse = FirebaseFirestore.getInstance();
        View view = LayoutInflater.from(context).inflate(R.layout.employee_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageEmployeesAdapter.ViewHolder holder, int position) {
        this.position = position;
        String name = employeesList.get(position).getName();
        String image = employeesList.get(position).getImage();
        String role = employeesList.get(position).getEmployee_type();
        String id = employeesList.get(position).getId();
        String number = employeesList.get(position).getPhone();
        String type = employeesList.get(position).getEmployee_type();
        String wage = employeesList.get(position).getWage();
        long added_on = employeesList.get(position).getDate_added();
        long last_paid = employeesList.get(position).getLast_paid();
        int advance = (int) employeesList.get(position).getAdvance();

        holder.setUserDetails(name, role, employeesList.get(position).getWage());
        holder.loadImage(image);
        holder.setDelete(id);
        holder.setCall(number);
        holder.setEdit(id, image, name, type, number, wage);
        holder.setInfo(name, image, number, type, added_on, last_paid, advance);
        holder.setMore(id, advance);
    }

    @Override
    public int getItemCount() {
        return employeesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CircleImageView user_image;
        private TextView user_name, user_role, user_wage;
        private ImageView call, edit, delete, info, more;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            user_image = view.findViewById(R.id.user_image);
            user_name = view.findViewById(R.id.user_name);
            user_role = view.findViewById(R.id.user_role);
            call = view.findViewById(R.id.call);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
            info = view.findViewById(R.id.info);
            more = view.findViewById(R.id.more);
            user_wage = view.findViewById(R.id.user_wage);
        }

        public void loadImage(String url) {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .load(url)
                    .into(user_image);
        }

        public void setUserDetails(String name, String role, String wage) {
            user_name.setText(name);
            user_role.setText(role);
            user_wage.setText("Wage : ₹ " + wage);
        }

        public void setDelete(final String id) {

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEmployee(id, getAdapterPosition());
                }
            });

        }

        public void setCall(final String number) {
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
                        }else{
                            context.startActivity(call);
                        }
                    }else {
                        context.startActivity(call);
                    }
                }
            });
        }

        public void setEdit(final String id, final String image, final String name, final String type, final String number, final String wage){
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent edit = new Intent(context, AddEmployeeActivity.class);
                    edit.putExtra("action", true);
                    edit.putExtra("url", image);
                    edit.putExtra("name", name);
                    edit.putExtra("type", type);
                    edit.putExtra("number", number);
                    edit.putExtra("id", id);
                    edit.putExtra("wage", wage);
                    context.startActivity(edit);
                }
            });
        }

        public void setInfo(final String name, final String url, final String number, final String type, final long added_on, final long last_paid, final int advance){
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent info = new Intent(context, EmployeeInfoActivity.class);
                    info.putExtra("url",url);
                    info.putExtra("name",name);
                    info.putExtra("number",number);
                    info.putExtra("type",type);
                    info.putExtra("last_paid", last_paid);
                    info.putExtra("date_added",added_on);
                    info.putExtra("advance", advance);
                    context.startActivity(info);
                }
            });
        }

        public void setMore(final String id, final int advance_amount){
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(more, id, advance_amount, getAdapterPosition());
                }
            });
        }


    }

    private void showPopup(ImageView button, final String id, final int advance_amount, final int position){
        PopupMenu popupMenu = new PopupMenu(context, button);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.advance:
                        showAdvanceDialog(id, advance_amount, position);
                        break;
                    case R.id.monthly_payment:
                        Intent intent = new Intent(context, CalculatePaymentActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("url", employeesList.get(position).getImage());
                        intent.putExtra("name", employeesList.get(position).getName());
                        intent.putExtra("type", employeesList.get(position).getEmployee_type());
                        intent.putExtra("last_paid", employeesList.get(position).getLast_paid());
                        intent.putExtra("advance", (int) employeesList.get(position).getAdvance());
                        intent.putExtra("wage", employeesList.get(position).getWage());
                        Activity activity = (Activity) context;
                        activity.startActivityForResult(intent, REQUEST_CODE);
                        break;
                    case R.id.show_payments:
                        Intent intent1 = new Intent(context, PaymentsActivity.class);
                        intent1.putExtra("id", id);
                        context.startActivity(intent1);
                        break;
                    case R.id.pay:
                        showPayDialog(employeesList.get(position).getId(), employeesList.get(position).getName(),
                                employeesList.get(position).getImage(), employeesList.get(position).getEmployee_type());
                        break;
                    case R.id.attendance:
                        showRangePicker(employeesList.get(position).getId());
                        break;
                }
                return false;
            }
        });
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_employee, popupMenu.getMenu());
        popupMenu.show();
    }

    private void showRangePicker(final String id){
        SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
            @Override
            public void onDateRangeSet(SmoothDateRangePickerFragment view, int yearStart, int monthStart, int dayStart, int yearEnd, int monthEnd, int dayEnd) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                try {
                    Date startDate = sdf.parse(String.valueOf(dayStart)+" "+getMonth(monthStart)+" "+String.valueOf(yearStart));
                    Date endDate = sdf.parse(String.valueOf(dayEnd)+" "+getMonth(monthEnd)+" "+String.valueOf(yearEnd));

                    Intent range = new Intent(context, AttendanceRangeActivity.class);
                    range.putExtra("id", id);
                    range.putExtra("startDate", startDate.getTime());
                    range.putExtra("endDate", endDate.getTime());
                    context.startActivity(range);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        smoothDateRangePickerFragment.show(((Activity) context).getFragmentManager() , "smoothDateRangePicker");

    }

    private void showPayDialog(final String id, final String name, final String image, final String type){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.wage_dialog_layout, null);
        dialog.setView(view);
        dialog.setTitle("Pay Today's amount");
        dialog.setIcon(context.getResources().getDrawable(R.drawable.ic_rupee));
        final EditText editText = view.findViewById(R.id.get_wage);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String date = sdf.format(System.currentTimeMillis());
        Date date1 = null;
        try {
            date1 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final long final_date = date1.getTime();

        dialog.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString())){
                    toast("Please enter an amount to be paid");
                }else {
                    HashMap<String, Object> pay = new HashMap<>();
                    pay.put(DailyPaymentRef.USER_ID, id);
                    pay.put(DailyPaymentRef.AMOUNT, editText.getText().toString());
                    pay.put(DailyPaymentRef.DATE, final_date);
                    pay.put(DailyPaymentRef.NAME, name);
                    pay.put(DailyPaymentRef.IMAGE, image);
                    pay.put(DailyPaymentRef.TYPE, type);

                    mDatabse.collection(CollectionRef.EMPLOYEE_DAILY_PAYMENTS)
                            .document(id)
                            .set(pay)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        toast(name + " was paid successfully");
                                    }else {
                                        toast("Unable to pay "+name+". Please try again !");
                                    }
                                }
                            });
                }
            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
    }

    private void showAdvanceDialog(final String id, int amount, final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.wage_dialog_layout, null);
        dialog.setView(view);
        dialog.setTitle("Advance amount");
        dialog.setIcon(context.getResources().getDrawable(R.drawable.ic_rupee));

        final EditText editText = view.findViewById(R.id.get_wage);
        editText.setText(String.valueOf(amount));

        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString())){
                    toast("Please enter an amount");
                }else {
                    Map<String, Object> advance = new HashMap<>();
                    advance.put(EmployeeRefCredentials.ADVANCE_AMOUNT, Integer.parseInt(editText.getText().toString()));

                    mDatabse.collection(CollectionRef.EMPLOYEES)
                            .document(id)
                            .update(advance)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        employeesList.get(position).setAdvance(Integer.parseInt(editText.getText().toString()));
                                        notifyItemChanged(position);
                                        toast("Advance amount updated successfully");
                                    }else {
                                        toast("Unable to update advance amount");
                                    }

                                }
                            });
                }
            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();

    }

    private void deleteEmployee(final String id, final int position){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you really want to delete this employee ?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                employeesList.remove(position);
                notifyDataSetChanged();

                mDatabse.collection(CollectionRef.EMPLOYEES).document(id)
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    toast("Employee was deleted successfully");
                                }else {
                                    toast("Unable to delete employee");
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("no", null);
        builder.create();
        builder.show();
    }

    private void toast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private String getMonth(int month){
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }
}
