package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.ViewPagerAdapter;
import com.example.nhahang.Fragments.HomeFragment;
import com.example.nhahang.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.play.integrity.internal.a;
import com.google.firebase.auth.FirebaseAuth;
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

        authSigined();



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
                        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
                        break;
                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.action_menu);
                        break;
                    case 2:
                        binding.bottomNavigation.setSelectedItemId(R.id.action_reservation);
                        break;
                    case 3:
                        binding.bottomNavigation.setSelectedItemId(R.id.action_user);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }
}