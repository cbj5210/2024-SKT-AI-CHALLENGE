package com.skt.help.model;

import java.util.List;

public class ChatGptResponse {
    private Boolean isEmergency;
    private Boolean isLocationTracking;
    private String emergencyDetail;
    private String target;
    private List<String> contextTo;
    private String context;

    public ChatGptResponse() {

    }

    public ChatGptResponse(boolean isEmergency, boolean isLocationTracking, String emergencyDetail, String target, List<String> contextTo, String context) {
        this.isEmergency = isEmergency;
        this.isLocationTracking = isLocationTracking;
        this.emergencyDetail = emergencyDetail;
        this.target = target;
        this.contextTo = contextTo;
        this.context = context;
    }

    public boolean getIsEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public boolean getIsLocationTracking() {
        return isLocationTracking;
    }

    public void setIsLocationTracking(boolean locationTracking) {
        isLocationTracking = locationTracking;
    }

    public String getEmergencyDetail() {
        return emergencyDetail;
    }

    public void setEmergencyDetail(String emergencyDetail) {
        this.emergencyDetail = emergencyDetail;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<String> getContextTo() {
        return contextTo;
    }

    public void setContextTo(List<String> contextTo) {
        this.contextTo = contextTo;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
