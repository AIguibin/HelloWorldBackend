package com.aiguibin.business.entity;
/**
 * 
 *
 * @author AIguibin
 * Date time 2019年04月30日 22:21:37
 */
public class FruitEntity {
    private String fruitId;
    private String fruitName;
    private String production;
    private String appearance;

    public String getFruitId() {
        return fruitId;
    }

    public void setFruitId(String fruitId) {
        this.fruitId = fruitId;
    }

    public String getFruitName() {
        return fruitName;
    }

    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getAppearance() {
        return appearance;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }
}
