package com.vishalroy.workermanager.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vishalroy.workermanager.Helpers.CollectionRef;
import com.vishalroy.workermanager.Models.Employees;
import com.vishalroy.workermanager.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeesAttendanceAdapter extends RecyclerView.Adapter<EmployeesAttendanceAdapter.ViewHolder> {

    private List<Employees> employeesList;
    private Context context;
    private FirebaseFirestore mDatabase;
    private String date;
    private long date_in_milli;

    public EmployeesAttendanceAdapter(List<Employees> employeesList, String date){
        this.employeesList = employeesList;
        this.date = date;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date_in_milli = date1.getTime();
    }

    @NonNull
    @Override
    public EmployeesAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mDatabase = FirebaseFirestore.getInstance();
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.employee_attendance_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeesAttendanceAdapter.ViewHolder holder, int position) {
        String name = employeesList.get(position).getName();
        String role = employeesList.get(position).getEmployee_type();
        String image = employeesList.get(position).getImage();
        String id = employeesList.get(position).getId();

        holder.setDetails(name, role, image);
        holder.setMark(id);
    }

    @Override
    public int getItemCount() {
        return employeesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CircleImageView user_image;
        private TextView user_name, user_role;
        private ImageView mark;
        private boolean present;
        private DocumentSnapshot snapshot;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            user_image = view.findViewById(R.id.user_image);
            user_name = view.findViewById(R.id.user_name);
            user_role = view.findViewById(R.id.user_role);
            mark = view.findViewById(R.id.mark);
        }

        public void setDetails(String name, String role, String image){
            user_name.setText(name);
            user_role.setText(role);
            loadImage(image);
        }

        public void setMark(final String id){
            final DocumentReference employee = mDatabase.collection(CollectionRef.ATTENDANCE)
                    .document(date);


            employee.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    snapshot = documentSnapshot;
                    if (documentSnapshot.contains(id)){
                        present = documentSnapshot.getBoolean(id);
                        setMarkImage(present);
                    }else {
                        present = false;
                        setMarkImage(false);
                    }
                }
            });

            setMarkListener(employee, id);
        }

        private void setMarkListener(final DocumentReference documentReference, final String id){

            mark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (snapshot!=null){
                        if (snapshot.exists()){
                            if (present){
                                final Map<String, Object> present = new HashMap<>();
                                present.put(id, false);
                                present.put("date", date_in_milli);
                                documentReference.update(present);
                            }else {
                                final Map<String, Object> present = new HashMap<>();
                                present.put(id, true);
                                present.put("date", date_in_milli);
                                documentReference.update(present);
                            }
                        }else {
                            if (present){
                                final Map<String, Object> present = new HashMap<>();
                                present.put(id, false);
                                present.put("date", date_in_milli);
                                documentReference.set(present);
                            }else {
                                final Map<String, Object> present = new HashMap<>();
                                present.put(id, true);
                                present.put("date", date_in_milli);
                                documentReference.set(present);
                            }
                        }
                    }
                }
            });
        }

        private void setMarkImage(boolean present){
            final Resources res = context.getResources();

            if (present){
                mark.setBackgroundColor(res.getColor(R.color.colorBtnCall));
                mark.setImageDrawable(res.getDrawable(R.drawable.ic_check));
            }else {
                mark.setBackgroundColor(res.getColor(R.color.colorBtnDelete));
                mark.setImageDrawable(res.getDrawable(R.drawable.ic_cancel));
            }
        }

        private void loadImage(String url) {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .load(url)
                    .into(user_image);
        }

    }
}
