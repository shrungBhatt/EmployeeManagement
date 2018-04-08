package activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import service.TrackerService;
import utils.MySharedPreferences;
import utils.URLGenerator;

public class Activity_Homescreen extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    private static final int PERMISSIONS_REQUEST = 1;
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

        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

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

    private void startTrackerService() {
        startService(TrackerService.newIntent(this, MySharedPreferences.getStoredUsername(this)));
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
                        res_fetchAttendances = gson.fromJson(jsonObject.toString(),
                                Res_FetchAttendances.class);
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

    private void checkIsSubmitted() {
        showProgressBar(this, TAG);

        final String userName = MySharedPreferences.getStoredUsername(this);
        final String date = mFormatter.format(new Date());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.CHECK_IS_SUBMITTED,
                response -> {
                    hideProgressBar();
                    if(response.equalsIgnoreCase("TRUE")){
                        showToastMessage("You have already submitted today's attendance");
                    }else if(response.equalsIgnoreCase("FALSE")){

                        startActivity(new Intent(this, SubmitAttendanceActivity.class));
                    }

                }, error -> {
            hideProgressBar();
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", userName);
                params.put("date",date);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out_menu_item) {
            if(isNetworkAvailableAndConnected()){
                MySharedPreferences.setStoredLoginStatus(this,false);
                Intent i = new Intent(this, Activity_Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if(id == R.id.menu_user_profile){
            startActivity(new Intent(this,Activity_Profile.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);

        return true;
    }

    @OnClick(R.id.add_attendance_fab_button)
    public void onViewClicked() {
        checkIsSubmitted();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        }else {
            finish();
        }
    }
}
