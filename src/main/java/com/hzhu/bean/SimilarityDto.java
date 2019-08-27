package com.hzhu.bean;

import java.util.Objects;

/**
 * @Description 比较对象
 * @Date 2019/8/27 14:55
 * @Created by CZB
 */
public class SimilarityDto {

    /**
     * 名称
     */
    private String text;

    /**
     * 相似度
     */
    private Double ratio;

    public SimilarityDto(String text, Double ratio) {
        this.text = text;
        this.ratio = ratio;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    @Override
    public String toString() {
        return "SimilarityDto{" +
                "text='" + text + '\'' +
                ", ratio=" + ratio +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimilarityDto that = (SimilarityDto) o;
        return Objects.equals(text, that.text) &&
                Objects.equals(ratio, that.ratio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, ratio);
    }
}
