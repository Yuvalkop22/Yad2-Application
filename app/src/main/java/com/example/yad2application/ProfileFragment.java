package com.example.yad2application;

import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yad2application.Model.Model;
import com.example.yad2application.ProductModel.Product;
import com.example.yad2application.ProductModel.ProductModel;
import com.example.yad2application.databinding.FragmentFirstBinding;
import com.example.yad2application.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    // Create a static AsyncTask class to retrieve the product category from the database
//    private static class ProductCategoryTask extends AsyncTask<Void, Void, String> {
//        private WeakReference<ProfileFragment> activityRef;
//
//        public ProductCategoryTask(ProfileFragment activity) {
//            activityRef = new WeakReference<>(activity);
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {
//            // Retrieve the product category from the database
//            return ProductModel.instance().getProductByName("yuv").getCategory();
//        }
//
//        @Override
//        protected void onPostExecute(String category) {
//            // Get a reference to the activity (if it still exists)
//            ProfileFragment activity = activityRef.get();
//            if (activity == null || activity.isRemoving()) {
//                return;
//            }
//
//            // Set the product category on the UI thread
//            activity.binding.userEmailTv.setText(category);
//        }
//    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        super.onCreate(savedInstanceState);
//        ProductCategoryTask task = new ProductCategoryTask(this);
//        task.execute();


        binding.btnAllOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.productsOwnerListFragment);
            }
        });

        binding.btnAllCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.productsCustomerListFragment);
            }
        });

        FragmentActivity parentActivity = getActivity();
        parentActivity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.removeItem(R.id.profileFragment);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        },getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}