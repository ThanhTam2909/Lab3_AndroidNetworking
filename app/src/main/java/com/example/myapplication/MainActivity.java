package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String GET_URL = "http://103.118.28.46:3000/get-quote";

    private static final String POST_URL = "http://103.118.28.46:3000/add-quote";

    private static final String GET_LIST_URL = "http://103.118.28.46:3000/get-list-quote";
    TextView textView;
    Button mybutton1, mybutton2, mybutton3;

    List<String> qoutes = new ArrayList<>();
    LinearLayout load;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.thongbao);
        mybutton1 = findViewById(R.id.button1);
        mybutton2 = findViewById(R.id.button2);
        mybutton3 = findViewById(R.id.button3);
        load = findViewById(R.id.layou_load);

    }

    private void sendGethttpConnection() throws Exception {
        URL url = new URL(GET_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(responseBody, "UTF-8");
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.beginObject();
            String quote = getValue("quote", jsonReader);
            load.setVisibility(View.GONE);
            textView.setText(quote);
        } else {
            //Todo: xy ly ket qua tra ve la loi
            load.setVisibility(View.VISIBLE);
            //System.out.print("Error");
            String error = getErrorMessage(connection);
            // hiển thị thông báo lỗi lên textview hoặc thực hiện các hành động phù hợp khác
            textView.setText(error);
        }
    }

    //send post request
    private  void sendPostHttpConnection() throws Exception{
        URL urlPOST = new URL(POST_URL);

        // ở giao thức http
        HttpURLConnection cont = (HttpURLConnection) urlPOST.openConnection();

        // phương thức sử dụng là post
        cont.setRequestMethod("POST");
        // config request
        cont.setRequestProperty("Content-Type", "application/json");
        cont.setRequestProperty("Accept", "application/json");

        cont.setDoOutput(true);

        // xu ly data body
        JSONObject data = new JSONObject();
        //set key value
        data.put("name", "Tammy");

        // dua data object json vao request post
        byte[] postData = data.toString().getBytes(StandardCharsets.UTF_8);
        try (OutputStream outputStream = cont.getOutputStream()){
            outputStream.write(postData);
        }

        int responseCode = cont.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // xu ly du lieu tra ve
            InputStream responeBody = cont.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responeBody, "UTF-8");

            //xu ly json trong java
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            jsonReader.beginObject();

            String qoute = getValue("message", jsonReader);
            textView.setText(qoute);

            //todo: xu ly json object de lay du lieu

        } else {
            //Todo: xy ly ket qua tra ve la loi
//            System.out.print("Error");
//            xử lý phản hồi
            String error = getErrorMessage(cont);
//            hiển thị thông báo lỗi lên textview hoặc thực hiện các hành động phù hợp
            textView.setText(error);
        }
    }

    private void getListHTTP(){
        ApiServerci.apiServerci.getListQuote(5).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                qoutes = response.body();
                textView.setText(qoutes.toString());
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });
    }

    // XỬ LÝ DỮ LIỆU TRẢ VỂ  VÀ XỬ LÝ GET
    private String getValue(String key, JsonReader jsonReader) throws Exception {
        String value = "";

        while (jsonReader.hasNext()) {  //doc jsonReader cho den het thi thoi
            String h = jsonReader.nextName();
            if (h.equals(key)) {
                value = jsonReader.nextString();
                break;
            } else {
                jsonReader.skipValue();
            }
        }

        return value;
    }

    private String getErrorMessage(HttpURLConnection connection) throws Exception{
        InputStream inputStream = connection.getErrorStream();
        if (inputStream != null){
            InputStreamReader errorStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(errorStreamReader);
            jsonReader.beginObject();
            String errorMessage = getValue("message", jsonReader);
            jsonReader.close();
            return errorMessage;
        }else {
            return "Unknown error";
        }
    }

    public void onClickGetHttp(View view){
        load.setVisibility(View.VISIBLE);
        Log.d( "onClickGetHttp: ", "pressed me");
        try {
            sendGethttpConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClickPostHttp(View view){
        try {
            sendPostHttpConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClickGetListHttp(View view){
//        int num = 5; // so cau noi can lay
        try {
            //sendGetListHttpUrlConnection(num);
            getListHTTP();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}