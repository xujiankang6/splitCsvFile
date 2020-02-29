package com.jiankang.splitfile.bean;

import org.springframework.context.annotation.Bean;

public class Model {
    private String id;
    private  String name;
    private String unit;
    private float number;
    private float onePrice;
    private double sumPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public float getOnePrice() {
        return onePrice;
    }

    public void setOnePrice(float onePrice) {
        this.onePrice = onePrice;
    }

    public double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(double sumPrice) {
        this.sumPrice = sumPrice;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", number=" + number +
                ", onePrice=" + onePrice +
                ", sumPrice=" + sumPrice +
                '}';
    }
}
