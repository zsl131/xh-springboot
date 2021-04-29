package com.zslin.core.controller.dto;

import lombok.Data;

/**
 * 文件上传参数
 *  - 文件上传时的拓展参数
 */
@Data
public class UploadParam {

    /** 上传目标，即上传到服务器本地，还是七牛，1-服务器；2-七牛 */
    private String target = "2";

    /** 虚拟路径，只有target为1时才有用 */
    private String path = "/publicFile/upload";

    /** 图片宽度 */
    private Integer width = 800;

    /** 图片高度 */
    private Integer height = 800;

    /** 临时票据 */
    private String ticket;

    /** 归属对象类型 */
    private String objClassName;

    /** 归属对象ID */
    private Integer objId=0;

    /** 排序序号 */
    private Integer orderNo = 0;

    /** 是否是封面图片 */
    private String isFirst = "0";

    /** 是否是编辑器 */
    private boolean isEditor = false;
}
