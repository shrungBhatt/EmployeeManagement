package activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.projects.shrungbhatt.employeemanagement.R;
import com.roomorama.caldroid.CaldroidFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.Res_FetchAttendances;
import utils.MySharedPreferences;
import utils.URLGenerator;

public class Activity_Homescreen extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.calendar_view_1)
    RelativeLayout mCalendarView;
    @BindView(R.id.add_attendance_fab_button)
    FloatingActionButton mAddAttendanceFabButton;

    private CaldroidFragment caldroidFragment;
    private SimpleDateFormat mFormatter;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        ButterKnife.bind(this);


        mFormatter = new SimpleDateFormat("dd MMM yyyy");

        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        caldroidFragment = new CaldroidFragment();


        // Setup arguments

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);


            caldroidFragment.setArguments(args);
        }


        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_view_1, caldroidFragment);
        t.commit();

        caldroidFragment.setBackgroundDrawableForDate(getResources().
                getDrawable(R.drawable.circlelayout1), new Date());

        fetchAttendance(MySharedPreferences.getStoredUsername(this));

    }


    private void fetchAttendance(final String userName) {
        showProgressBar(this, TAG);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.FETCH_ATTENDANCE,
                response -> {
                    hideProgressBar();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Gson gson = new Gson();
                        Res_FetchAttendances res_fetchAttendances;
                        res_fetchAttendances = gson.fromJson(jsonObject.toString(), Res_FetchAttendances.class);
                        setAttendedDate(res_fetchAttendances);

                    } catch (JSONException e) {
                        hideProgressBar();
                        e.printStackTrace();
                    }
                }, error -> {
                    hideProgressBar();
                    error.getMessage();
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

    private void setAttendedDate(Res_FetchAttendances resFetchAttendances) {

        for (int i = 0; i < resFetchAttendances.getList().size(); i++) {
            try {
                caldroidFragment.setBackgroundDrawableForDate(getDrawable(R.drawable.circlelayout1),
                        mFormatter.parse(resFetchAttendances.getList().get(i).getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        caldroidFragment.refreshView();



    }


    @OnClick(R.id.add_attendance_fab_button)
    public void onViewClicked() {
        startActivity(new Intent(this,SubmitAttendanceActivity.class));
    }


}
