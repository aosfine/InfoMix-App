package com.yourname.infomix.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class ApiClient {
    private static final OkHttpClient client = new OkHttpClient();
    
    public static String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
