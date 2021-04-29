package com.zslin.business.dto;

import lombok.Data;

/**
 * 封面图片DTO对象
 */
@Data
public class CoverDto {

    private String picUrl;

    private String videoUrl;

    public CoverDto(String picUrl, String videoUrl) {
        this.picUrl = picUrl;
        this.videoUrl = videoUrl;
    }
}
