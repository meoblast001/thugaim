### Thugaim - You just lost ###

A simple space shooter for Android API 13 (Android 3.2+) created for an
artificial intelligence and game design course. After the course was completed,
the project was expanded.

#### Screenshots ####

<img src="website/screenshot1.png" alt="Screenshot 1" height="175px" />
<img src="website/screenshot2.png" alt="Screenshot 2" height="175px" />
<img src="website/screenshot3.png" alt="Screenshot 3" height="175px" />

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
