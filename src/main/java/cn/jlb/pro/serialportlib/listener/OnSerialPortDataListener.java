package cn.jlb.pro.serialportlib.listener;

/**
 * Created by jinbiao.wu on 2019/01/14.
 * 串口消息监听
 */

public interface OnSerialPortDataListener {

    /**
     * 数据接收
     *
     * @param bytes 接收到的数据
     */
    void onDataReceived(String[] bytes, int type);

    /**
     * 数据发送
     *
     * @param bytes 发送的数据
     */
    void onDataSent(byte[] bytes);
}
