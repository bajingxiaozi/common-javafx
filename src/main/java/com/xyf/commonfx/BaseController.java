package com.xyf.commonfx;

import com.xyf.common.annotation.UiThread;
import com.xyf.common.util.Lg;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController extends BaseRxLifeCircle implements Initializable {

    private static final String TAG = BaseController.class.getSimpleName();

    @UiThread
    public void onStop() {
        Lg.d(TAG, this);
        clearDisposable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Lg.d(TAG, this);
    }

}
