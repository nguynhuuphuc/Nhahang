package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.StaffManagementAdapter;
import com.example.nhahang.BottomSheetDialogFragments.WarningDeleteItemFragment;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickViewInWaringDelete;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.Requests.PositionIdRequest;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.databinding.ActivityStaffManagementBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffManagementActivity extends AppCompatActivity {
    private ActivityStaffManagementBinding binding;
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    private StaffManagementAdapter staffManagementAdapter;
    private List<Employee> employees;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        binding.staffManageRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        employees = new ArrayList<>();
        staffManagementAdapter = new StaffManagementAdapter(employees,this);
        binding.staffManageRV.setAdapter(staffManagementAdapter);


        dbRealtime.getReference("staffManagementChange").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChange = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(isChange)){
                    dbRealtime.getReference("staffManagementChange").setValue(false);
                    loadDataRV();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        loadDataRV();




    }
    private void loadDataRV(){
        if(!employees.isEmpty()){
            employees.clear();
        }
        inProgress(true);
        PositionIdRequest positionIdRequest = new PositionIdRequest("NV");
        ApiService.apiService.getListEmployeesByPositionId(positionIdRequest)
                .enqueue(new Callback<ArrayList<Employee>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                            switch (response.code()){
                                case 500:
                                    Toast.makeText(StaffManagementActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                                    break;
                                default://response success
                                    employees.addAll(response.body());
                                    staffManagementAdapter.notifyDataSetChanged();
                                    break;
                            }
                        inProgress(false);


                    }

                    @Override
                    public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                    }
                });

    }

    private void inProgress(Boolean isIn){
        if(isIn){
            binding.staffManageRV.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            return;
        }
        binding.staffManageRV.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.staff_management_menu,menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toolbar.setNavigationIcon(null);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                binding.toolbar.setNavigationIcon(R.drawable.close_icon);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add:
               startActivity(new Intent(this,RegisterNewStaffActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}