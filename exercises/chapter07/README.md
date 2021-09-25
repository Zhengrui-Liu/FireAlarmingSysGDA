# Gateway Device Application (Gateway Devices)

## Lab Module 07


### Description

Implemented mqtt functions

1.Implemented publish,subscribe and unsubscribe functions.
2.Inplemented a simple version of callback functions.
3.Connected mqttClientConnector with DDM.

### Code Repository and Branch


URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-Zhengrui-Liu/tree/chapter07

### UML Design Diagram(s)

![image](./GDA-chapter07.svg)


### Unit Tests Executed

- piot-java-components/src/test/java/programmingtheiot/part01/unit/common/ConfigUtilTest.java
- piot-java-components/src/test/java/programmingtheiot/part01/unit/system/all
- piot-java-components/src/test/java/programmingtheiot/part02/unit/data/all

### Integration Tests Executed

- piot-java-components/src/test/java/programmingtheiot/part01/integration/app/GatewayDeviceAppTest.java
- piot-java-components/src/test/java/programmingtheiot/part02/integration/app/all
- piot-java-components/src/test/java/programmingtheiot/part02/integration/data/all
- piot-java-components/src/test/java/programmingtheiot/part02/integration/connection/MqttCilentConnectorTest.java


### MQTT 14 Control Packets (QoS 1 and 2)

CONNECT

![image](./mqtt/connect.png)

CONNACK

![image](./mqtt/connectack.png)

PUBLISH

![image](./mqtt/pub.png)

PUBACK(QoS1 only)

![image](./mqtt/puback.png)

PUBREC(QoS2 only)

![image](./mqtt/pubrec.png)

PUBREL(QoS2 only)

![image](./mqtt/pubrel.png)

PUBCOMP(QoS2 only)

![image](./mqtt/pubcmp.png)

SUBSCRIBE

![image](./mqtt/subreq.png)

SUBACK

![image](./mqtt/suback.png)

UNSUBSCRIBE

![image](./mqtt/unsub.png)

UNSUBACK

![image](./mqtt/unsuback.png)

PINGREQ

![image](./mqtt/ping.png)

PINGRESP

![image](./mqtt/pingres.png)

DISCONNECT

![image](./mqtt/disconnect.png)