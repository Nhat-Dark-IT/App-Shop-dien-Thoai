package com.example.doan_sale.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_sale.Product.ChiTietSanPhamActivity;
import com.example.doan_sale.R;
import com.example.doan_sale.model.Product;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MainAdapter adapter;
    private ArrayList<Product> searchResults;
    private DBHelper dbHelper;
    private TextView noResultsText;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        
        try {
            // Khởi tạo các thành phần UI
            recyclerView = findViewById(R.id.search_results_recycler_view);
            noResultsText = findViewById(R.id.no_results_text);
            backButton = findViewById(R.id.back_button);
            
            // Thiết lập RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            
            // Lấy từ khóa tìm kiếm từ Intent
            String searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
            if (searchQuery == null) searchQuery = "";
            
            // Khởi tạo DBHelper và thực hiện tìm kiếm
            dbHelper = new DBHelper(this);
            searchResults = dbHelper.searchProducts(searchQuery);
            
            // Hiển thị kết quả hoặc thông báo không tìm thấy
            if (searchResults != null && !searchResults.isEmpty()) {
                // Sửa tại đây: Truyền trực tiếp một ItemClickListener vào constructor
                adapter = new MainAdapter(this, searchResults, product -> {
                    Intent intent = new Intent(SearchResults.this, ChiTietSanPhamActivity.class);
                    // Sử dụng Bundle để truyền toàn bộ đối tượng sản phẩm
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("thongtinsanpham", product);
                    intent.putExtras(bundle);
                    startActivity(intent);
                });
                
                recyclerView.setAdapter(adapter);
                noResultsText.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                noResultsText.setVisibility(View.VISIBLE);
            }
            
            // Thiết lập nút quay lại
            backButton.setOnClickListener(v -> finish());
            
        } catch (Exception e) {
            Log.e("SearchResults", "Error: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu xảy ra lỗi
        }
    }
}