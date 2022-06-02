package com.sobot.chat.utils;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class SobotSerializableMap implements Serializable {
    private LinkedHashMap map;

    public LinkedHashMap getMap() {
        return map;
    }

    public void setMap(LinkedHashMap map) {
        this.map = map;
    }
}
