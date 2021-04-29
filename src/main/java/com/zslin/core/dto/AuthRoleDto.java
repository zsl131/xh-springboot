package com.zslin.core.dto;

import com.zslin.core.model.AdminRole;
import lombok.Data;

import java.util.List;

/**
 * Created by zsl on 2018/7/15.
 */
@Data
public class AuthRoleDto {

    private List<Integer> authIds;

    private List<AdminRole> roleList;

    public AuthRoleDto() {
    }

    public AuthRoleDto(List<Integer> authIds, List<AdminRole> roleList) {
        this.authIds = authIds;
        this.roleList = roleList;
    }
}
