package com.e.app_users;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.e.mylibrary.Image;
import com.e.mylibrary.JsonMsg;
import com.e.mylibrary.UserIn;
import com.e.util.BaseTActivity;
import com.e.util.NetWork;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

@Route(path = "/app_users/HeadpicUsersActivity")
public class HeadpicActivity extends BaseTActivity {
    private Uri mCameraUri;
    String userName=null;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    String imagePath = null;
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headpic);
        picture=findViewById(R.id.headPict);
        Toolbar toolbar = findViewById(R.id.toolbar_head);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        List<UserIn> newsList = LitePal.where("i =?", "1").find(UserIn.class);
        if(newsList != null && newsList.size()==1){
            for (int i=0;i<newsList.size();i++){
                userName=newsList.get(i).getUserName();
            }
        }else{
            Toast.makeText(getApplicationContext(), "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
        }
        TextView toolbar_cancel=findViewById(R.id.toolbar_cancel);
        toolbar_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat
                        .checkSelfPermission(HeadpicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(HeadpicActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 2);
                } else {
                    openAlbum();
                }
            }
        });
        display();
        TextView fromPict=findViewById(R.id.fromPict);
        fromPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat
                        .checkSelfPermission(HeadpicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(HeadpicActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 2);
                } else {
                    openAlbum();
                }
            }
        });
        TextView fromCamera=findViewById(R.id.fromCamera);
        fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                        Manifest.permission.CAMERA);
                if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
                    //????????????????????????
                    openCamera();
                } else {
                    //??????????????????????????????
                    ActivityCompat.requestPermissions(HeadpicActivity.this,new String[]{Manifest.permission.CAMERA},
                            PERMISSION_CAMERA_REQUEST_CODE);
                }
            }
        });
    }
    //????????????
    private void display() {
        if (NetWork.isNetworkConnected(getApplicationContext())) {
            OkHttpClient mOkHttpClient = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("userName", userName)
                    .build();
            Request request = new Request.Builder()
                    .url("https://starlibrary.online/haopeng/user/reimage")
                    .post(formBody)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Image jsonBeanImage = new Gson()
                                            .fromJson(data, Image.class);
                                    if (jsonBeanImage.getStatus() == 200) {
                                        RequestOptions userAvatarOptions = new RequestOptions()
                                                .signature(new ObjectKey(jsonBeanImage.getData().getSrc()));
                                        if (!HeadpicActivity.this.isDestroyed()){
                                            Glide.with(HeadpicActivity.this)
                                                    .applyDefaultRequestOptions(userAvatarOptions)
                                                    .load(jsonBeanImage.getData().getSrc())
                                                    .into(picture);
                                        }

                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "???????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
        }
    }

    //????????????
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // ????????????
    }
    //???????????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            if (imagePath==null){
                finish();
            }else {
                httpPic();
                Timer timer=new Timer();
                //??????TimerTask????????????????????????????????????????????????
                TimerTask timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                };
                //2s?????????
                timer.schedule(timerTask,500);
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (imagePath==null){
            finish();
        }else {
            httpPic();
            Timer timer=new Timer();
            //??????TimerTask????????????????????????????????????????????????
            TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            };
            //2s?????????
            timer.schedule(timerTask,1000);
        }
    }

    //????????????
    private void httpPic() {
        if (imagePath!=null) {
            File f = new File(imagePath);
            if (f.exists() && f.isFile()) {
                Log.d("?????????", "?????????????????????"+f.length());
                    Luban.with(this)
                            .load(imagePath)
                            .ignoreBy(100)
                            .setTargetDir(getPath())
                            .filter(new CompressionPredicate() {
                                @Override
                                public boolean apply(String path) {
                                    return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".png"));
                                }
                            })
                            .setCompressListener(new OnCompressListener() {
                                @Override
                                public void onStart() {
                                    // ???????????????????????????????????????????????? loading UI
                                }

                                @Override
                                public void onSuccess(File file) {
                                    //??????????????????????????????????????????????????????
                                    imagePath=null;
                                    imagePath=file.getAbsolutePath();
                                    updata();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    //????????????????????????????????????
                                }
                            }).launch();
            }
        }
    }
    //??????
    private void updata(){
            File f = new File(imagePath);
            Log.d("??????","???????????????"+f.length() );
        if (NetWork.isNetworkConnected(getApplicationContext())) {
            OkHttpClient okHttpClient = new OkHttpClient();
            Log.d("imagePath", imagePath);
            File file = new File(imagePath);
            RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", imagePath, image)
                    .addFormDataPart("userName", userName)
                    .build();
            Request request = new Request.Builder()
                    .url("https://starlibrary.online/haopeng/user/reviseimage")
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String data = Objects.requireNonNull(response.body()).string();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JsonMsg jsonMsg = new Gson()
                                            .fromJson(data, JsonMsg.class);
                                    if (jsonMsg.getStatus() == 200) {
                                        Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "???????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
        }
    }
    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    //????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CHOOSE_PHOTO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(HeadpicActivity.this, "???????????????????????????", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //???????????????????????????????????????
                openCamera();
            } else {
                //?????????????????????????????????
                Toast.makeText(this,"?????????????????????",Toast.LENGTH_LONG).show();
            }
        }
    }
    //???????????????
    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ?????????????????????
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // ??????android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    imagePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //??????Android 7.0?????????????????????FileProvider????????????content?????????Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }

            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, PERMISSION_CAMERA_REQUEST_CODE);
            }
        }
    }
    // ???????????????SD???,????????????SD?????????,?????????SD????????????????????????
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // ???????????????SD???,????????????SD?????????,?????????SD????????????????????????
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }
    //??????????????????
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!Objects.requireNonNull(storageDir).exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    //????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                // ???????????????????????????
                // 4.4?????????????????????????????????????????????
                handleImageOnKitKat(data);
            }
        }
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 ????????????uri??????
                    picture.setImageURI(mCameraUri);
                } else {
                    // ????????????????????????
                    picture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                }
            } else {
                Toast.makeText(this,"?????????????????????",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //??????
    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // ?????????document?????????Uri????????????document id??????
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(Objects.requireNonNull(uri).getAuthority())) {
                String id = docId.split(":")[1]; // ????????????????????????id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(Objects.requireNonNull(uri).getScheme())) {
            // ?????????content?????????Uri??????????????????????????????
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // ?????????file?????????Uri?????????????????????????????????
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // ??????????????????????????????
    }
    //???????????????????????????
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // ??????Uri???selection??????????????????????????????
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    //????????????
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "???????????????????????????????????????UFO?????????", Toast.LENGTH_SHORT).show();
        }
    }


}