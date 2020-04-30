
package com.hdarha.happ.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CardSet {

    @SerializedName("set_name")
    @Expose
    private String setName;
    @SerializedName("set_code")
    @Expose
    private String setCode;
    @SerializedName("set_rarity")
    @Expose
    private String setRarity;
    @SerializedName("set_rarity_code")
    @Expose
    private String setRarityCode;
    @SerializedName("set_price")
    @Expose
    private String setPrice;

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public String getSetRarity() {
        return setRarity;
    }

    public void setSetRarity(String setRarity) {
        this.setRarity = setRarity;
    }

    public String getSetRarityCode() {
        return setRarityCode;
    }

    public void setSetRarityCode(String setRarityCode) {
        this.setRarityCode = setRarityCode;
    }

    public String getSetPrice() {
        return setPrice;
    }

    public void setSetPrice(String setPrice) {
        this.setPrice = setPrice;
    }

}
