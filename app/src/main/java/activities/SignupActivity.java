package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.projects.shrungbhatt.employeemanagement.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utils.URLGenerator;

public class SignupActivity extends BaseActivity implements Validator.ValidationListener {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.login_progress)
    ProgressBar mLoginProgress;
    @Email
    @NotEmpty
    @BindView(R.id.user_sign_up_email)
    AutoCompleteTextView mUserSignUpEmail;
    @NotEmpty
    @BindView(R.id.user_sign_up_username)
    EditText mUserSignUpUsername;
    @Password
    @BindView(R.id.user_sign_up_password)
    EditText mUserSignUpPassword;
    @ConfirmPassword
    @BindView(R.id.user_sign_up_reenter_password)
    AutoCompleteTextView mUserSignUpReenterPassword;
    @BindView(R.id.user_sign_up_phone_no)
    AutoCompleteTextView mUserSignUpPhoneNo;
    @BindView(R.id.user_sign_up_company)
    AutoCompleteTextView mUserSignUpCompany;
    @BindView(R.id.user_sign_up_designation)
    AutoCompleteTextView mUserSignUpDesignation;
    @BindView(R.id.sign_up_as_doctor)
    CheckBox mSignUpAsDoctor;
    @BindView(R.id.user_register_button)
    Button mUserRegisterButton;
    @BindView(R.id.email_login_form)
    LinearLayout mEmailLoginForm;

    private Validator mValidator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
    }

    @OnClick(R.id.user_register_button)
    public void onViewClicked() {

        mValidator.validate();

    }

    @Override
    public void onValidationSucceeded() {
        registerUser();
    }

    private void registerUser() {

        final String email = mUserSignUpEmail.getText().toString();
        final String username = mUserSignUpUsername.getText().toString();
        final String password = mUserSignUpPassword.getText().toString();
        final String phoneNo = mUserSignUpPhoneNo.getText().toString();
        final String company = mUserSignUpCompany.getText().toString();
        final String designation = mUserSignUpDesignation.getText().toString();
        final String presence = "0";


        showProgressBar(this, TAG);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.REGISTER_USER,
                response -> {
                    hideProgressBar();
                    if (response.equals("Insert SuccessFul")) {
                        Toast.makeText(getApplicationContext(), "Registration Complete",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SignupActivity.this, Activity_Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showToastMessage("Something went wrong!");
                    }

                }, error -> {
                    hideProgressBar();
                    error.getMessage();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("user_name", username);
                params.put("password", password);
                params.put("phone_no", phoneNo);
                params.put("company", company);
                params.put("designation", designation);
                params.put("presence", presence);


                return params;
            }


        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

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
