# 身份证读取串口mod

## 项目接口说明

### android_serialport_api
<-- 谷歌串口方法，只能用这个包名 -->

### cn.jlb.pro.serialportlib
<-- 串口方法 -->

### listenner
onOpenSerialPortListener：打开串口成功？失败接口
OnSerialPortDataListener：串口数据监听接口

### serial
SerialPortManager：串口管理类，采用单例模式

### thread
SerialPortReadThread：串口消息读取线程，主要读取身份证信息，并用接口返回上层

### util
帮助类

### IDCReader
身份证照片读取

## 使用方式

### app.gradle加载lib  ->  compile project(':SerialPortLib')
### 实现OnOpenSerialPortListener，监听串口回调
### 实现OnSerialPortDataListener，监听数据收发回调
### SerialPortManager.getInstatce().openSerialPort(new File(path), 115200);打开串口
### public void onDataReceived(String[] decodeInfo, int type) {
//接收读取信息，要切换回UI线程
"姓名：" + decodeInfo[0] + "\n" + "性别："
							+ decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
							+ "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
							+ decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
							+ "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
							+ decodeInfo[7] + "-" + decodeInfo[8] + "\n"
							+ decodeInfo[9] + "\n");
					if (type == 1) {
						FileInputStream fis = new FileInputStream(
								Environment.getExternalStorageDirectory()
										+ "/serialport_lib/zp.bmp");
						Bitmap bmp = BitmapFactory.decodeStream(fis);
						fis.close();
						image.setImageBitmap(bmp);
					} else {
						照片解码失败，请检查路径
					}
} 
