package com.skt.help.model;

public class TelegramResponse {
    private boolean ok;
    private Result result;

    public boolean isOk() {
        return ok;
    }

    public Result getResult() {
        return result;
    }

    public static class Result {
        private int message_id;
        private String text;

        public int getMessageId() {
            return message_id;
        }

        public String getText() {
            return text;
        }
    }
}
