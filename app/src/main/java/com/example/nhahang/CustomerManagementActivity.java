package com.example.nhahang;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Adapters.CustomerManagementAdapter;
import com.example.nhahang.Adapters.StaffManagementAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.CustomerModel;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.Requests.PositionIdRequest;
import com.example.nhahang.databinding.ActivityStaffManagementBinding;
import com.google.protobuf.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerManagementActivity extends AppCompatActivity {
    private ActivityStaffManagementBinding binding;
    private CustomerManagementAdapter customerManagementAdapter;
    private List<CustomerModel> customerModels;
    private SearchView searchView;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.toolbar.setTitle("Quản lý khách hàng");

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK){
                    Intent data = o.getData();
                    if(data != null){
                        String action = data.getStringExtra("action");
                        switch (action){
                            case "DELETE":
                                CustomerModel employee_deleted = (CustomerModel) data.getSerializableExtra("employee_deleted");
                                customerManagementAdapter.deleteEmployee(employee_deleted);
                                break;

                            case "UPDATE":
                                CustomerModel employee = (CustomerModel) data.getSerializableExtra("employee");
                                customerManagementAdapter.updateEmployee(employee);
                                break;

                            case "NEW ACCOUNT":
                                CustomerModel newEmployee = (CustomerModel) data.getSerializableExtra("new_employee");
                                customerManagementAdapter.addEmployee(newEmployee);
                                break;
                        }

                    }
                }
            }
        });

        Drawable icon = binding.toolbar.getNavigationIcon();
        if (icon != null) {
            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTint(icon, ContextCompat.getColor(this, R.color.black));
            binding.toolbar.setNavigationIcon(icon);
        }
        

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        binding.staffManageRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        customerModels = new ArrayList<>();
        customerManagementAdapter = new CustomerManagementAdapter(customerModels,this,launcher);
        binding.staffManageRV.setAdapter(customerManagementAdapter);


        
        getCustomers();




    }

    private void getCustomers() {
        inProgress(true);
        if(!customerModels.isEmpty()){
            customerModels.clear();
        }
        ApiService.apiService.getCustomers().enqueue(new Callback<ArrayList<CustomerModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CustomerModel>> call, Response<ArrayList<CustomerModel>> response) {
                if(response.isSuccessful()){
                    customerModels.addAll(response.body());
                    customerManagementAdapter.notifyDataSetChanged();
                    inProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CustomerModel>> call, Throwable t) {
                Toast.makeText(CustomerManagementActivity.this, "Server err", Toast.LENGTH_SHORT).show();
                inProgress(false);
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

        MenuItem item = menu.findItem(R.id.add);
        item.setVisible(false);

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
                customerManagementAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customerManagementAdapter.getFilter().filter(newText);
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
               launcher.launch(new Intent(this,RegisterNewStaffActivity.class));
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