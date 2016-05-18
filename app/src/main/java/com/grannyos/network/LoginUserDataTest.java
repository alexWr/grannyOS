package com.grannyos.network;

import com.google.gson.annotations.SerializedName;


public class LoginUserDataTest {

    public LoginUserDataTest(){}

    @SerializedName("provider")
    String provider = "email";

    @SerializedName("uuid")
    String uuid = "test6@test.com";

    @SerializedName("token")
    String token = "test";

}
