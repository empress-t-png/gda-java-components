Semester Project Proposal: Smart Room Temperature Management System
Student Name: Ene Mercy Tosin
Course: Programming the Internet of Things (PIOT)
Date: December 9, 2025

What – Problem Statement
Modern residential and small office spaces often lack intelligent temperature management systems that can autonomously maintain comfortable environmental conditions while providing remote monitoring and control capabilities. Many occupants experience discomfort due to inadequate heating or cooling responses, leading to reduced productivity and increased energy consumption from manual HVAC adjustments. This project addresses the need for an automated, cloud-connected temperature management solution that continuously monitors room temperature, automatically triggers appropriate heating or cooling responses when temperatures fall outside comfortable ranges (20-24°C), and provides real-time visibility and remote control through a cloud-based dashboard. The system aims to bridge the gap between traditional thermostats and modern IoT-enabled smart building solutions by leveraging edge computing and cloud integration.

Why – Purpose
I care about this problem because maintaining optimal indoor temperature directly impacts daily comfort, health, and productivity. During my university experience, I've noticed how uncomfortable room temperatures can affect concentration and well-being, yet many spaces lack automated climate control. This project matters because it demonstrates practical IoT principles that can be applied to real-world scenarios, particularly relevant as smart home adoption continues to grow. From a broader perspective, intelligent temperature management contributes to energy efficiency by preventing unnecessary heating or cooling cycles, aligning with sustainability goals. Additionally, this solution showcases the complete IoT technology stack—from edge devices (sensors and actuators) through gateway processing to cloud analytics and control—making it an ideal culmination of concepts learned throughout the semester. The ability to remotely monitor and control environmental conditions also has applications beyond personal comfort, including protecting temperature-sensitive equipment, plants, or materials in various settings.

How – Diagram of Approach
System Architecture
The solution implements a three-tier IoT architecture consisting of the Constrained Device Application (CDA), Gateway Device Application (GDA), and Cloud Services Framework (CSF):Data Flow
Uplink (Sensor → Cloud):

SenseHAT temperature sensor reads data every 5 seconds
CDA packages data as SensorData object
CDA threshold logic checks temperature range
CDA sends data to GDA via CoAP POST
GDA forwards to Ubidots via MQTT publish
Ubidots stores data and updates dashboard

Downlink (Cloud → Actuator):

User clicks control on Ubidots dashboard OR alert rule triggers
Ubidots publishes ActuatorData command via MQTT
GDA receives command and validates
GDA forwards to CDA via CoAP or MQTT
CDA triggers HVAC actuator emulator
CDA sends acknowledgment back to cloud

Autonomous Operation:

CDA continuously monitors temperature locally
When temp > 24°C: Auto-trigger cooling actuator
When temp < 20°C: Auto-trigger heating actuator
Log all autonomous actions to cloud for audit trail

Technology Stack

CDA: Python 3, SenseHAT Emulator, CoAP/MQTT clients
GDA: Java, Californium (CoAP), Paho (MQTT), Maven
Cloud: Ubidots IoT platform (MQTT broker, dashboard, alerts)
Protocols: CoAP (CDA↔GDA), MQTT over TLS (GDA↔Cloud)
Results – Outcomes
Upon successful implementation, this project will demonstrate a fully functional end-to-end IoT temperature management system with both autonomous and remote control capabilities. The expected outcomes include: (1) Real-time temperature data visualization on the Ubidots dashboard with historical trend analysis, enabling users to observe temperature patterns over time; (2) Automated actuator responses when temperature thresholds are exceeded, demonstrating edge intelligence and reducing reliance on constant cloud connectivity; (3) Bidirectional communication allowing manual override commands from the cloud dashboard to reach the edge device within 2-3 seconds; (4) Alert notifications triggered when temperature remains outside comfortable range for extended periods; and (5) Comprehensive data logging showing correlation between temperature readings and actuator states. Performance metrics will include message latency (target: <5 seconds end-to-end), data reliability (target: >95% successful transmissions), and system uptime. The project will be validated through demonstration scenarios including gradual temperature changes, threshold crossings, manual command execution, and network interruption recovery. Documentation will include system architecture diagrams, configuration files, code repositories with clear commit history, and a final demonstration video showing the complete workflow. This implementation will serve as a proof-of-concept that can be extended to multi-room monitoring, integration with actual HVAC systems, or incorporation of additional environmental sensors.

Implementation Timeline

Week 1: Project setup, branch creation, requirements documentation
Week 2: CDA threshold logic implementation and testing
Week 3: GDA integration and cloud dashboard configuration
Week 4: End-to-end testing, documentation, and demonstration


References

Lab Module 10: Edge Integration (CoAP/MQTT communication)
Lab Module 11: Cloud Integration (Ubidots configuration)
Course textbook: Programming the Internet of Things by Andrew D. King
