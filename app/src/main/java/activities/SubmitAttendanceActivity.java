package activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.projects.shrungbhatt.employeemanagement.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utils.Const;
import utils.MySharedPreferences;
import utils.URLGenerator;

public class SubmitAttendanceActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.upload_date_tv)
    TextView mUploadDateTv;
    @BindView(R.id.documents_image_view)
    ImageView mDocumentsImageView;
    @BindView(R.id.upload_document_image)
    ImageView mUploadDocumentImage;
    @BindView(R.id.upload_document_tv)
    TextView mUploadDocumentTv;
    @BindView(R.id.frm_openimagepicker)
    FrameLayout mFrmOpenimagepicker;
    @BindView(R.id.upload_time_tv)
    TextView mUploadTimeTv;
    @BindView(R.id.upload_attendance_fab_button)
    FloatingActionButton mUploadAttendanceFabButton;

    private File out = null;
    private String mImage;
    private SimpleDateFormat mDateFormat;
    private SimpleDateFormat mTimeFormat;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_attendance);
        ButterKnife.bind(this);

        mDateFormat = new SimpleDateFormat("dd MMM yyyy");
        mTimeFormat = new SimpleDateFormat("hh:mm a");

        String date = "Today is " + mDateFormat.format(new Date());
        mUploadDateTv.setText(date);

        String time = "...submitting at " + mTimeFormat.format(new Date());
        mUploadTimeTv.setText(time);
    }


    private void addAttendance(final String date, final String time, final String photoDesc,
                               final String image, final String userName,
                               final String isSubmitted) {

        showProgressBar(this,TAG);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.ADD_ATTENDANCE,
                response -> {
                    hideProgressBar();
                    if (response.equals("Insert SuccessFul")) {
                        Toast.makeText(getApplicationContext(), "Registration Complete",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this,Activity_Homescreen.class).
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();

                    } else {
                        showToastMessage(response);
                    }

                }, error -> {
            hideProgressBar();
            error.getMessage();

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("user_name", userName);
                params.put("date", date);
                params.put("time", time);
                params.put("image", image);
                params.put("image_desc", photoDesc);
                params.put("is_submitted", isSubmitted);


                return params;
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @OnClick({R.id.upload_document_image, R.id.upload_attendance_fab_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.upload_document_image:
                requestPersmission();
                break;
            case R.id.upload_attendance_fab_button:
                addAttendance(mDateFormat.format(new Date()),
                        mTimeFormat.format(new Date()),
                        UUID.randomUUID().toString()+"img",getImage(),
                        MySharedPreferences.getStoredUsername(this),"1");
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Const.OpenCamera) {
                out = new File(getFilesDir(), Const.IMAGE_CAPTURED);
//                Uri filePath = data.getData();
                mUploadDocumentImage.setVisibility(View.GONE);
                mUploadDocumentTv.setVisibility(View.GONE);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.fromFile(out));
                    mDocumentsImageView.setImageBitmap(bitmap);
                    setImage(getStringImage(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

}
