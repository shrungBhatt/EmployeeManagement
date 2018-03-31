package activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.projects.shrungbhatt.employeemanagement.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements Validator.ValidationListener {


    @NotEmpty
    @Email
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


    }

    @OnClick({R.id.email_sign_in_button, R.id.user_sign_up_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                mValidator.validate();
                break;
            case R.id.user_sign_up_button:
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        //redirect to home screen.

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
