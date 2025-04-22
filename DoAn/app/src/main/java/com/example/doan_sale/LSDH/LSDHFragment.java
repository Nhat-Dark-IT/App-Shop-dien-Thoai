package com.example.doan_sale.LSDH;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log; // Thêm import cho Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast; // Thêm import cho Toast
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan_sale.R;
import com.example.doan_sale.model.Order;
import com.example.doan_sale.ui.DBHelper;
import com.example.doan_sale.ui.MainActivity;
import java.util.ArrayList; // Thêm import cho ArrayList
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class LSDHFragment extends Fragment {
    private RecyclerView recyclerView;
    private LSDHAdapter adapter;
    private List<Order> orderList;
    private DBHelper dbHelper;
    private ImageButton gobackbtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_l_s_d_h, container, false);
        gobackbtn = view.findViewById(R.id.gobackbtn);
        recyclerView = view.findViewById(R.id.fragmentLSDH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dbHelper = new DBHelper(getActivity());
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        loadOrderData();
        adapter = new LSDHAdapter(getActivity(), orderList);
        recyclerView.setAdapter(adapter);
        return view;
    }
    private void loadOrderData() {
        String username = getUsernameFromSharedPreferences();
        if (username != null && !username.isEmpty()) {
            try {
                orderList = dbHelper.getOrdersByUsername(username);
                
                // Kiểm tra null trước khi sắp xếp
                if (orderList != null && !orderList.isEmpty()) {
                    // Sắp xếp với xử lý ngoại lệ
                    Collections.sort(orderList, new Comparator<Order>() {
                        @Override
                        public int compare(Order o1, Order o2) {
                            try {
                                // Kiểm tra null
                                if (o1.getDate() == null && o2.getDate() == null) return 0;
                                if (o1.getDate() == null) return 1;
                                if (o2.getDate() == null) return -1;
                                return o2.getDate().compareTo(o1.getDate());
                            } catch (Exception e) {
                                Log.e("LSDHFragment", "Lỗi khi so sánh ngày: " + e.getMessage());
                                return 0;
                            }
                        }
                    });
                } else {
                    // Khởi tạo danh sách trống nếu không có đơn hàng
                    orderList = new ArrayList<>();
                    Toast.makeText(getContext(), "Bạn chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("LSDHFragment", "Lỗi khi tải dữ liệu đơn hàng: " + e.getMessage());
                orderList = new ArrayList<>(); // Khởi tạo danh sách trống nếu có lỗi
                Toast.makeText(getContext(), "Đã xảy ra lỗi khi tải lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getUsernameFromSharedPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return preferences.getString("username", "");
    }
}
