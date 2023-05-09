package com.voocoo.pet.base;

/**
 * Created by chenjiahui on 2018/3/25.
 */

public interface MvpBaseUsecase<T> {
    MvpBaseUsecase execute(T... params);

    void stop();

    void cancel();
}

