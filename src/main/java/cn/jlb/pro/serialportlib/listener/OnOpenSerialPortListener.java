package cn.jlb.pro.serialportlib.listener;

import java.io.File;

/**
 * Created by jinbiao.wu on 2019/01/14.
 * 打开串口监听
 */

public interface OnOpenSerialPortListener {

    void onSuccess(File device);

    void onFail(File device, Status status);

    enum Status {
        NO_READ_WRITE_PERMISSION,
        OPEN_FAIL
    }
}
