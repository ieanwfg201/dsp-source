package com.kritter.device;

import com.kritter.device.entity.HandsetMasterData;

public interface HandsetDetectionProvider {
    public HandsetMasterData detectHandsetForUserAgent(String userAgent) throws Exception;
}