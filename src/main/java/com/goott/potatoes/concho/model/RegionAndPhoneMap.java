package com.goott.potatoes.concho.model;

import java.util.HashMap;

public final class RegionAndPhoneMap {
    private final HashMap<String, String> regionToPhoneMap;
    private final HashMap<String, String> phoneToRegionMap;

    // Constructor to initialize the maps
    public RegionAndPhoneMap() {
        regionToPhoneMap = new HashMap<>();
        phoneToRegionMap = new HashMap<>();
        initializeMaps();
    }

    // Method to initialize the maps with region names and their phone codes
    private void initializeMaps() {
        // Adding region to phone map entries
        regionToPhoneMap.put("서울특별시", "02");
        regionToPhoneMap.put("인천광역시", "032");
        regionToPhoneMap.put("대전광역시", "042");
        regionToPhoneMap.put("부산광역시", "051");
        regionToPhoneMap.put("울산광역시", "052");
        regionToPhoneMap.put("대구광역시", "053");
        regionToPhoneMap.put("광주광역시", "062");
        regionToPhoneMap.put("제주특별자치도", "064");
        regionToPhoneMap.put("경기도", "031");
        regionToPhoneMap.put("강원도", "033");
        regionToPhoneMap.put("충청남도", "041");
        regionToPhoneMap.put("충청북도", "043");
        regionToPhoneMap.put("경상북도", "054");
        regionToPhoneMap.put("경상남도", "055");
        regionToPhoneMap.put("전라남도", "061");
        regionToPhoneMap.put("전라북도", "063");

        // Adding phone to region map entries
        for (String region : regionToPhoneMap.keySet()) {
            phoneToRegionMap.put(regionToPhoneMap.get(region), region);
        }
    }

    public String getPhoneCode(String region) {
        return regionToPhoneMap.get(region);
    }

    public String getRegion(String phoneCode) {
        return phoneToRegionMap.get(phoneCode);
    }

}


