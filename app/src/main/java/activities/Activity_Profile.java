package activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.projects.shrungbhatt.employeemanagement.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Res_UserProfile;
import utils.MySharedPreferences;
import utils.URLGenerator;

public class Activity_Profile extends BaseActivity {


    @BindView(R.id.user_profile_close)
    ImageView mUserProfileClose;
    @BindView(R.id.user_profile_edit)
    ImageView mUserProfileEdit;
    @BindView(R.id.user_profile_image)
    CircleImageView mUserProfileImage;
    @BindView(R.id.user_profile_user_name)
    EditText mUserProfileUserName;
    @BindView(R.id.user_profile_email)
    EditText mUserProfileEmail;
    @BindView(R.id.user_profile_company)
    EditText mUserProfileCompany;
    @BindView(R.id.user_profile_designation)
    EditText mUserProfileDesignation;
    @BindView(R.id.user_profile_presences)
    EditText mUserProfilePresences;
    @BindView(R.id.user_profile_phone)
    EditText mUserProfilePhone;
    @BindView(R.id.user_profile_address)
    EditText mUserProfileAddress;
    @BindView(R.id.user_profile_edt_btn)
    Button mUserProfileEdtBtn;
    private String TAG = getClass().getSimpleName();
    private boolean isEditEnabled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        this.setFinishOnTouchOutside(false);
//        Objects.requireNonNull(getSupportActionBar()).hide();

        disableEdtTxts(false);

        fetchUserProfile(MySharedPreferences.getStoredUsername(this));
    }


    private void disableEdtTxts(boolean flag) {
        mUserProfileAddress.setFocusable(flag);
        mUserProfileCompany.setFocusable(flag);
        mUserProfileDesignation.setFocusable(flag);
        mUserProfileEmail.setFocusable(flag);
        mUserProfilePhone.setFocusable(flag);
        mUserProfilePresences.setFocusable(flag);
        mUserProfileUserName.setFocusable(flag);
    }

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    private void fetchUserProfile(final String userName) {
        showProgressBar(this, TAG);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.FETCH_USER_PROFILE,
                response -> {
                    hideProgressBar();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Gson gson = new Gson();
                        Res_UserProfile res_userProfile;
                        res_userProfile = gson.fromJson(jsonObject.toString(), Res_UserProfile.class);
                        setUserData(res_userProfile);
                    } catch (JSONException e) {
                        hideProgressBar();
                        e.printStackTrace();
                    }

                }, error -> {
            hideProgressBar();

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", userName);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setUserData(Res_UserProfile res_userProfile) {
        if(res_userProfile.getList()!=null) {
            Res_UserProfile.List list = res_userProfile.getList().get(0);
            mUserProfileUserName.setText(list.getUsername());
            mUserProfilePhone.setText(list.getPhoneNo());
            mUserProfileEmail.setText(list.getEmail());
            mUserProfileDesignation.setText(list.getDesignation());
            mUserProfileCompany.setText(list.getCompany());
            mUserProfileAddress.setText(list.getAddress());
            Picasso.get().load(list.getPhotoUrl()).into(mUserProfileImage);
        }
    }

    @OnClick({R.id.user_profile_close, R.id.user_profile_edit,R.id.user_profile_edt_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_profile_close:
                onBackPressed();
                break;
            case R.id.user_profile_edit:
                disableEdtTxts(true);
                break;
            case R.id.user_profile_edt_btn:
                break;
        }
    }


}
