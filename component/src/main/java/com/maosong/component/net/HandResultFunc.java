package com.maosong.component.net;
import io.reactivex.functions.Function;

/**
 * Created by tianweiping on 2018/1/30.
 */

public class HandResultFunc<T> implements Function<BaseResultBean<T>, T> {
    @Override
    public T apply(BaseResultBean<T> t) throws Exception {
        if (t.getCode() != NetConstant.RESULT_CODE_SUCCESS) {
            throw new ApiException(t.getCode(), t.getMsg());
        }
        return t.getData();
    }
}
