README

1. Download Android SDK (https://developer.android.com/sdk/index.html)

2. Download OpenCV for Android (http://opencv.org/downloads.html)

3. Upload the photos into the device (use {your location of Android SDK}/sdk/platform-tools/adb)
3.a the website link of the photo (http://www.vision.ee.ethz.ch/showroom/zubud/ZuBuD.tar.gz)
Command line [./adb adb push /path/to/local/file /mnt/sdcard/Pictures/FindFind]

3. Upload the “location.txt” into the device (use {your location of Android SDK}/sdk/platform-tools/adb)
Command line [./adb adb push /path/to/local/file /mnt/sdcard/]

4. Open the Eclipse in Android SDK folder


In Eclipse:
1. Download the Google Play services SDK via the Android SDK Manager (Click [Window->Android SDK Manager] -> install google play service)

2. Import the projects (Click File->Import->Existing Project into Workspace, Then import all projects)

3. Link the project to the OpenCV SDK (right click the “”FindFind” project -> Properties -> Android -> libraries -> delete all in the list -> add “OpenCV Library - 2.4.7” and “google-play-service_lib”)

3. Run it on a virtual machine or active device

4. Enjoy it


Easy way

1. install the app by FindFind.apk (How to install an APK: http://developer.android.com/tools/help/adb.html#move)