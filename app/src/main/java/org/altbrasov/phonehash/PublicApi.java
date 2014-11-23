package org.altbrasov.phonehash;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by adi on 11/23/14
 */
public interface PublicApi {
    @GET("/test")
    void test(Callback<String> cb);



    @FormUrlEncoded
    @POST("/messages")
    void sendMessage(@Field("message") String message, Callback<JsonElement> callback);


    @GET("/messages")
    void getMessages(@Query("hash") String hash, Callback<String[]> callback);

    @FormUrlEncoded
    @POST("/test_push")
    void testPush(@Field("regId") String regId, Callback<JsonElement> callback);

}
