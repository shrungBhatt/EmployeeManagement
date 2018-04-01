package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
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
import utils.MySharedPreferences;
import utils.URLGenerator;

public class Activity_Login extends BaseActivity implements Validator.ValidationListener {


    private final String TAG = getClass().getSimpleName();
    @NotEmpty
    @BindView(R.id.user_email)
    AutoCompleteTextView mUserEmail;
    @NotEmpty
    @Password
    @BindView(R.id.user_password)
    EditText mUserPassword;
    @BindView(R.id.sign_in_as_photographer)
    CheckBox mSignInAsPhotographer;
    @BindView(R.id.email_sign_in_button)
    Button mEmailSignInButton;
    @BindView(R.id.user_sign_up_button)
    Button mUserSignUpButton;

    private Validator mValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        boolean status = MySharedPreferences.getStoredLoginStatus(Activity_Login.this);
        if (status) {
            Intent i;
            if (MySharedPreferences.isAdminLoggedOn(this)) {
                i = new Intent(this, Activity_Homescreen.class);
            } else {
                i = new Intent(this, Activity_Homescreen.class);
            }
            startActivity(i);
            finish();
        }



    }

    @OnClick({R.id.email_sign_in_button, R.id.user_sign_up_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                mValidator.validate();
                break;
            case R.id.user_sign_up_button:
                startActivity(new Intent(this, SignupActivity.class));
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        //redirect to home screen.
        requestLogin(mUserEmail.getText().toString(), mUserPassword.getText().toString());
    }

    private void requestLogin(final String user_name, final String password) {
        showProgressBar(this, TAG);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.LOGIN_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressBar();
                        try {
                            if (response != null &&
                                    !response.equals("Wrong Username or Password")) {
                                MySharedPreferences.setStoredLoginStatus(Activity_Login.this, true);
                                MySharedPreferences.setStoredUsername(Activity_Login.this, user_name);
                                Intent i;
                                if (user_name.equalsIgnoreCase("admin")&&password.equalsIgnoreCase("admin")) {
                                    MySharedPreferences.setIsAdminLoggedOn(Activity_Login.this, true);
                                    i = new Intent(Activity_Login.this, Activity_Homescreen.class);
                                    finish();
                                } else {
                                    i = new Intent(Activity_Login.this, Activity_Homescreen.class);
                                    finish();
                                }
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT)
                                        .show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressBar();
                        Log.e(TAG, error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", user_name);
                params.put("password", password);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


    private void logInUser(final String user_name, final String password) {
        showProgressBar(this, TAG);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.LOGIN_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressBar();
                        try {
                            if (response != null &&
                                    !response.equals("Wrong Username or Password")) {
                                MySharedPreferences.setStoredLoginStatus(Activity_Login.this, true);
                                MySharedPreferences.setStoredUsername(Activity_Login.this, user_name);
                                Intent i;
                                if (user_name.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {
                                    MySharedPreferences.setIsAdminLoggedOn(Activity_Login.this, true);
//                                    i = new Intent(Activity_Login.this, Activity_DoctorAppointmentList.class);
//                                    finish();
                                } else {
//                                    i = new Intent(Activity_Login.this, Activity_DiseaseList.class);
//                                    finish();
                                }
//                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT)
                                        .show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressBar();
                Log.e(TAG, error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", user_name);
                params.put("password", password);
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
