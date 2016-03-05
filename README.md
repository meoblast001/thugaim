### Thugaim - You just lost ###

A simple space shooter for Android API 8 (Android 2.2+) created for an
artificial intelligence and game design course. After the course was completed,
the project was expanded.

#### Screenshots ####

![Screenshot 1](website/screenshot1.png)
![Screenshot 2](website/screenshot2.png)
![Screenshot 3](website/screenshot3.png)

#### Build Instructions ####

First you will need the Android SDK and support for Android API 8+. Then locate
the executable `android` in the SDK under `sdk/tools/`. From the project
directory, execute:

    /PATH/TO/ANDROID/sdk/tools/android update project -p `pwd`

This will update the `local.properties` file in the project. Then you can create
a debug version of Thugaim with:

    ant debug

You can then use ADB to install the new `bin/thugaim-debug.apk` on your device:

    /PATH/TO/ANDROID/sdk/platform-tools/adb install -r bin/thugaim-debug.apk
