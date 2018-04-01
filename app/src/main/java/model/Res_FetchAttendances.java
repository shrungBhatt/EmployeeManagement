package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Res_FetchAttendances extends BaseModel {

    @SerializedName("list")
    @Expose
    private ArrayList<List> list = null;

    public ArrayList<List> getList() {
        return list;
    }

    public void setList(ArrayList<List> list) {
        this.list = list;
    }

    public class List {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("photo_url")
        @Expose
        private String photoUrl;
        @SerializedName("presence")
        @Expose
        private String presence;
        @SerializedName("is_submitted")
        @Expose
        private String isSubmitted;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getPresence() {
            return presence;
        }

        public void setPresence(String presence) {
            this.presence = presence;
        }

        public String getIsSubmitted() {
            return isSubmitted;
        }

        public void setIsSubmitted(String isSubmitted) {
            this.isSubmitted = isSubmitted;
        }

    }
}
