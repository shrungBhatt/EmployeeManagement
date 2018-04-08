package activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.projects.shrungbhatt.employeemanagement.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Res_UserProfile;
import utils.MySharedPreferences;
import utils.URLGenerator;

public class Activity_Profile extends BaseActivity implements Validator.ValidationListener {


    @BindView(R.id.user_profile_close)
    ImageView mUserProfileClose;
    @BindView(R.id.user_profile_edit)
    ImageView mUserProfileEdit;
    @BindView(R.id.user_profile_image)
    CircleImageView mUserProfileImage;

    @NotEmpty
    @BindView(R.id.user_profile_user_name)
    EditText mUserProfileUserName;

    @NotEmpty
    @Email
    @BindView(R.id.user_profile_email)
    EditText mUserProfileEmail;

    @NotEmpty
    @BindView(R.id.user_profile_company)
    EditText mUserProfileCompany;

    @NotEmpty
    @BindView(R.id.user_profile_designation)
    EditText mUserProfileDesignation;


    @BindView(R.id.user_profile_presences)
    EditText mUserProfilePresences;

    @NotEmpty
    @BindView(R.id.user_profile_phone)
    EditText mUserProfilePhone;

    @NotEmpty
    @BindView(R.id.user_profile_address)
    EditText mUserProfileAddress;

    @BindView(R.id.user_profile_edt_btn)
    Button mUserProfileEdtBtn;

    private String TAG = getClass().getSimpleName();
    private boolean isEditEnabled = false;
    private Res_UserProfile mResUserProfile;
    private Validator mValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        this.setFinishOnTouchOutside(false);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        fetchUserProfile(MySharedPreferences.getStoredUsername(this));

        enableEdtTxts(false);
        isEditEnabled = false;
    }


    private void enableEdtTxts(boolean flag) {
        mUserProfileAddress.setEnabled(flag);
        mUserProfileCompany.setEnabled(flag);
        mUserProfileDesignation.setEnabled(flag);
        mUserProfileEmail.setEnabled(flag);
        mUserProfilePhone.setEnabled(flag);
        mUserProfilePresences.setEnabled(flag);
        mUserProfileUserName.setEnabled(flag);

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
                        mResUserProfile = res_userProfile;
                        setUserData(res_userProfile);
                        enableEdtTxts(true);
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
        if (res_userProfile.getList() != null && res_userProfile.getList().size() !=0 ) {
            Res_UserProfile.List list = res_userProfile.getList().get(0);
            mUserProfileUserName.setText(list.getUsername());
            mUserProfilePhone.setText(list.getPhoneNo());
            mUserProfileEmail.setText(list.getEmail());
            mUserProfileDesignation.setText(list.getDesignation());
            mUserProfileCompany.setText(list.getCompany());
            mUserProfileAddress.setText(list.getAddress());
            mUserProfilePresences.setText(list.getPresence());
            if(list.getPhotoUrl()!=null && !list.getPhotoUrl().isEmpty())
            Picasso.get().load(list.getPhotoUrl()).into(mUserProfileImage);
        }else{
            mUserProfileEdit.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.user_profile_close, R.id.user_profile_edit, R.id.user_profile_edt_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_profile_close:
                onBackPressed();
                break;
            case R.id.user_profile_edit:
                if (isEditEnabled) {
                    enableEdtTxts(false);
                    isEditEnabled = false;
                    mUserProfileEdtBtn.setVisibility(View.GONE);

                } else {
                    enableEdtTxts(true);
                    isEditEnabled = true;
                    mUserProfileEdtBtn.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.user_profile_edt_btn:
                mValidator.validate();
                break;
        }
    }


    private void updateUserProfile(Res_UserProfile res_userProfile) {
        showProgressBar(this, TAG);

        final String id = res_userProfile.getList().get(0).getEid();
        final String username = mUserProfileUserName.getText().toString();
        final String email = mUserProfileEmail.getText().toString();
        final String phoneNo = mUserProfilePhone.getText().toString();
        final String company = mUserProfileCompany.getText().toString();
        final String designation = mUserProfileDesignation.getText().toString();
        final String address = mUserProfileAddress.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.UPDATE_USER_PROFILE,
                response -> {
                    hideProgressBar();
                    if (response.equalsIgnoreCase("Updation SuccessFul")) {
                        showToastMessage("Saved");
                        finish();
                    } else {
                        showToastMessage("Something went wrong!");
                    }

                },
                error -> {
                    hideProgressBar();

                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("eid", id);
                params.put("email", email);
                params.put("user_name", username);
                params.put("phone_no", phoneNo);
                params.put("company", company);
                params.put("designation", designation);
                params.put("address", address);


                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


    @Override
    public void onValidationSucceeded() {
        if (mResUserProfile != null)
            updateUserProfile(mResUserProfile);

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
