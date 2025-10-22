package com.example.prm.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ApiClient {
    private static final String BASE_URL = "https://10.0.2.2:5001/api/"; // HTTPS port cho Android Emulator
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Trust all certificates for localhost development
            final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
            };

            OkHttpClient client;
            try {
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                client = new OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustAllCerts[0])
                        .hostnameVerifier((hostname, session) -> true)
                        .build();
            } catch (Exception e) {
                // Fallback to default client if SSL fails
                client = new OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build();
            }

            // Configure Gson to serialize null values
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .create();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    public static CenterApiService getCenterApiService() {
        return getClient().create(CenterApiService.class);
    }

    public static ServiceApiService getServiceApiService() {
        return getClient().create(ServiceApiService.class);
    }

    public static VehicleApiService getVehicleApiService() {
        return getClient().create(VehicleApiService.class);
    }

    public static ProfileApiService getProfileApiService() {
        return getClient().create(ProfileApiService.class);
    }

    // Method để lấy Retrofit instance (sử dụng trong activities)
    public static Retrofit getRetrofitInstance() {
        return getClient();
    }
}
