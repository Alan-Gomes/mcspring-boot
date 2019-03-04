package dev.alangomes.mcspring.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alangomes.mcspring.converter.LocationConverter;
import dev.alangomes.mcspring.warp.http.WarpRestClient;
import okhttp3.OkHttpClient;
import org.bukkit.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitConfiguration {

    @Value("${http.endpoint:http://localhost:8081/}")
    private String baseUrl;

    private static Retrofit buildRetrofit(String baseUrl, OkHttpClient client) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationConverter()).create();
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    @Scope("singleton")
    WarpRestClient warpRestClientBean() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        final Retrofit retrofit = buildRetrofit(baseUrl, okHttpClient);

        return retrofit.create(WarpRestClient.class);
    }

}
