package com.kritter.ex_int.utils.picker;

import com.kritter.entity.reqres.entity.ResponseAdInfo;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by oneal on 2017/3/1.
 */
public interface AdPicker {

    public ResponseAdInfo pick(Set<ResponseAdInfo> responseAdInfoList,
                               Random randomPicker);
}
