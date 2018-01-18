package com.agromarketday.ussd.datamodel.json;

/**
 *
 * @author smallgod
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckAccountResponse {

    /*
    
        {
            "success": true,
            "data": {
              "user_id": "774983602", 
              "is_exist":true,
              "account_status": "REGISTERED", 
              "name": "Musa Ozeki", 
              "district": "Kampala",
              "description": "Account active!"
            }
         }
    
     */
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private Data data;

    private CheckAccountResponse(boolean success) {
        this.success = success;
    }

    public CheckAccountResponse() {
        this(Boolean.TRUE);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("user_id")
        @Expose
        private String userId;

        @SerializedName("is_exist")
        @Expose
        private boolean isExist;

        @SerializedName("account_status")
        @Expose
        private String accountStatus;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("district")
        @Expose
        private String district;

        @SerializedName("description")
        @Expose
        private String description;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isIsExist() {
            return isExist;
        }

        public void setIsExist(boolean isExist) {
            this.isExist = isExist;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
    }
}
