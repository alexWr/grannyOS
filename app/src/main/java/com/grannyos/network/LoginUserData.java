package com.grannyos.network;

import com.google.gson.annotations.SerializedName;

/**
 * POJO class for send data to web server if user login
 */

public class LoginUserData {

    public LoginUserData(String firstName, String lastName, String email, String accessToken,
                         String udid, String language, String manufacture, String device, float scale, int width, int height,
                         String name, String version){


        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.accessToken=accessToken;
        this.device=new Device(udid,language,manufacture,device,scale,width,height,name,version);
    }

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("provider")
    public String provider="google+";

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("email")
    public String email;

    @SerializedName("token")
    public String accessToken;

    @SerializedName("type")
    public String type="tab";

    @SerializedName("device")
    public Device device;

    public class Device{

        public Device(String udid, String language, String manufacture, String device, float scale, int width, int height, String name, String version){
            this.udid=udid;
            this.language=language;
            this.manufacture=manufacture;
            this.device=device;
            this.scale=scale;
            this.width=width;
            this.height=height;
            this.os = new OS(name,version);
        }

        @SerializedName("udid")
        public String udid;

        @SerializedName("language")
        public String language;

        @SerializedName("manufacture")
        public String manufacture;

        @SerializedName("device")
        public String device;

        @SerializedName("scale")
        public float scale;

        @SerializedName("width")
        public int width;

        @SerializedName("height")
        public int height;

        @SerializedName("os")
        public OS os;

        public class OS{

            public OS(String name, String version){
                this.name=name;
                this.version=version;
            }
            @SerializedName("name")
            public String name;

            @SerializedName("type")
            public String type = "Android";

            @SerializedName("version")
            public String version;

        }

    }
}
