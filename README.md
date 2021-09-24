# Yeelight Java SDK
SDK for Java apps to communicate with Yeelight devices.

## Installing
### Gradle
  `implementation 'io.github.przybandrzej:yeelight-java-sdk:0.1.2'`
### Maven
  ```xml
  <dependency>
    <groupId>io.github.przybandrzej</groupId>
    <artifactId>yeelight-java-sdk</artifactId>
    <version>0.1.2</version>
</dependency>
```
  
## Usage
### Search for devices  
  ```java
  Discover discover = new Discover("192.168.2.109", 12345);
  discover.sendSearch();
  boolean searching = true;
  while(searching) {
    try {
      Device device = discover.receiveSearchPacket(5000);
    } catch(SearchTimeoutException e) {
      searching = false;
    }
  }
  ```
  
  The first parameter of the constructor is your machine's IP in local network, the second is a port of your choice - make sure it's not used by other services.
  Then you can send a search packet and receive search response. The response will be from only one device. If you want to search all devices within your network, make sure to     call the method until the timeout.
  

### Control a device
```java
DeviceControl ctrl = new DeviceControl(device);
ctrl.onDeviceStateChange(() -> {
  System.out.println("Callback for when a device has received an update.");
});
ctrl.setPower(true, false);
ctrl.setColorTemperature(1800, false);
ctrl.adjustBrightness(-50, false);
ctrl.close();
```

## Author
**Andrzej Przybysz**  
**Email:** andrzej.przybysz01@gmail.com  
**Github:** [przybandrzej](https://github.com/przybandrzej)  
**LinkedIn:** [Andrzej Przybysz](https://www.linkedin.com/in/andrzej-przybysz/)  
