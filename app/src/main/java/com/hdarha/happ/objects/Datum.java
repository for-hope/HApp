
package com.hdarha.happ.objects;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("atk")
    @Expose
    private Integer atk;
    @SerializedName("def")
    @Expose
    private Integer def;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("race")
    @Expose
    private String race;
    @SerializedName("attribute")
    @Expose
    private String attribute;
    @SerializedName("card_sets")
    @Expose
    private List<CardSet> cardSets = null;
    @SerializedName("card_images")
    @Expose
    private List<CardImage> cardImages = null;
    @SerializedName("card_prices")
    @Expose
    private List<CardPrice> cardPrices = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getAtk() {
        return atk;
    }

    public void setAtk(Integer atk) {
        this.atk = atk;
    }

    public Integer getDef() {
        return def;
    }

    public void setDef(Integer def) {
        this.def = def;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public List<CardSet> getCardSets() {
        return cardSets;
    }

    public void setCardSets(List<CardSet> cardSets) {
        this.cardSets = cardSets;
    }

    public List<CardImage> getCardImages() {
        return cardImages;
    }

    public void setCardImages(List<CardImage> cardImages) {
        this.cardImages = cardImages;
    }

    public List<CardPrice> getCardPrices() {
        return cardPrices;
    }

    public void setCardPrices(List<CardPrice> cardPrices) {
        this.cardPrices = cardPrices;
    }

}
