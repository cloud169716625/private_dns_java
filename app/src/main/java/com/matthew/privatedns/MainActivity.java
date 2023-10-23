package com.matthew.privatedns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import com.matthew.privatedns.databinding.ActivityMainBinding; // Add this import

public class MainActivity extends AppCompatActivity {
    private MyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new MyViewModel();

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
    }
}
