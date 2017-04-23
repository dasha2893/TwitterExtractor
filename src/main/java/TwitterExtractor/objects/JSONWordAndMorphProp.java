package TwitterExtractor.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class JSONWordAndMorphProp {
    private String text;
    private ArrayList<JSONMorphProperties> JSONMorphProperties;

    @JsonCreator
    public JSONWordAndMorphProp(@JsonProperty("text") String text, @JsonProperty("analysis") ArrayList<JSONMorphProperties> JSONMorphProperties) {
        this.text = text;
        this.JSONMorphProperties = JSONMorphProperties;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<JSONMorphProperties> getJSONMorphProperties() {
        return JSONMorphProperties;
    }

    public void setJSONMorphProperties(ArrayList<JSONMorphProperties> JSONMorphProperties) {
        this.JSONMorphProperties = JSONMorphProperties;
    }
}
