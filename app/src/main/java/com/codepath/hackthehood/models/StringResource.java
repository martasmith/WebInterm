package com.codepath.hackthehood.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by thomasharte on 12/10/2014.
 */
@ParseClassName("StringResource")
public class StringResource extends ParseObject {

    public StringResource() {}

    /*
        Exposed properties:

            String text
     */

    private final String textKey = "text";
    public void setText(String text) {
        put(textKey, text);
    }
    public String getText() {
        return getString(textKey);
    }
}
