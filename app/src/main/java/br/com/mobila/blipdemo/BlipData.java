
package br.com.mobila.blipdemo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class BlipData {

    @SerializedName("DisplayName")
    private String mDisplayName;
    @SerializedName("ID")
    private String mID;
    @SerializedName("MatchTypes")
    private List<String> mMatchTypes;
    @SerializedName("Name")
    private String mName;
    @SerializedName("PassParams")
    private String mPassParams;
    @SerializedName("Score")
    private Double mScore;

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getID() {
        return mID;
    }

    public void setID(String iD) {
        mID = iD;
    }

    public List<String> getMatchTypes() {
        return mMatchTypes;
    }

    public void setMatchTypes(List<String> matchTypes) {
        mMatchTypes = matchTypes;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPassParams() {
        return mPassParams;
    }

    public void setPassParams(String passParams) {
        mPassParams = passParams;
    }

    public Double getScore() {
        return mScore;
    }

    public void setScore(Double score) {
        mScore = score;
    }

}
