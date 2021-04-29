package com.zslin.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zslin.core.api.ExplainResult;
import com.zslin.core.api.ExplainResultField;
import com.zslin.core.exception.BusinessException;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zsl on 2018/7/3.
 */
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@Data
@ExplainResult(name = "API请求返回结果对象", notes = "所有请求返回结果都是一样的格式")
public class JsonResult {

    public static final String SUCCESS_REASON = "请求成功";
    public static final String SUCCESS_CODE = "0";
    public static final String BUSINESS_ERR_CODE = BusinessException.Code.DEFAULT_ERR_CODE; //业务错误代码

    @ExplainResultField(name = "返回的结果信息", type = "String", notes = "结果信息")
    private String reason = SUCCESS_REASON;

    /**
     * 错误代码
     * 0-成功返回
     */
    @ExplainResultField(name = "错误代码", type = "String", notes="0-没有错误")
    private String errCode = SUCCESS_CODE;

   /* @ApiModelProperty(value = "登陆标记，0-不需要登陆，1-需要登陆")
    private String needLogin = "0";*/

    @ExplainResultField(name = "请求结果数据集合", type = "Object", notes = "这是一个数据集合，不同的数据接口返回的数据格式可能不同")
    private Map<String, Object> result;

    /**
     * 标记
     * 1-请求无异常，数据也正常
     * 0-请求无异常，数据不正常
     */
    /*@ApiModelProperty(value = "结果标记，1-请求和结果数据无异常；0-请求无异常，数据结果有异常")
    private String flag = "1";*/

    @Override
    public String toString() {
        return "JsonResult{" +
                "reason='" + reason + '\'' +
                ", errCode='" + errCode + '\'' +
                ", result=" + result +
                '}';
    }

    public static JsonResult getInstance() {
        return new JsonResult();
    }

    public static JsonResult getInstance(String sucMsg) {
        return new JsonResult(sucMsg);
    }

    private JsonResult(String sucMsg) {
        this.reason = SUCCESS_REASON;
        this.errCode = SUCCESS_CODE;
        this.result = new HashMap<>();
        this.result.put("message", sucMsg);
    }

    public static JsonResult error(String errMsg) {
        JsonResult that = getInstance().fail(errMsg);
        return that;
    }

    public static JsonResult error(String errCode, String errMsg) {
        return getInstance().fail(errCode, errMsg);
    }

    public static JsonResult success() {
        return success(SUCCESS_REASON);
    }

    public static JsonResult success(String sucMsg) {
        JsonResult that = getInstance(sucMsg);
        return that;
    }

    public static JsonResult succ(Object obj) {
        JsonResult that = getInstance().set("obj", obj);
        return that;
    }

    public JsonResult failFlag(String msg) {
        this.reason = SUCCESS_REASON;
        this.errCode = SUCCESS_CODE;
        if(this.result==null) {
            this.result = new HashMap<>();
        }
        this.result.put("message", msg);
//        this.flag = "0";
        this.result.put("flag", "0");
        return this;
    }

    public JsonResult failFlag(String errCode, String msg, Object obj) {
        this.reason = msg;
        this.errCode = errCode;
        if(this.result==null) {this.result = new HashMap<>();}
        this.result.put("message", msg);
        this.result.put("errors", obj);
        return this;
    }

    public JsonResult failLogin(String msg) {
        this.reason = SUCCESS_REASON;
        this.errCode = SUCCESS_CODE;
        if(this.result==null) {
            this.result = new HashMap<>();
        }
        this.result.put("message", msg);
//        this.needLogin = "1";
        this.result.put("needLogin", "1");
        return this;
    }

    public JsonResult ok(String result) {
        this.reason = SUCCESS_REASON;
        this.errCode = SUCCESS_CODE;
        this.result.put("message", result);
        return this;
    }

    public JsonResult fail(String errCode, String errMsg) {
        this.reason = errMsg;
        this.errCode = errCode;
        this.result.put("message", errMsg);
        return this;
    }

    public JsonResult fail(String errMsg) {
        return fail(BUSINESS_ERR_CODE, errMsg);
    }

    public JsonResult set(String key, Object data) {
        result.put(key, data);
        return this;
    }

    private JsonResult(){
        result = new HashMap<>();
    }

}
