![Logo](https://raw.githubusercontent.com/leotyndale/EnFloatingView/master/preview/logo.png)

EnCrashWhiteList
==========================
[![Jcenter](https://img.shields.io/badge/Jcenter-v1.0-brightgreen.svg?style=flat)](https://bintray.com/leotyndale/Muxuan/EnCrashWhitList)
[![Muxuan](https://img.shields.io/badge/PoweredBy-Muxuan-green.svg?style=flat)](http://blog.imuxuan.com/)
[![Website](https://img.shields.io/website-up-down-green-red/https/shields.io.svg?label=Blog)](http://blog.imuxuan.com)

![《移动开发架构设计实战》](https://upload-images.jianshu.io/upload_images/14802001-4864444c478c88ee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

[点击购买](https://item.jd.com/12730926.html)


## 适用范围

- 主线程调用栈中出现Looper.loop
- 某一类特殊 BUG
- 疑难的 ROM BUG
- 捕获后不影响正常生命周期的崩溃

## 使用场景

在实际工作时，我们为了维护APP的崩溃率，经常需要面临由于安卓系统碎片化带来的各种各样奇葩的BUG，其中有一些是 ROM 的 BUG，ROM 平台又不推送更新修复的，还有一些是没有思路的棘手问题，比如：

占版本崩溃量 10% 的某一类 NullPointerException。

```java
Fatal Exception: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.Class java.lang.Object.getClass()' on a null object reference
       at android.os.Message.toString + 574(Message.java:574)
       at android.os.Message.toString + 480(Message.java:480)
       at android.os.Looper.loop + 187(Looper.java:187)
       at android.app.ActivityThread.main + 5509(ActivityThread.java:5509)
       at java.lang.reflect.Method.invoke(Method.java)
       at java.lang.reflect.Method.invoke + 372(Method.java:372)
       at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run + 961(ZygoteInit.java:961)
       at com.android.internal.os.ZygoteInit.main + 756(ZygoteInit.java:756)
```

占版本崩溃量的 5% 的某一类 RemoteServiceException：

```java
Fatal Exception: android.app.RemoteServiceException: Bad notification posted from package *: Couldn't expand RemoteViews for: StatusBarNotification(pkg=* user=UserHandle{0} id=3000171 tag=null score=20: Notification(pri=2 contentView=*/0x1090064 vibrate=default sound=default defaults=0xffffffff flags=0x11 kind=[null]))
       at android.app.ActivityThread$H.handleMessage + 1366(ActivityThread.java:1366)
       at android.os.Handler.dispatchMessage + 102(Handler.java:102)
       at android.os.Looper.loop + 136(Looper.java:136)
       at android.app.ActivityThread.main + 5095(ActivityThread.java:5095)
       at java.lang.reflect.Method.invokeNative(Method.java)
       at java.lang.reflect.Method.invoke + 515(Method.java:515)
       at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run + 786(ZygoteInit.java:786)
       at com.android.internal.os.ZygoteInit.main + 602(ZygoteInit.java:602)
       at dalvik.system.NativeStart.main(NativeStart.java)
```

但是我们发现，这种崩溃只集中在部分机型。

![image-20190903151058768](README.assets/image-20190903151058768.png)

然后又一顿操作，我们确定这个是 ROM BUG 了。

![image-20190903151116187](README.assets/image-20190903151116187.png)

这时候，我们就需要 Crash 白名单来整治这些问题，避免对用户产生影响。

## 运行原理

当线程执行完可执行的代码段后，会停止生命周期，而安卓中的主线程有 Looper.loop() 可以开启死循环，保证主线程的存活。

```java
package android.os;

public final class Looper {
  	……
		public static void loop() {
        ……
        for (;;) {
            ……
        }
    }
}
```

这个 loop 是在 ActivityThread 的 main 方法中启动死循环的，关于启动流程，请自行脑补。

```java
package android.app;

public final class ActivityThread extends ClientTransactionHandler {
  	……
    public static void main(String[] args) {
        ……
        Looper.loop();
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
}
```

当我们在程序中手动调用 Looper.loop()，我们就成为了 loop 的启动方，处理主线程消息，这时候，我们只要 try catch 住 Looper.loop()，那么，调用栈从 loop 报出来的崩溃就都可以被我们捕获。

```java
    new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() {
                try {
                    Looper.loop();
                } catch (Throwable e) {
                    Log.i(TAG, "loop crash:" + e);
                }
            }
        }
    });
```

不过这个并不新鲜，类似的实践有很多(like [StackOverflow](https://stackoverflow.com/questions/2764394/ideal-way-to-set-global-uncaught-exception-handler-in-android%29))，我们不能一味的捕获主线程中的崩溃，需要有一套白名单崩溃处理机制和 loop 协作，来整治棘手的崩溃问题。

## 使用方法

### 依赖配置

在gralde的dependencies中加入

```groovy
compile 'com.imuxuan:whitecrash:1.0'
```

### 配置崩溃信息

启动白名单

```java
WhiteCrash.get().catchCrash(new CrashInfo()) // 配置崩溃信息1
  .catchCrash(new CrashInfo()) // 配置崩溃信息1
  .catchCrash(new CrashInfo()) // 配置崩溃信息1
  .setCustomInfo(deviceCustomInfo) // 配置自定义信息，如设备ID等
  .start(); // 启动白名单处理
```

崩溃信息配置

```java
new CrashInfo().setExceptionName("NullPointerException") // 捕获 NullPointerException
   			// 指定捕获 Exception Info 包含如下信息的 NullPointerException
        .setExceptionInfo("Attempt to invoke virtual method " +
                "'java.lang.Class java.lang.Object.getClass()' on a null object reference")
        .addDeviceInfo( // 指定捕获机型1的该崩溃，不指定则全部捕获
                new DeviceInfo().setDeviceName(OPPO_R9PLUSTMA).addBuildVersion("5.1.1")
        )
        .addDeviceInfo( // 指定捕获机型2的该崩溃，不指定则全部捕获
                new DeviceInfo().setDeviceName(OPPO_R9PLUS).addBuildVersion("5.1.1")
        );
```

### DevieName 拼接方式

拼接规则如下：

```java
Build.MANUFACTURER/Build.MODEL
```

其中，Build.MANUFACTURER 为获取设备制造商，Build.MODEL 为获取设备名，用“/”连接。

例如：

```java
vivo/vivo X3L
```

或者，

```java
OPPO/R9PlustmA
```

## 实践效果

OPPO R9 的 NullPointerException 崩溃统计图，崩溃已经下降，剩余少量无白名单机制的旧版本应用还在崩溃：

![image-20190911111339387](README.assets/image-20190911111339387.png)

Vivo X3 的 RemoteServiceException 崩溃统计图，崩溃已经下降，剩余少量无白名单机制的旧版本应用还在崩溃：

![image-20190911111429030](README.assets/image-20190911111429030.png)

其他的还在未来的路上，等你实践。

## 下载测试

[点击下载](https://github.com/leotyndale/EnCrashWhiteList/releases/download/1.0/app-debug.apk)

二维码用微信扫一扫会被拦截，请换其他扫码工具

![image-20190911142747718](README.assets/image-20190911142747718.png)
