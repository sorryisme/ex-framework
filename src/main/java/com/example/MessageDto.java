package com.example;


public class MessageDto {

    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toStringify() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append("\"message\": \"").append(message).append("\"");
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "message='" + message + '\'' +
                '}';
    }
}
