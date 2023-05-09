package com.voocoo.pet.common.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Constructor;

/**
 * Presenter通过反射实例化工具类
 */

public class PresenterBuilder {
    public static final String PRESENTER_CLASS = "presenter_class";

    private Class<?> presenterClass;
    private Constructor<?> constructor;

    public static Intent attachPresenterToIntent(Intent intent, Class<?> presenterClass){
        intent.putExtra(PRESENTER_CLASS, presenterClass);
        return intent;
    }

    /**
     * 用于创建fragment之后，为fragment的argument附加所需的presenter class
     * @param args
     * @param presenterClass
     * @return
     */
    public static Bundle attachPresenterToArgument(Bundle args, Class<?> presenterClass){
        args.putSerializable(PRESENTER_CLASS, presenterClass);
        return args;
    }

    public static PresenterBuilder create(Intent intent, Class<?>... paramClasses){
        return create(intent.getExtras(), paramClasses);
    }

    public static PresenterBuilder create(Bundle args, Class<?>... paramClasses){
        PresenterBuilder builder = new PresenterBuilder(args);
        builder.addConstructorClass(paramClasses);
        return builder;
    }

    private PresenterBuilder(Bundle args){
        if(args == null){
            throw new RuntimeException("bundle is null, how to find a presenter class?");
        }
        presenterClass = (Class<?>) args.getSerializable(PRESENTER_CLASS);
        if(presenterClass == null){
            Log.e(getClass().toString(), "Get presenter class from bundle fail.");
            throw new RuntimeException("Get presenter class from bundle fail.");
        }
    }

    public PresenterBuilder addConstructorClass(Class<?>... paramClasses){
        try {
            constructor = presenterClass.getDeclaredConstructor(paramClasses);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public <T> T build(Object... params){
        try {
            if(constructor == null){
                constructor = presenterClass.getDeclaredConstructor();
            }
            return (T) constructor.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
