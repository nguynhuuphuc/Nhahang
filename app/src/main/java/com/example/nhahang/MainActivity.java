package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.ViewPagerAdapter;
import com.example.nhahang.Fragments.HomeFragment;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.integrity.internal.a;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        authSigined();
        try {
            db.collection("users").document(auth.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserModel model = documentSnapshot.toObject(UserModel.class);
                            if(model == null) return;
                            if(model.getPosition().equals("manager")){
                                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,binding.drawerLayout,binding.toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
                                binding.drawerLayout.addDrawerListener(toggle);
                                toggle.syncState();
                            }
                        }
                    });
        }catch (Exception ignored){}




        setUpViewPager();
        binding.viewPager.setUserInputEnabled(false);



        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_menu:
                        binding.viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_reservation:
                        binding.viewPager.setCurrentItem(2);
                        break;
                    case R.id.action_user:
                        binding.viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.nav_quanLyMenu:
                        startActivity(new Intent(MainActivity.this,MenuManagementActivity.class));
                        break;
                    case R.id.nav_quanLyNV:
                        startActivity(new Intent(MainActivity.this,StaffManagementActivity.class));                        break;
                }
                return true;
            }
        });

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) binding.navigationView.getLayoutParams();

            // Đặt chiều rộng của NavigationView là một nửa của màn hình
        params.width = getResources().getDisplayMetrics().widthPixels / 2;

            // Cập nhật layout params mới
        binding.navigationView.setLayoutParams(params);

    }

    private void authSigined() {
        try{
            db.collection("users")
                    .document(auth.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documet = task.getResult();
                                if(documet.exists()){
                                    String s = "Xin chào "+ documet.getString("name");
                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name",auth.getCurrentUser().getPhoneNumber());
                                    user.put("phone",auth.getCurrentUser().getPhoneNumber());
                                    user.put("email","");
                                    user.put("dateofbirth","");
                                    user.put("sex","");
                                    user.put("avatar","");
                                    user.put("position","staff");
                                    db.collection("users")
                                            .document(auth.getUid())
                                            .set(user);
                                }
                            }
                        }
                    });


        }
        catch (Exception e){
            startActivity(new Intent(this,LoginActivity.class));
        }

    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(MainActivity.this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.toolbarTitle.setText("Đặt bàn");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
                        break;
                    case 1:
                        binding.toolbarTitle.setText("Thực đơn");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_menu);
                        break;
                    case 2:
                        binding.toolbarTitle.setText("Bàn đã đặt");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_reservation);
                        break;
                    case 3:
                        binding.toolbarTitle.setText("Hồ sơ");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_user);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }
}