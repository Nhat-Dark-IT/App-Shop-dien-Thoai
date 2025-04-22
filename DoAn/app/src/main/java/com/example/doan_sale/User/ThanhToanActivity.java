package com.example.doan_sale.User;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.doan_sale.R;
import com.example.doan_sale.ThankYouActivity;
import com.example.doan_sale.model.GioHang;
import com.example.doan_sale.model.Voucher;
import com.example.doan_sale.ui.DBHelper;
import com.example.doan_sale.ui.MainActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ThanhToanActivity extends AppCompatActivity implements ThanhToanAdapter.ThanhToanCallBack {
    RecyclerView rvListCode;
    ArrayList<GioHang> lstPro;
    static TextView tongtienTT;
    EditText voucher;
    Button apply, confirm;
    ImageButton back;
    ThanhToanAdapter productAdapter;
    EditText addre;
    DBHelper dbHelper = new DBHelper(ThanhToanActivity.this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        rvListCode = findViewById(R.id.rvList3);
        tongtienTT = (TextView) findViewById(R.id.payment_total_amount);
        voucher = (EditText) findViewById(R.id.payment_discount_code_input);
        apply = (Button)findViewById(R.id.payment_apply_coupon_button);
        confirm = (Button)findViewById(R.id.payment_confirm_button);
        back = (ImageButton) findViewById(R.id.backtt);
        addre = findViewById(R.id.edt_Address);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        lstPro = (ArrayList<GioHang>) intent.getSerializableExtra("giohang");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThanhToanActivity.this, GioHangActivity.class);
                startActivity(intent);
            }
        });
        
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = getUsernameFromSharedPreferences();
                String address = addre.getText().toString().trim();
                if (address.isEmpty()) {
                    Toast.makeText(ThanhToanActivity.this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Sử dụng lstPro thay vì MainActivity.manggiohang
                long orderId = dbHelper.addOrderWithDetails(username, lstPro, address);

                // trong phần xử lý khi thanh toán thành công
                if (orderId != -1) {
                    // Thành công, thông báo cho người dùng và xóa giỏ hàng
                    Toast.makeText(ThanhToanActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    
                    // Clone danh sách sản phẩm đã thanh toán thành công để lưu vào đơn hàng 
                    ArrayList<GioHang> processedItems = new ArrayList<>(lstPro);
                    
                    // Xóa khỏi giỏ hàng toàn cục các sản phẩm đã thanh toán
                    for (GioHang item : processedItems) {
                        for (int i = 0; i < MainActivity.manggiohang.size(); i++) {
                            if (MainActivity.manggiohang.get(i).getIdsp() == item.getIdsp()) {
                                MainActivity.manggiohang.remove(i);
                                break;
                            }
                        }
                    }
                    
                    // Xóa khỏi danh sách hiện tại
                    lstPro.clear();
                    productAdapter.notifyDataSetChanged();
                    EventUtil();
                    
                    // Chuyển sang màn hình ThankYouActivity
                    Intent i = new Intent(ThanhToanActivity.this, ThankYouActivity.class);
                    startActivity(i);
                    finish(); // Kết thúc activity này để khi quay lại sẽ tạo mới
                }
            }
        });

        apply.setOnClickListener(v->applyVoucher());
        
        // Thiết lập adapter với danh sách sản phẩm nhận từ intent
        productAdapter = new ThanhToanAdapter(this, lstPro, this);
        rvListCode.setLayoutManager(new LinearLayoutManager(this));
        productAdapter.setProCallBack(this);
        rvListCode.setAdapter(productAdapter);
        
        EventUtil();
    }

    public void EventUtil() {
        long tongtien = 0;
        // Sử dụng lstPro thay vì MainActivity.manggiohang
        for (int i = 0; i < lstPro.size(); i++) {
            tongtien += lstPro.get(i).getGiasp();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtienTT.setText(decimalFormat.format(tongtien) + " Đ");
    }
    
    private void applyVoucher() {
        String voucherCode = voucher.getText().toString().trim();

        // Check if voucher code exists in database
        Voucher voucher = dbHelper.getVoucherByCode(voucherCode);
        if (voucher == null) {
            Toast.makeText(this, "Mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if voucher is applicable to any product in cart
        boolean isApplicable = false;
        int productId = voucher.getVoucherProductId();
        for (GioHang gioHang : lstPro) {  // Sử dụng lstPro
            if (gioHang.getIdsp() == productId) {
                isApplicable = true;
                break;
            }
        }
        if (!isApplicable) {
            Toast.makeText(this, "Mã giảm giá không áp dụng cho sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Apply voucher discount
        int discount = voucher.getDiscount();
        long tongtien = 0;
        boolean isDiscountAppliedAgain = false;

        for (GioHang gioHang : lstPro) {  // Sử dụng lstPro
            if (gioHang.getIdsp() == productId) {
                if (!gioHang.isDiscountApplied()) {
                    long discountedPrice = gioHang.getGiasp() - discount;

                    if (discountedPrice < 0) {
                        discountedPrice = 0;  // Ensure the price doesn't go below 0
                    }

                    tongtien += discountedPrice;

                    gioHang.setGiasp(discountedPrice);
                    gioHang.setDiscountApplied(true); // Đánh dấu đã áp dụng mã giảm giá

                } else {
                    isDiscountAppliedAgain = true; 
                    tongtien += gioHang.getGiasp();
                }
            } else {
                tongtien += gioHang.getGiasp();
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtienTT.setText(decimalFormat.format(tongtien) + " Đ");
        productAdapter.notifyDataSetChanged();

        if (isDiscountAppliedAgain) {
            Toast.makeText(this, "Mã giảm giá chỉ được sử dụng một lần.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đã áp dụng mã giảm giá.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemLongClicked(GioHang gioHang) {
        // Xử lý sự kiện khi nhấn giữ item
    }
    
    private String getUsernameFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        return preferences.getString("username", null); // null là giá trị mặc định nếu không tìm thấy
    }
}