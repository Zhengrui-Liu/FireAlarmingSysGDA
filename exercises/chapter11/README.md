# Gateway Device Application (Gateway Devices)

## Lab Module 11


### Description

Integration with Ubidots.

Set cloud function:
1. Trigger pressure actuator in cloud
2. Trigger humidifier in GDA
3. Trigger HVAC in CDA

### Code Repository and Branch


URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-Zhengrui-Liu/tree/chapter11

### UML Design Diagram(s)

![image](./GDA-chapter11.svg)


### Unit Tests Executed

- piot-java-components/src/test/java/programmingtheiot/part01/unit/common/ConfigUtilTest.java
- piot-java-components/src/test/java/programmingtheiot/part01/unit/system/all
- piot-java-components/src/test/java/programmingtheiot/part02/unit/data/all

### Integration Tests Executed

- piot-java-components/src/test/java/programmingtheiot/part01/integration/app/GatewayDeviceAppTest.java
- piot-java-components/src/test/java/programmingtheiot/part02/integration/app/all
- piot-java-components/src/test/java/programmingtheiot/part02/integration/data/all
- piot-java-components/src/test/java/programmingtheiot/part03/integration/connection/MqttCilentConnectorTest.java
- piot-java-components/src/test/java/programmingtheiot/part03/integration/connection/CoapServerGatewayTest.java
- piot-java-components/src/test/java/programmingtheiot/part03/integration/connection/CoapClientConnectorTest.java

### Subscription of Ubidots

![image](./ubi.png)

### Sensor at Emulator

![image](./emu.png)
