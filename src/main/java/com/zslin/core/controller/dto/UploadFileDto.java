package com.zslin.core.controller.dto;

import lombok.Data;

/**
 * 上传文件DTO对象
 */
@Data
public class UploadFileDto {

    private Integer id;

    private String url;

    public UploadFileDto() {}

    public UploadFileDto(Integer id, String url) {
        this.id = id;
        this.url = url;
    }
}
