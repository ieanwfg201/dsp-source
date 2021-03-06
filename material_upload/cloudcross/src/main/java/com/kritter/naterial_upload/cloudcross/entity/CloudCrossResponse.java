package com.kritter.naterial_upload.cloudcross.entity;

/**
 * Created by hamlin on 16-7-31.
 */
public class CloudCrossResponse {
    private Integer status;
    private Success success;
    private CloudCrossError error;

    public CloudCrossError getError() {
        return error;
    }

    public void setError(CloudCrossError error) {
        this.error = error;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    public class Success {
        private Integer index;
        private Integer code;
        private String message;
        private Integer bannerId;
        private String field;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getBannerId() {
            return bannerId;
        }

        public void setBannerId(Integer bannerId) {
            this.bannerId = bannerId;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
