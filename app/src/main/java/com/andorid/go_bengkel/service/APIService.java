package com.andorid.go_bengkel.service;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA4Q3ei58:APA91bFe2cZ5MCPfq1FCz-nmbdi77KQOKzqYu7a-icOktQQjkXFTIRqVK59v3bciq8Bxbdn7hnQzGp8W-pwHtDrpr7hRVBdaxbqlmVQa-Wtmes-oMyJJGtHnWEYNfyBLJnKRet-I41Rx" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}