package com.e.app_bookmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentUris;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.e.JavaClass.JsonDataBookId;
import com.e.JavaClass.JsonMsg;
import com.e.mylibrary.HttpUtil;
import com.e.mylibrary.MyActivity;
import com.e.mylibrary.NetWork;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


@Route(path = "/app_bookmanagement/AddBookActivity")
public class AddBookActivity extends MyActivity {
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    String imagePath = null;
    int bookId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        //????????????
        TextView publishTimeSelector_addBook=findViewById(R.id.publishTimeSelector_addBook);
        publishTimeSelector_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddBookActivity.this,mdateListener,2000,0,1).show();
            }
        });
        //????????????
        picture=findViewById(R.id.bookpic_addBook);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat
                        .checkSelfPermission(AddBookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(AddBookActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 2);
                } else {
                    openAlbum();
                }
            }
        });
        //??????????????????
        Spinner spinner1 =findViewById(R.id.spinner_bookPlace_addBook);
        // ???????????????
        String[] mItems1 = getResources().getStringArray(R.array.bookPlaces);
        // ??????Adapter?????????????????????
        ArrayAdapter<String> adapter1= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //?????? Adapter?????????
        spinner1 .setAdapter(adapter1);

        //??????????????????
        Spinner spinner2 =findViewById(R.id.spinner_type_addBook);
        // ???????????????
        String[] mItems2 = getResources().getStringArray(R.array.types);
        // ??????Adapter?????????????????????
        ArrayAdapter<String> adapter2= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //?????? Adapter?????????
        spinner2 .setAdapter(adapter2);
        /*
        * ??????spinner??????
        * */
        //String selectText = spinner.getSelectedItem().toString();
        LinearLayout cancel_addBook=findViewById(R.id.cancel_addBook);
        cancel_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayout addBook_addBook=findViewById(R.id.addBook_addBook);
        addBook_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });
    }
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
                    .addFormDataPart("bookId", String.valueOf(bookId))
                    .build();
            Request request = new Request.Builder()
                    .url("https://starlibrary.online/haopeng/book/trimage")
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
                                        Toast.makeText(getApplicationContext(),"bookid???"+bookId, Toast.LENGTH_SHORT).show();
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
    private void addBook(){
        if (NetWork.isNetworkConnected(AddBookActivity.this)){
            EditText bookName_edit_addBook=findViewById(R.id.bookName_edit_addBook);
            String bookName=bookName_edit_addBook.getText().toString();
            EditText bookAuthor_edit_addBook=findViewById(R.id.bookAuthor_edit_addBook);
            String bookAuthor=bookAuthor_edit_addBook.getText().toString();
            EditText bookPublish_edit_addBook=findViewById(R.id.bookPublish_edit_addBook);
            String bookPublish=bookPublish_edit_addBook.getText().toString();
            TextView publishTimeSelector_addBook=findViewById(R.id.publishTimeSelector_addBook);
            String publishTime=publishTimeSelector_addBook.getText().toString();
            EditText bookNumber_edit_addBook=findViewById(R.id.bookNumber_edit_addBook);
            EditText bookPage_edit_addBook=findViewById(R.id.bookPage_edit_addBook);
            Spinner spinner1 =findViewById(R.id.spinner_bookPlace_addBook);
            String bookPlace = spinner1.getSelectedItem().toString();
            Spinner spinner2 =findViewById(R.id.spinner_type_addBook);
            String type = spinner2.getSelectedItem().toString();
            EditText bookIntroduction_edit_addBook=findViewById(R.id.bookIntroduction_edit_addBook);
            String bookIntroduction=bookIntroduction_edit_addBook.getText().toString();

            if (!Objects.equals(bookName, "")&&!bookAuthor.equals("")&&!bookPublish.equals("")&&!publishTime.equals("")&&!bookNumber_edit_addBook.getText().toString().equals("")&&!bookPage_edit_addBook.getText().toString().equals("")&&!bookIntroduction.equals("")&&imagePath!=null){
                int bookNumber=Integer.parseInt(bookNumber_edit_addBook.getText().toString());
                int bookPage=Integer.parseInt(bookPage_edit_addBook.getText().toString());
                Log.d("TAG", bookNumber+"");
                String s="https://starlibrary.online/haopeng/book/addbook"+"?bookName="+bookName+"&bookAuthor="+bookAuthor+"&bookPublish="+bookPublish+"&publishTime="+publishTime+"&bookNumber="+bookNumber+"&bookPage="+bookPage+"&bookPlace="+bookPlace+"&type="+type+"&bookIntroduction="+bookIntroduction;
                HttpUtil.sendOkHttpRequest(s, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String responseData = Objects.requireNonNull(response.body()).string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JsonDataBookId jsonDataBookId = new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .create()
                                        .fromJson(responseData, JsonDataBookId.class);

                                if (jsonDataBookId.getStatus() == 200) {
                                    Toast.makeText(AddBookActivity.this, jsonDataBookId.getMsg(), Toast.LENGTH_SHORT).show();
                                    bookId=jsonDataBookId.getData();
                                    httpPic();
                                    finish();
                                }else {
                                    Toast.makeText(AddBookActivity.this, jsonDataBookId.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else {
                Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_LONG).show();
            }

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
    //??????onDateSet??????
    private DatePickerDialog.OnDateSetListener mdateListener=new DatePickerDialog.OnDateSetListener(){

        @SuppressLint("SetTextI18n")
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            TextView publishTimeSelector_addBook=findViewById(R.id.publishTimeSelector_addBook);
            String month=null;
            String day;
            if (monthOfYear>=0&&monthOfYear<=11){
                monthOfYear+=1;
                if (monthOfYear <= 9){
                    month="0"+monthOfYear;
                }else {
                    month=monthOfYear+"";
                }
            }
            if (dayOfMonth <= 9){
                day="0"+dayOfMonth;
            }else {
                day=dayOfMonth+"";
            }
            publishTimeSelector_addBook.setText(year+"-"+month+"-"+day);
            //year???monthOfYear,dayOfMonth?????????????????????????????????????????????????????????????????????????????????
        }
    };
    //???????????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }
    //????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CHOOSE_PHOTO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(AddBookActivity.this, "???????????????????????????", Toast.LENGTH_LONG).show();
            }
        }
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
