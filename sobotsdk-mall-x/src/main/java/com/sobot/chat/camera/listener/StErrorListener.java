package com.sobot.chat.camera.listener;

public interface StErrorListener {
    void onError();
    void AudioPermissionError();
    boolean checkAutoPremission();
    boolean checkCameraPremission();
}
