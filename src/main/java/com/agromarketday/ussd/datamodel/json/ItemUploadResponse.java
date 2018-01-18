package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemUploadResponse {

    /*
{
  "success": true,
  "data": {
    "status": "SUCCESSFUL",
    "description": "Uploaded successfuly"
  }
}
     */
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private Data data;

    private ItemUploadResponse(boolean success) {
        this.success = success;
    }

    public ItemUploadResponse() {
        this(Boolean.TRUE);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public class Data {

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("description")
        @Expose
        private String description;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
