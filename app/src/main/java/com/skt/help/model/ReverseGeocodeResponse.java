package com.skt.help.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReverseGeocodeResponse {

    @SerializedName("results")
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        @SerializedName("region")
        private Region region;

        public Region getRegion() {
            return region;
        }

        public static class Region {
            @SerializedName("area1")
            private Area area1;

            @SerializedName("area2")
            private Area area2;

            @SerializedName("area3")
            private Area area3;

            public Area getArea1() { return area1; }
            public Area getArea2() { return area2; }
            public Area getArea3() { return area3; }

            public static class Area {
                @SerializedName("name")
                private String name;

                public String getName() {
                    return name;
                }
            }
        }
    }
}