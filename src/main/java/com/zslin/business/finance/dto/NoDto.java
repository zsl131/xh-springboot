package com.zslin.business.finance.dto;

/**
 * Created by zsl on 2019/1/11.
 */
public class NoDto {

    private Integer tno;

    private String no;

    public NoDto(Integer tno, String no) {
        this.tno = tno;
        this.no = no;
    }

    public Integer getTno() {
        return tno;
    }

    public void setTno(Integer tno) {
        this.tno = tno;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
