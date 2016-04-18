package com.binarycraft.acland.entity;

/**
 * Created by user pc on 4/19/2016.
 */
public class UpdateUnionMouzaResponse {

    private boolean isUnionUpdated;
    private boolean isMouzaUpdated;
    private UnionMouzaResponse data;
    private String message;

    public boolean isUnionUpdated() {
        return isUnionUpdated;
    }

    public void setIsUnionUpdated(boolean isUnionUpdated) {
        this.isUnionUpdated = isUnionUpdated;
    }

    public boolean isMouzaUpdated() {
        return isMouzaUpdated;
    }

    public void setIsMouzaUpdated(boolean isMouzaUpdated) {
        this.isMouzaUpdated = isMouzaUpdated;
    }

    public UnionMouzaResponse getData() {
        return data;
    }

    public void setData(UnionMouzaResponse data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
