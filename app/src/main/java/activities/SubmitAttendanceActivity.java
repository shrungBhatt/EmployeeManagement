package activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.shrungbhatt.employeemanagement.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utils.Const;

public class SubmitAttendanceActivity extends BaseActivity {

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

    @OnClick({R.id.upload_document_image, R.id.upload_attendance_fab_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.upload_document_image:
                requestPersmission();
                break;
            case R.id.upload_attendance_fab_button:

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Const.OpenCamera) {
                out = new File(getFilesDir(), Const.IMAGE_CAPTURED);
                mUploadDocumentImage.setVisibility(View.GONE);
                mUploadDocumentTv.setVisibility(View.GONE);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(out));
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
