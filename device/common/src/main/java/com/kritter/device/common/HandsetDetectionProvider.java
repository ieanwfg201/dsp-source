package com.kritter.device.common;

import com.kritter.device.common.entity.HandsetMasterData;

public interface HandsetDetectionProvider {
    public HandsetMasterData detectHandsetForUserAgent(String userAgent) throws Exception;
}
