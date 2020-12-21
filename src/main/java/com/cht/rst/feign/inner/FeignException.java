package com.cht.rst.feign.inner;


import static com.cht.rst.feign.inner.Util.UTF_8;

public class FeignException extends RuntimeException {

    private static final long serialVersionUID = 0;
    private int status;
    private byte[] content;

    protected FeignException(String message, Throwable cause) {
        super(message, cause);
    }

    protected FeignException(String message, Throwable cause, byte[] content) {
        super(message, cause);
        this.content = content;
    }

    protected FeignException(String message) {
        super(message);
    }

    protected FeignException(int status, String message, byte[] content) {
        super(message);
        this.status = status;
        this.content = content;
    }

    public int status() {
        return this.status;
    }

    public byte[] content() {
        return this.content;
    }

    public String contentUTF8() {
        return new String(content, UTF_8);
    }
}
