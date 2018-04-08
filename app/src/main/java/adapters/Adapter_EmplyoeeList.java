package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.projects.shrungbhatt.employeemanagement.R;

import java.util.ArrayList;

import activities.DisplayActivity;
import butterknife.BindView;
import model.Res_UserProfile;

public class Adapter_EmplyoeeList extends RecyclerView.Adapter<Adapter_EmplyoeeList.EmployeeViewHolder> {


    private Context mContext;
    private ArrayList<Res_UserProfile.List> mUsers;

    public Adapter_EmplyoeeList(Context context, ArrayList<Res_UserProfile.List> lists) {
        mContext = context;
        mUsers = lists;
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        return new EmployeeViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        Res_UserProfile.List userData = mUsers.get(position);
        holder.bindUserData(userData);

        holder.mListItemTrackEmployee.setOnClickListener(view -> {
            mContext.startActivity(DisplayActivity.newIntent(mContext, mUsers.get(position).
                    getUsername()));
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {


        TextView mListItemEmployeeName;
        Button mListItemTrackEmployee;
        TextView mListItemEmployeeEmail;
        TextView mListItemEmployeeDesignation;
        TextView mListItemEmployeePresences;
        TextView mListItemEmployeeAddress;

        EmployeeViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.list_item_emplyoee_list, parent, false));

            mListItemEmployeeName = itemView.findViewById(R.id.list_item_employee_name);

            mListItemTrackEmployee = itemView.findViewById(R.id.list_item_track_employee);

            mListItemEmployeeEmail = itemView.findViewById(R.id.list_item_employee_email);

            mListItemEmployeeDesignation = itemView.findViewById(R.id.list_item_employee_designation);

            mListItemEmployeePresences = itemView.findViewById(R.id.list_item_employee_presences);

            mListItemEmployeeAddress = itemView.findViewById(R.id.list_item_employee_address);

        }


        void bindUserData(Res_UserProfile.List userData) {
            mListItemEmployeeName.setText(userData.getUsername());
            mListItemEmployeeEmail.setText(userData.getEmail());
            mListItemEmployeeDesignation.setText(userData.getDesignation());
            mListItemEmployeePresences.setText(userData.getPresence());
            mListItemEmployeeAddress.setText(userData.getAddress());

        }
    }
}
