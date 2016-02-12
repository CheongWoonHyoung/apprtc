package org.appspot.apprtc;

import org.json.JSONObject;

/**
 *  SHJSONParser
 *  isho <isho@unist.ac.kr>
 *  2015. 11. 13..
 */

public interface SHJSONParserCallback {
    void onResult(JSONObject result, int parserTag);
    void exceptionOccured(Exception e);
    void cancelled();
}
