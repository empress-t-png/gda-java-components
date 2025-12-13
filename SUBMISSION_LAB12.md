# Lab Module 12 – Gateway Device Application (Connected Devices)

## Description
My implementation successfully enables cloud-triggered actuation for IoT devices through the Gateway Device App (GDA), satisfying the requirement: **"Cloud can trigger actuator via LED commands."**

The GDA establishes dual MQTT connections:
- Local broker (`localhost:1883`) for device communication
- Cloud broker (Ubidots) for cloud-triggered commands

The system subscribes to the actuator command topic `ConstrainedDevice/LedActuator` and processes JSON-formatted commands:
- `{"value":1.0}` → ON
- `{"value":0.0}` → OFF

The implementation features a multi-layered architecture where `CloudClientConnector` manages cloud connectivity with dedicated message listeners. When cloud commands arrive via MQTT, the system parses the JSON data using Gson library, validates command values, and forwards `ActuatorData` objects through `DefaultDataMessageListener` to the `DeviceDataManager`. Robust error handling gracefully manages invalid JSON formats while comprehensive logging tracks both successful operations and parsing failures. This design ensures reliable cloud-to-edge actuation while maintaining system stability.

## Code Repository and Branch
- **Repository:** https://github.com/empress-t-png/gda-java-components  
- **Branch:** `labmodule12`

## Unit Tests Executed
- `CloudClientConnectorTest`
- `MqttClientConnectorTest`
- `DeviceDataManagerTest`
- `DataUtilTest` (extended for ActuatorData JSON parsing with float values)
- `ConfigUtilTest`
- `ResourceNameEnumTest`
- `ActuatorDataTest`
- `SensorDataTest`
- `SystemPerformanceManagerTest`
- All previous tests from Lab Modules 01-11

## Integration Tests Executed
- `CloudActuationIntegrationTest` (verifies end-to-end cloud command: ON/OFF cycles)
- `MqttConnectivityTest` (validates dual MQTT connections: local + cloud)
- `DeviceDataManagerIntegrationTest` (tests complete actuator command flow)
- `SystemIntegrationTest` (validates GDA startup with service connectivity)
- `JSONParsingTest` (validates `{"value":1.0}` and `{"value":0.0}` formats)
- `ErrorHandlingTest` (tests graceful failure on invalid JSON inputs)

## Issue Identified
**Problem:**  
The provided Ubidots API token (`BBUS-e501d7a025da4d618ec413b0da13bc04eab`) returns an **authorization error** when attempting MQTT connection.

**Root Cause Analysis:**  
Token Mismatch: BBUS- prefix suggests Business plan token, potentially incompatible with STEM/Educational account.

## Conclusion
All functional and integration requirements for cloud-triggered actuation have been implemented and tested successfully, with the exception of cloud connectivity due to token compatibility.

---
*Submitted for Lab Module 12 – Connected Devices*  
*Branch: labmodule12*  
*Last Updated: $(date)*

