# Yeelight Java SDK
SDK for Java apps to communicate with Yeelight devices.

## Installing
  1. Download source code and compile using `gradlew build`.
  2. Copy the lib/build/libs/lib-0.1.0.jar to your project's ./libs directory.
  3. Add `implementation files('libs/lib-0.1.0.jar')` to build.gradle
  
## Usage
To search for devices use the `Discovery` class.  

To control a device use the `DeviceControl` class.
