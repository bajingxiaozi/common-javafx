package com.xyf.common;

import com.xyf.common.util.Lg;
import javafx.application.Application;
import javafx.stage.Stage;

public abstract class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void init() throws Exception {
        super.init();
        Lg.d(TAG, this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Lg.d(TAG, this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Lg.d(TAG, this);
    }

}
