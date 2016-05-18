package com.grannyos.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * In this file initialize constance value
 */
public class Const {
    public static final HashMap<String, String> MOMENT_TYPES;
    public static final ArrayList<String> MOMENT_LIST;
    public static final String[] ACTIONS;
    static {
        MOMENT_TYPES = new HashMap<>(9);
        MOMENT_TYPES.put("AddActivity","https://developers.google.com/+/plugins/snippet/examples/thing");
        MOMENT_TYPES.put("BuyActivity","https://developers.google.com/+/plugins/snippet/examples/a-book");
        MOMENT_TYPES.put("CheckInActivity","https://developers.google.com/+/plugins/snippet/examples/place");
        MOMENT_TYPES.put("CommentActivity","https://developers.google.com/+/plugins/snippet/examples/blog-entry");
        MOMENT_TYPES.put("CreateActivity","https://developers.google.com/+/plugins/snippet/examples/photo");
        MOMENT_TYPES.put("ListenActivity","https://developers.google.com/+/plugins/snippet/examples/song");
        MOMENT_TYPES.put("ReserveActivity","https://developers.google.com/+/plugins/snippet/examples/restaurant");
        MOMENT_TYPES.put("ReviewActivity","https://developers.google.com/+/plugins/snippet/examples/widget");
        MOMENT_LIST = new ArrayList<>(Const.MOMENT_TYPES.keySet());
        Collections.sort(MOMENT_LIST);
        ACTIONS = MOMENT_TYPES.keySet().toArray(new String[0]);
        int count = ACTIONS.length;
        for (int i = 0; i < count; i++) {
            ACTIONS[i] = "http://schemas.google.com/" + ACTIONS[i];
        }
    }
}
