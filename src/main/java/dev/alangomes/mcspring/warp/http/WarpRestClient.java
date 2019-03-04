package dev.alangomes.mcspring.warp.http;

import dev.alangomes.mcspring.warp.model.WarpDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WarpRestClient {

    @POST("warp")
    Call<WarpDTO> createWarp(@Body WarpDTO warp);

    @GET("warp/{name}")
    Call<WarpDTO> getWarp(@Path("name") String name);

}
