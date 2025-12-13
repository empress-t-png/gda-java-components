# Project Presentation: Gateway Device Application (GDA)

## Lab Module 12: Cloud-Triggered Actuation

### Architecture Diagram

*Figure: Cloud-triggered actuation with dual MQTT connectivity*

### Key Components
- **Ubidots Cloud**: MQTT broker with dashboard interface
- **Gateway Device App (GDA)**: Dual MQTT connections (cloud + local)
- **Constrained Device**: LED actuator controlled via JSON commands

### Implementation Summary
- Dual MQTT broker connectivity
- JSON command parsing: `{"value":1.0}` (ON) / `{"value":0.0}` (OFF)
- Cloud-to-edge actuation via `CloudClientConnector`
- Comprehensive unit and integration testing
- *Note: Ubidots token compatibility issue identified*

### Repository Reference
- Lab Branch: `labmodule12`
- Submission: [SUBMISSION_LAB12.md](https://github.com/empress-t-png/gda-java-components/blob/labmodule12/SUBMISSION_LAB12.md)
