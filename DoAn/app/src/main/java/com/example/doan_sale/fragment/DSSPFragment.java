package com.example.doan_sale.fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.doan_sale.Product.CateAdapter;
import com.example.doan_sale.Product.ChiTietSanPhamActivity;
import com.example.doan_sale.Product.ProDataQuery;
import com.example.doan_sale.Product.ProductAdapter;
import com.example.doan_sale.R;
import com.example.doan_sale.model.Product;
import com.example.doan_sale.ui.DBHelper;
import com.example.doan_sale.ui.MainAdapter;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.recyclerview.widget.GridLayoutManager;

public class DSSPFragment extends Fragment implements CateAdapter.CateCallBack {
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categoryNames;
    private RecyclerView phoneList;
    private List<Product> phoneProducts;
    private List<Product> headphoneProducts;
    private List<Product> chargerProducts;
    private CateAdapter phoneAdapter;
    private CateAdapter headphoneAdapter;
    private CateAdapter chargerAdapter;
    private DBHelper dbHelper;
    //code moi
    private TabLayout tabLayoutOrderStatus;
    private TextView tvStatusMessage;
    private ProgressBar progressBar;
    private CateAdapter currentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d_s_s_p, container, false);
        
        // Khởi tạo TabLayout
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        
        // Khởi tạo RecyclerView cho danh sách điện thoại
        phoneList = view.findViewById(R.id.phoneList);
        phoneList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        // Khởi tạo adapter
        phoneAdapter = new CateAdapter(new ArrayList<>());
        headphoneAdapter = new CateAdapter(new ArrayList<>());
        chargerAdapter = new CateAdapter(new ArrayList<>());
        
        // Thiết lập callback cho adapter
        phoneAdapter.setCateCallBack(this);
        headphoneAdapter.setCateCallBack(this);
        chargerAdapter.setCateCallBack(this);
        
        // Gán adapter mặc định (Điện thoại)
        phoneList.setAdapter(phoneAdapter);
        
        // Khởi tạo DBHelper
        dbHelper = new DBHelper(getContext());
        
        // Xử lý chuyển tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadPhoneProducts();
                        break;
                    case 1:
                        loadHeadphoneProducts();
                        break;
                    case 2:
                        loadChargerProducts();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        // Mặc định hiển thị sản phẩm điện thoại
        loadPhoneProducts();
        
        return view;
    }
    
    // Phương thức tải sản phẩm điện thoại (categoryId = 1)
    private void loadPhoneProducts() {
        phoneProducts = dbHelper.getProductsByCategoryId(1);
        phoneAdapter.updateData(new ArrayList<>(phoneProducts));
        switchAdapter(phoneAdapter);
    }
    
    // Phương thức tải sản phẩm tai nghe (categoryId = 2)
    private void loadHeadphoneProducts() {
        headphoneProducts = dbHelper.getProductsByCategoryId(2);
        headphoneAdapter.updateData(new ArrayList<>(headphoneProducts));
        switchAdapter(headphoneAdapter);
    }
    
    // Phương thức tải sản phẩm sạc điện thoại (categoryId = 3)
    private void loadChargerProducts() {
        chargerProducts = dbHelper.getProductsByCategoryId(3);
        chargerAdapter.updateData(new ArrayList<>(chargerProducts));
        switchAdapter(chargerAdapter);
    }
    
    // Phương thức chuyển đổi adapter
    private void switchAdapter(RecyclerView.Adapter adapter) {
        phoneList.setAdapter(adapter);
    }

    private void toggleRecyclerViewVisibility(boolean showList) {
        phoneList.setVisibility(showList ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(showList ? View.GONE : View.VISIBLE); // Ẩn hiển thị progressBar khi có dữ liệu
        tvStatusMessage.setVisibility(showList ? View.GONE : View.VISIBLE);
        tvStatusMessage.setText(showList ? "" : "Không có sản phẩm nào."); // Cập nhật thông báo khi không có dữ liệu
    }

    // code moi

    @Override
    public void onItemClick(int id) {
        Product product = dbHelper.getProductById(id);
        Intent intent = new Intent(getActivity(), ChiTietSanPhamActivity.class);
        intent.putExtra("thongtinsanpham", product);
        startActivity(intent);
    }

    private void updateRecyclerView(String selectedCategory) {
        phoneList.setVisibility(View.GONE);

        if (selectedCategory.equals("Điện thoại")) {
            phoneList.setVisibility(View.VISIBLE);
        } else if (selectedCategory.equals("Tai nghe")) {
        } else if (selectedCategory.equals("Sạc điện thoại")) {
        }
    }


}
