package com.zslin.business.finance.dto;

/**
 * Created by zsl on 2019/1/8.
 */
public class FinanceCountDto {

    private String name;

    private Double total;

    @Override
    public String toString() {
        return "FinanceCountDto{" +
                "name='" + name + '\'' +
                ", total=" + total +
                '}';
    }

    public FinanceCountDto(String name, Double total) {
        this.name = name;
        this.total = total;
    }

    public FinanceCountDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
