package activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.projects.shrungbhatt.employeemanagement.R;

import java.util.Objects;

public class BaseActivity extends AppCompatActivity {

    private Dialog mProgressDialog;


    public boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = Objects.requireNonNull(cm).getActiveNetworkInfo() != null;

        return isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
    }

    public void showProgressBar(Context context, String TAG) {
        mProgressDialog = new Dialog(context);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.circleprogress);
        mProgressDialog.setCancelable(false);

        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mProgressDialog.getWindow().setGravity(Gravity.CENTER);
        try {
            mProgressDialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void hideProgressBar() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            Log.e("BaseClassForInterface", "Error in hideProgressBar");
        }
    }

    public void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
