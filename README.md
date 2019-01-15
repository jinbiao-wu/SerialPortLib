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