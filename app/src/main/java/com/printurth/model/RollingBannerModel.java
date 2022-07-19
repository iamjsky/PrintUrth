package com.printurth.model;


import lombok.Data;

@Data
public class RollingBannerModel {

    public RollingBannerModel(String id, String fileName, String title, String thumbImgName, String makerName, String uploadAt, String level, String time, String filament, String description, int isfin, int like, String lastdate) {
        this.id = id;
        this.fileName = fileName;
        this.title = title;
        this.thumbImgName = thumbImgName;
        this.makerName = makerName;
        this.uploadAt = uploadAt;
        this.level = level;
        this.time = time;
        this.filament = filament;
        this.description = description;
        this.isfin = isfin;
        this.like = like;
        this.lastdate = lastdate;
    }

    public RollingBannerModel(String id, int isfin, int like) {
        this.id = id;
        this.isfin = isfin;
        this.like = like;
    }
    public RollingBannerModel(String id, String lastdate) {
        this.id = id;
        this.lastdate = lastdate;
    }

    public String id;
    public String fileName;
    public String title;
    public String thumbImgName;
    public String makerName;
    public String uploadAt;
    public String level;
    public String time;
    public String filament;
    public String description;
    public int isfin;
    public int like;
    public String lastdate;

}
