package com.hoanglan.sqrcauthenticationapp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.hoanglan.sqrcauthenticationapp.api.ApiAuth;
import com.hoanglan.sqrcauthenticationapp.api.ApiUser;
import com.hoanglan.sqrcauthenticationapp.api.AuthRes;
import com.hoanglan.sqrcauthenticationapp.api.UserRes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button button;
    String qr_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanSQRC();
            }
        });
    }

    private void scanSQRC() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("QR Scan");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Kiểm tra kết quả quét mã QR
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            // Nếu quét thành công
            if (intentResult.getContents() != null) {
                qr_data = intentResult.getContents();

                // Mở chọn ảnh
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            if (data != null && qr_data!=null) {
                // Lấy đường dẫn của hình ảnh đã chọn
                Uri imageUri = data.getData();
                try {
                    sendImageToServer(imageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void sendImageToServer(Uri imageUri) throws IOException {
        String filePath = RealPathUtil.getRealPath(MainActivity.this,imageUri);

        File file = new File(filePath);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://sqrccd.onrender.com")
                .addConverterFactory(GsonConverterFactory.create()).build();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody qr_body = RequestBody.create(MediaType.parse("multipart/form-data"),qr_data);

        ApiAuth apiService = retrofit.create(ApiAuth.class);
        Call<AuthRes> call = apiService.sendAuth(qr_body,body);

        call.enqueue(new Callback<AuthRes>() {
            @Override
            public void onResponse(Call<AuthRes> call, Response<AuthRes> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Auth Success!", Toast.LENGTH_SHORT).show();
                    String token = response.body().getAccessToken();
                    String id = response.body().getId().toString();
                    ApiUser apiUser = retrofit.create(ApiUser.class);
                    Call<UserRes> call1 = apiUser.getUser(id,"Bearer " + token);
                    call1.enqueue(new Callback<UserRes>() {
                        @Override
                        public void onResponse(Call<UserRes> call1, Response<UserRes> response1) {
                            String message = "You have authenticated with username: " + response1.body().getUsername();
                            showAlertDialog(message);
                        }
                        @Override
                        public void onFailure(Call<UserRes> call1, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Failed To Get User Detail!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Auth Failed!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AuthRes> call, Throwable t) {
                Log.e("Error",t.toString());
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Tắt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Đóng dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}