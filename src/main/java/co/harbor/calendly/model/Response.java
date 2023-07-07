package co.harbor.calendly.model;

import lombok.Getter;

/**
 * This is a POJO for generic response message with status and a message.
 */
@Getter
public class Response {
    private String status;
    private String message;
    public Response(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
