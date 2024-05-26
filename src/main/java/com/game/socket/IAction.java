package com.game.socket;

import org.json.JSONObject;

public interface IAction {
    void call(JSONObject object);
}
