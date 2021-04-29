package com.zslin.core.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsl on 2019/9/24.
 */
@Data
public class UploadResult {

    private Integer errno = 0;
    private List<Object> data;

    public UploadResult(){
        data = new ArrayList<>();
    }

    public UploadResult(Integer errno) {
        this();
        this.errno = errno;
    }

    public UploadResult(Integer errno, String imagePath) {
        this(errno);
        this.errno = errno;
        this.data.add(imagePath);
    }

    public UploadResult add(String imagePath) {
        this.data.add(imagePath);
        return this;
    }

    public UploadResult add(Integer id, String imagePath) {
        this.data.add(new UploadFileDto(id, imagePath));
        return this;
    }
}
