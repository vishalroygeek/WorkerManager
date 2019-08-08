package com.vishalroy.workermanager.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;

import com.vishalroy.workermanager.R;

public class LoadingDialog {

    public Dialog dialog;
    public Context context;

    public LoadingDialog(Context context){
        this.context = context;
        dialog = new Dialog(context);
    }

    public void show(){
        dialog.setContentView(R.layout.loading_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }
}
