package activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
