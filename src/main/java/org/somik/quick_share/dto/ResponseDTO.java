package org.somik.quick_share.dto;

public class ResponseDTO {
    private String status;
    private Object content;
    private String error;

    public ResponseDTO(String status) {
        this(status, "", "");
    }

    public ResponseDTO(String status, Object content) {
        this(status, content, "");
    }

    public ResponseDTO(String status, Object content, String error) {
        this.status = status;
        this.content = content;
        this.error = error;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return String.format("{ status='%s', content='%s', error='%s'}", getStatus(), getContent(), getError());
    }

}
