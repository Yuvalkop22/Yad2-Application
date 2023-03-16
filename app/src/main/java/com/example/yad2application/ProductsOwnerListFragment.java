package com.example.yad2application;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yad2application.ProductModel.Product;
import com.example.yad2application.ProductModel.ProductModel;
import com.example.yad2application.databinding.FragmentProductsOwnerListBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsOwnerListFragment extends Fragment {
    FragmentProductsOwnerListBinding binding;
    ProductRecyclerAdapter adapter;
    ProductsListFragmentViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductsListFragmentViewModel.class);
    }

    Bundle bundle = new Bundle();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductsOwnerListBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        binding.productsrecyclerListOwner.setHasFixedSize(true);
        binding.productsrecyclerListOwner.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = null;
        adapter = new ProductRecyclerAdapter(getLayoutInflater(),viewModel.getDataOwner().getValue());
        binding.productsrecyclerListOwner.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Log.d("TAG", "Row was clicked " + pos);
                Bundle bundle = new Bundle();
                bundle.putInt("pos",pos); // Put anything what you want
                getParentFragmentManager().setFragmentResult("posClicked",bundle);
                Log.d("TAG", pos + "");
                ProductPageFragment productPageFragment = new ProductPageFragment();
                productPageFragment.setArguments(bundle);

                Navigation.findNavController(view).navigate(R.id.productPageFragment);
            }
        });
        binding.progressBarOwner.setVisibility(View.GONE);

        viewModel.getDataOwner().observe(getViewLifecycleOwner(),list->{
            adapter.setData(list);
        });

        ProductModel.instance().EventStudentsListLoadingState.observe(getViewLifecycleOwner(),status->{
            binding.swipeRefreshOwner.setRefreshing(status == ProductModel.LoadingState.LOADING);
        });

        binding.swipeRefreshOwner.setOnRefreshListener(()->{
            reloadData();
        });

        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ProductsListFragmentViewModel.class);
    }

    void reloadData(){
//        binding.progressBar.setVisibility(View.VISIBLE);
        ProductModel.instance().refreshAllProducts();
    }
    static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView nameTv;
        TextView idTv;
        CheckBox cb;
        List<Product> data;
        ImageView avatarImage;
        public ProductViewHolder(@NonNull View itemView, ProductRecyclerAdapter.OnItemClickListener listener, List<Product> data) {
            super(itemView);
            this.data = data;
            nameTv = itemView.findViewById(R.id.studentlistrow_name_tv);
            idTv = itemView.findViewById(R.id.studentlistrow_id_tv);
            avatarImage = itemView.findViewById(R.id.studentlistrow_avatar_img);
            cb = itemView.findViewById(R.id.studentlistrow_cb);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int)cb.getTag();
                    Product st = data.get(pos);
                    st.cb = cb.isChecked();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(pos);
                }
            });
        }

        public void bind(Product st, int pos) {
            nameTv.setText(st.name);
            idTv.setText(st.description);
            cb.setChecked(st.cb);
            cb.setTag(pos);
            if (st.getAvatarUrl()  != null && st.getAvatarUrl().length() > 5) {
                Picasso.get().load(st.getAvatarUrl()).placeholder(R.drawable.avatar).into(avatarImage);
            }else{
                avatarImage.setImageResource(R.drawable.avatar);
            }
        }
    }

    public static class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductViewHolder>{
        OnItemClickListener listener;
        public interface OnItemClickListener{
            void onItemClick(int pos);
        }

        LayoutInflater inflater;
        List<Product> data;
        public void setData(List<Product> data){
            this.data = data;
            notifyDataSetChanged();
        }
        public ProductRecyclerAdapter(LayoutInflater inflater, List<Product> data){
            this.inflater = inflater;
            this.data = data;
        }

        void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.products_list_row,parent,false);
            return new ProductViewHolder(view,listener, data);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product st = data.get(position);
            holder.bind(st,position);
        }

        @Override
        public int getItemCount() {
            if (data == null) return 0;
            return data.size();
        }

    }

}