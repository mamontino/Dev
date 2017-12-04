package com.softdesign.devintensive.ui.activities;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

public class BaseActivity extends AppCompatActivity
{
    private static final String TAG = ConstantManager.TAG_PREFIX + "Base Activity";
    protected ProgressDialog progressDialog;

    public void showProgress()
    {
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(this, R.style.custom_dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_splash);
        } else
        {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_splash);
        }
    }

    public void hideProgress()
    {
        if (progressDialog != null)
        {
            if (progressDialog.isShowing())
            {
                progressDialog.hide();
                progressDialog.dismiss();
            }
        }
    }

    public void showError(String messege, Exception error)
    {
        showToast(messege);
        Log.e(TAG, String.valueOf(error));
    }

    public void showToast(String messege)
    {
        Toast.makeText(this, messege, Toast.LENGTH_LONG).show();
    }
}
