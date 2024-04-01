package com.example.design1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class Progress extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#283593")));

        fragmentManager = getSupportFragmentManager();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        replaceFragment(new CreateProcessFragment(), "create_process_fragment_tag");
                        break;
                    case 1:
                        replaceFragment(new ProcessManagementFragment(), "process_management_fragment_tag");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });

        // 默认显示第一个 Fragment
        if (savedInstanceState == null) {
            replaceFragment(new CreateProcessFragment(), "create_process_fragment_tag");
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(null); // 将 Fragment 添加到返回栈中，以便可以通过返回键返回上一个 Fragment
        transaction.commit();
    }
}