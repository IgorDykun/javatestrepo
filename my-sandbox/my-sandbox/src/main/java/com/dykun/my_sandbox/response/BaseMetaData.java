package com.dykun.my_sandbox.response;

public class BaseMetaData {
    private boolean success;
    private int code;
    private String errorMessage;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}