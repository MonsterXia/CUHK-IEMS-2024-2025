# Assignment1

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Your Name</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>
</div>


### Basic information

My phone model is <strong>XiaoMi 12S Pro</strong>(Xiaomi 2206122SC/Android 12L)
I use an Android emulator.

[TOC]

<div style="page-break-after: always;"></div>

## Result

Screenshot(s) for the project tree:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111734748.png"/>

Screenshot(s) for running your project:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111734747.png"/>

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111735048.png)





## Detailed Result

### 1. Create a new TextView holding your student ID and student name in a single line. (50%)

Create the Project follow the Assignment.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111735431.png)

 Create and manage virtual devices.

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409132326506.png"/>

However, it was found to be quite laggy in actual use, so another approach was used.

I already have the Android emulator "MuMu Emulator" installed on my PC.

Start the "Mumu Emulator".

Enter "Settings"->"About phone"->"Build number", click it multiple times until the system shows that "No need, you are already a developer" to enter the developer mode.

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409140153225.png"/>

Enter "Settings"->"System"->"Developer options", open the "USB debugging".

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409140155898.png"/>

In my PC, add the "MuMu Emulator"'s install path to system's environment path so that we can use terminal to start link to Android emulator anywhere.

![image-20240914020141699](C:\Users\monst\AppData\Roaming\Typora\typora-user-images\image-20240914020141699.png)

To connect, use

```bash
adb connect 127.0.0.1:7555
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409140204282.png"/>

At this case, the emulator can be fond at Android Studio's Device Manager marked as Physical.

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409140206777.png"/>

In app‘s res/values/strings.xml, create string first.

```xml
<string name="student_info">Student ID, Your Name</string>
```

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111736096.png)

In layout/activity_main.xml drag the TextView in and change the text of it as shown.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111737465.png)

### 2.Change your app icon under the res/drawable folder. (50%)

Right click res to create a dir, choose mipmap as type and name it myicon.

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409220032299.png"/>

Switch to project view and find the just created folder, right click to add an image asset.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111738058.png)

Rename to app_icon and upload a icon from desktop. Next->Finish.

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409220036759.png"/>

Back to android view, in AndroidManifest.xml set icon to just uploaded image asset.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111738892.png)

Start to build and run

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409220046205.png"/>

The in app page shows

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111734747.png"/>



Back to home check the icon and name of app.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111735048.png)



To disconnect Android emulator, use

```bash
adb disconnect 127.0.0.1:7555
```

