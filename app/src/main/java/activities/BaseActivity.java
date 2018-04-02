package activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.projects.shrungbhatt.employeemanagement.R;

import java.util.ArrayList;
import java.util.Objects;

import utils.Const;
import utils.MyFileContentProvider;

public class BaseActivity extends AppCompatActivity {

    public static final int OpenCamera = 100;


    private final String TAG = getClass().getSimpleName();
    private Dialog mProgressDialog;
    private ArrayList<String> permissionList;


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

    public void requestPersmission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            OpenMediaPickerDialog();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setMessage("EM needs storage, location, camera,sms permissions in order to use all functionality")
                    .setCancelable(false)
                    .setPositiveButton("Give Permission", (dialog, which) -> requestReadStoragePermission())
                    .create().show();
        } else {
            requestReadStoragePermission();
        }


    }

    public void OpenMediaPickerDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Upload attachment");

            String[] values = {"Take photo"};

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
            builder.setAdapter(adapter, (dialog, which) -> {
                if (which == 0) {
                    OpenCamera(Const.OpenCamera);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    /**
     * Open gallery to select photo
     *
     * @param requestCode int value
     */
    public void OpenCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Intent intent = new Intent();
        Uri capturedPhotoURI = MyFileContentProvider.CONTENT_URI;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedPhotoURI);
        startActivityForResult(intent, requestCode);
    }

    private void requestReadStoragePermission() {
        permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.CAMERA);
        }

        if (permissionList != null && permissionList.size() > 0) {
            String[] permissiondata = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissiondata, Const.REQUEST_STORAGE_PERMISSION);
        } else {
            OpenMediaPickerDialog();
        }
    }

}
