package com.example.kt.shudaapp.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShaheedPakageModel {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("val")
    @Expose
    private List<Val> val = null;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public List<Val> getVal() {
        return val;
    }

    public void setVal(List<Val> val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "ShaheedPakageModel{" +
                "success=" + success +
                ", val=" + val +
                '}';
    }


    public class Val {

        @SerializedName("sname")
        @Expose
        private String sname;
        @SerializedName("relations")
        @Expose
        private String relations;
        @SerializedName("package_name")
        @Expose
        private String packageName;
        @SerializedName("package_details")
        @Expose
        private String packageDetails;
        @SerializedName("package_status")
        @Expose
        private String packageStatus;

        public String getSname() {
            return sname;
        }

        public void setSname(String sname) {
            this.sname = sname;
        }

        public String getRelations() {
            return relations;
        }

        public void setRelations(String relations) {
            this.relations = relations;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getPackageDetails() {
            return packageDetails;
        }

        public void setPackageDetails(String packageDetails) {
            this.packageDetails = packageDetails;
        }

        public String getPackageStatus() {
            return packageStatus;
        }

        public void setPackageStatus(String packageStatus) {
            this.packageStatus = packageStatus;
        }

        @Override
        public String toString() {
            return "Val{" +
                    "sname='" + sname + '\'' +
                    ", relations='" + relations + '\'' +
                    ", packageName='" + packageName + '\'' +
                    ", packageDetails='" + packageDetails + '\'' +
                    ", packageStatus='" + packageStatus + '\'' +
                    '}';
        }
    }
}
