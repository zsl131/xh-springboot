package com.zslin.business.mini.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PushMsgRabbitDto implements Serializable {

    private String tempSn;

    private String toUser;

    private String page;

    public List<String> content;
}
