package com.grannyos.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class is response from weather API
 */

public class WeatherResponse {

    public WeatherResponse(){}

    @SerializedName("city")
    private City city;

    @SerializedName("list")
    private List<WeatherList> weatherList;

    public class City{

        public City(){}

        @SerializedName("name")
        private String name;

        public String getName(){
            return name;
        }
    }

    public class WeatherList {

        public WeatherList(){}

        @SerializedName("dt")
        private long date;

        @SerializedName("pressure")
        private double pressure;

        @SerializedName("humidity")
        private int humidity;

        @SerializedName("weather")
        private List<Weather> weather;

        @SerializedName("temp")
        private Temp temp;

        @SerializedName("speed")
        private double speed;

        public class Temp{

            public Temp(){}

            @SerializedName("day")
            private double tempDay;

            @SerializedName("night")
            private double tempNight;

            @SerializedName("eve")
            private double tempEve;

            @SerializedName("morn")
            private double tempMorn;

            public double getTempDay(){
                return tempDay;
            }

            public double getTempNight(){
                return tempNight;
            }

            public double getTempEve(){
                return tempEve;
            }

            public double getTempMorn(){
                return tempMorn;
            }

        }

        public class Weather{

            public Weather(){}

            @SerializedName("main")
            private String main;

            public String getMain(){
                return main;
            }
        }

        public double getSpeed(){
            return speed;
        }

        public double getPressure(){
            return pressure;
        }

        public int getHumidity(){
            return humidity;
        }

        public List<Weather> getWeather(){
            return weather;
        }

        public Temp getTemp(){
            return temp;
        }

        public long getDate(){
            return date;
        }
    }

    public City getCity(){
        return city;
    }

    public List<WeatherList> getWeather(){
        return weatherList;
    }
}
