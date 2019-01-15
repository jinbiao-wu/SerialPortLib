package cn.jlb.pro.serialportlib.thread;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ivsign.android.IDCReader.IDCReaderSDK;
import cn.jlb.pro.serialportlib.util.NationDeal;

/**
 * Created by jinbiao.wu on 2018/01/16.
 * 串口消息读取线程
 */

public abstract class SerialPortReadThread extends Thread {

    public abstract void onDataReceived(String[] bytes, int size);

    byte[] cmd_find  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22  };
    byte[] cmd_selt  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21  };
    byte[] cmd_read  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x30, 0x01, 0x32 };

    private static final String TAG = SerialPortReadThread.class.getSimpleName();
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    byte[] recData = new byte[1500];
    String[] decodeInfo = new String[10];
    boolean isRun = false;//线程中断标志

    public SerialPortReadThread(InputStream inputStream, OutputStream outputStream) {
        mInputStream = inputStream;
        mOutputStream = outputStream;
    }

    @Override
    public void run() {
        super.run();

        while (isRun) {
            try {
                Thread.sleep(1000);
                if (null == mInputStream && null == mOutputStream) {
                    onDataReceived(null, -2);// 连接异常
                    return;
                }
                mOutputStream.write(cmd_find);
                Thread.sleep(200);
                int datalen = mInputStream.read(recData);
//                Log.d("wjb", "recData:" + Arrays.toString(recData));
                if (recData[9] == -97) {
                    mOutputStream.write(cmd_selt);
                    Thread.sleep(200);
                    datalen = mInputStream.read(recData);
                    if (recData[9] == -112) {
                        mOutputStream.write(cmd_read);
                        Thread.sleep(1000);
                        byte[] tempData = new byte[1500];
                        if (mInputStream.available() > 0) {
                            datalen = mInputStream.read(tempData);
                        } else {
                            Thread.sleep(500);
                            if (mInputStream.available() > 0) {
                                datalen = mInputStream.read(tempData);
                            }
                        }
                        int flag = 0;
                        if (datalen < 1294) {
                            for (int i = 0; i < datalen; i++, flag++) {
                                recData[flag] = tempData[i];
                            }
                            Thread.sleep(1000);
                            if (mInputStream.available() > 0) {
                                datalen = mInputStream.read(tempData);
                            } else {
                                Thread.sleep(500);
                                if (mInputStream.available() > 0) {
                                    datalen = mInputStream.read(tempData);
                                }
                            }
                            for (int i = 0; i < datalen; i++, flag++) {
                                recData[flag] = tempData[i];
                            }

                        } else {
                            for (int i = 0; i < datalen; i++, flag++) {
                                recData[flag] = tempData[i];
                            }
                        }
                        tempData = null;
                        if (flag == 1295) {
                            if (recData[9] == -112) {

                                byte[] dataBuf = new byte[256];
                                for (int i = 0; i < 256; i++) {
                                    dataBuf[i] = recData[14 + i];
                                }
                                String TmpStr = new String(dataBuf, "UTF16-LE");
                                TmpStr = new String(TmpStr.getBytes("UTF-8"));
                                decodeInfo[0] = TmpStr.substring(0, 15);
                                decodeInfo[1] = TmpStr.substring(15, 16);
                                decodeInfo[2] = TmpStr.substring(16, 18);
                                decodeInfo[3] = TmpStr.substring(18, 26);
                                decodeInfo[4] = TmpStr.substring(26, 61);
                                decodeInfo[5] = TmpStr.substring(61, 79);
                                decodeInfo[6] = TmpStr.substring(79, 94);
                                decodeInfo[7] = TmpStr.substring(94, 102);
                                decodeInfo[8] = TmpStr.substring(102, 110);
                                decodeInfo[9] = TmpStr.substring(110, 128);
                                if (decodeInfo[1].equals("1"))
                                    decodeInfo[1] = "男";
                                else
                                    decodeInfo[1] = "女";
                                try {
                                    int code = Integer.parseInt(decodeInfo[2]
                                            .toString());
                                    decodeInfo[2] = NationDeal.decodeNation(code);
                                } catch (Exception e) {
                                    decodeInfo[2] = "";
                                }

                                // 照片解码
                                try {
                                    int ret = IDCReaderSDK.Init();
                                    if (ret == 0) {
                                        byte[] datawlt = new byte[1384];
                                        byte[] byLicData = {(byte) 0x05,
                                                (byte) 0x00, (byte) 0x01,
                                                (byte) 0x00, (byte) 0x5B,
                                                (byte) 0x03, (byte) 0x33,
                                                (byte) 0x01, (byte) 0x5A,
                                                (byte) 0xB3, (byte) 0x1E,
                                                (byte) 0x00};
                                        for (int i = 0; i < 1295; i++) {
                                            datawlt[i] = recData[i];
                                        }
                                        int t = IDCReaderSDK.unpack(datawlt,
                                                byLicData);
                                        if (t == 1) {
                                            onDataReceived(decodeInfo, 1);// 读卡成功
                                        } else {
                                            onDataReceived(null, 6);// 照片解码异常
                                        }
                                    } else {
                                        onDataReceived(null, 6);// 照片解码异常
                                    }
                                } catch (Exception e) {
                                    onDataReceived(null, 6);// 照片解码异常
                                }
                            } else {
                                Log.d(TAG, "读卡失败");
                            }
                        } else {
                            Log.d(TAG, "读卡失败");
                        }
                    } else {
                        Log.d(TAG, "选卡失败");
                    }
                } else {
                    Log.d(TAG, "寻卡失败");
                }
        } catch(IOException e){
            e.printStackTrace();
            return;
        }catch(InterruptedException e){
            e.printStackTrace();
            Thread.interrupted();
            return;
        }
    }

}

    @Override
    public synchronized void start() {
        super.start();
        isRun = true;
    }

    /**
     * 关闭线程 释放资源
     */
    public void release() {
        isRun = false;
    }
}
