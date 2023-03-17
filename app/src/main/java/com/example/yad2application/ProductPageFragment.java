package com.example.yad2application;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yad2application.ProductModel.Product;
import com.example.yad2application.ProductModel.ProductModel;
import com.example.yad2application.databinding.FragmentProductPageBinding;
import com.squareup.picasso.Picasso;


public class ProductPageFragment extends Fragment {
    private FragmentProductPageBinding binding;
    ProductsListFragmentViewModel viewModel;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        setHasOptionsMenu(true);
        binding = FragmentProductPageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(ProductsListFragmentViewModel.class);

        getParentFragmentManager().setFragmentResultListener("productDetail", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d("TAG", "In product page -> name: " + result.getString("name"));

                Product pr = (Product) result.getSerializable("product");
                binding.textProductNamePreview.setText(pr.getName());
                binding.textCategoryPreview.setText(pr.getCategory());
                binding.textPricePreview.setText(pr.getPrice());
                binding.textDescriptionPreview.setText(pr.getDescription());
                Picasso.get().load(pr.getAvatarUrl()).into(binding.productImg);

                binding.btnBuyProdPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ProductModel.instance().deleteProduct(pr,(unused)->{
//                            Navigation.findNavController(view).navigate(R.id.productsListFragment);
//                        });
                        String email = ProductModel.instance().getCurrentUser().getEmail();
                        ProductModel.instance().order(pr.getName(),email,(unused)->{
                            Navigation.findNavController(view).navigate(R.id.productsListFragment);

                        });
                    }
                });
            }
        });



        binding.btnCancelProdPage.setOnClickListener((view1)->{
            Navigation.findNavController(view1).popBackStack();
        });

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