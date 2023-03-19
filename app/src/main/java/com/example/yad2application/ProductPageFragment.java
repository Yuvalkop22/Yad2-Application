package com.example.yad2application;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yad2application.Model.CurrencyConversion;
import com.example.yad2application.Model.CurrencyConversionModel;
import com.example.yad2application.Model.Joke;
import com.example.yad2application.Model.JokeModel;
import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.Product;
import com.example.yad2application.Model.Product;
import com.example.yad2application.Model.User;
import com.example.yad2application.databinding.FragmentProductPageBinding;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ProductPageFragment extends Fragment {
    private FragmentProductPageBinding binding;
    ProductsListFragmentViewModel viewModel;
    private String selectedCurrency;
    LiveData<CurrencyConversion> data;
    String currentCurrency;
    double currentPrice;
    String priceString;
    int currentCurrenctPosition;

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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.CURRENCY, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.currencySpinner.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener("productDetail", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d("TAG", "In product page -> name: " + result.getString("name"));

                Product pr = (Product) result.getSerializable("product");
                binding.textProductNamePreview.setText(pr.getName());
                binding.textCategoryPreview.setText(pr.getCategory());
                binding.textPricePreview.setText(pr.getPrice());
                priceString = pr.getPrice();
                binding.textDescriptionPreview.setText(pr.getDescription());
                Picasso.get().load(pr.getAvatarUrl()).into(binding.productImg);

                if (pr.customerEmail != null || pr.ownerEmail == Model.instance().getCurrentUser().getEmail()){
                    binding.btnBuyProdPage.setVisibility(View.GONE);
                } else{
                    binding.btnBuyProdPage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String email = Model.instance().getCurrentUser().getEmail();
                            Model.instance().order(pr,email,(unused)->{
                                Toast.makeText(getActivity(),"Thanks",Toast.LENGTH_LONG).show();
                                Navigation.findNavController(view).navigate(R.id.productsListFragment);

                            });
                        }
                    });
                }
            }
        });


            currentCurrenctPosition = 0;
            binding.currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ///Currency Selector
                currentCurrency = "USD";
                try {
                    currentPrice = Double.parseDouble(priceString);
                }catch (NumberFormatException e){
                    Log.d("TAG", "Failed parse to double: " + e.getMessage());
                }
                selectedCurrency =adapter.getItem(position).toString();
                if(position != currentCurrenctPosition) {
                    currentCurrenctPosition = position;
                    Log.d("TAG", "Currency clicked: " + selectedCurrency);
                    Log.d("TAG", "Currency current: " + currentCurrency);
                    Log.d("TAG", "Currency price: " + currentPrice);
                    data = CurrencyConversionModel.instance.convertCurrency(currentCurrency,selectedCurrency,currentPrice);
                    data.observe(getViewLifecycleOwner(), result->{
                        String newAmount = result.getNew_amount().toString();
                        Log.d("TAG", "Price returned: " + newAmount);
                        binding.textPricePreview.setText(newAmount);
                        currentCurrency = result.getNew_currency();
                        try {
                            currentPrice = Double.parseDouble(result.getNew_amount());
                        }catch (NumberFormatException e) {
                            Log.d("TAG", "onCreateView: " + e.getMessage());
                        }});

                }
                }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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