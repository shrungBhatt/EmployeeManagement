package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.projects.shrungbhatt.employeemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import adapters.Adapter_EmplyoeeList;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.Res_UserProfile;
import utils.MySharedPreferences;
import utils.URLGenerator;

public class Activity_EmplyoeeList extends BaseActivity {


    @BindView(R.id.employee_list_recyclerview)
    RecyclerView mEmployeeListRecyclerview;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);
        ButterKnife.bind(this);

        mEmployeeListRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        fetchUsers();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out_menu_item) {
            if(isNetworkAvailableAndConnected()){
                MySharedPreferences.setStoredLoginStatus(this,false);
                MySharedPreferences.setIsAdminLoggedOn(this,false);
                Intent i = new Intent(this, Activity_Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.employee_list_menu, menu);

        return true;
    }

    private void fetchUsers() {

        showProgressBar(this, TAG);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URLGenerator.FETCH_USERS,
                response -> {
                    hideProgressBar();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Gson gson = new Gson();
                        Res_UserProfile resUserProfile = gson.fromJson(jsonObject.toString(),
                                Res_UserProfile.class);
                        mEmployeeListRecyclerview.setAdapter(new Adapter_EmplyoeeList(this,
                                resUserProfile.getList()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            hideProgressBar();
            error.getMessage();

        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
