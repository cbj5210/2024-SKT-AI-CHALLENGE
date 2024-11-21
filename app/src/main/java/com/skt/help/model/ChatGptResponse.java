package com.skt.help.model;

import java.util.List;

public class ChatGptResponse {
    private boolean isEmergency;
    private boolean isLocationTracking;
    private String emergencyDetail;
    private String target;
    private String message;
    private List<String> contextTo;
    private String context;

    public ChatGptResponse() {

    }

    public ChatGptResponse(boolean isEmergency, boolean isLocationTracking, String emergencyDetail, String target, String message, List<String> contextTo, String context) {
        this.isEmergency = isEmergency;
        this.isLocationTracking = isLocationTracking;
        this.emergencyDetail = emergencyDetail;
        this.target = target;
        this.message = message;
        this.contextTo = contextTo;
        this.context = context;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public boolean isLocationTracking() {
        return isLocationTracking;
    }

    public void setLocationTracking(boolean locationTracking) {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
