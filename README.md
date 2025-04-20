# Corporate Ride Management System

This is the backend for the Corporate Ride Management System built using Spring Boot.

# Objectives
Corporate Ride Management System is a web-based application designed for companies to manage employee transportation and shuttle services. It ensures efficient commuting, catering to businesses with regular employee transportation needs.

This project demonstrates the use of a microservices-based approach with event-driven communication.

# Main Features
## 1. Ride management
* Employees can request, cancel rides and view ride history.
* Drivers can start, complete assigned rides and view ride history.
* Admins can monitor, cancel rides and view ride history.

## 2. Driver Management
* Admins can register and view drivers.

## 3. User Management
* Admins can register and view users.
* Users can log in with role-based access control (RBAC).

## 4. Automated driver assignments to rides

# Target Users
* Office employees with transportation needs → Passenger
* Drivers that receive ride assignments → Driver
* Businesses that provide and manage employee transportation → Admin

# Usage Scenario Examples
* Inter-Plant Travel Request: A maintenance supervisor at a large manufacturing corporation, needs to travel from Plant A to Plant D for an urgent inspection.
* Late Shift Transport: An employee finishing a late-night shift requests a ride from the facility to the nearby staff dormitory.
* Managerial Meeting: A department head requests a ride to attend a scheduled meeting at the administration block.
* Urgent Equipment Delivery: A technician uses the system to arrange a driver to transport a part urgently from the warehouse to a repair site.

# Technical implementation

## Architecture
![Architecture](https://github.com/user-attachments/assets/fe76fe5c-90a2-479f-92e9-a99227dd7bdd)

![Tech Stack](https://github.com/user-attachments/assets/76332db4-c951-4d60-b9ab-ca9e5b31cbc0)

## Libraries Used
Spring Boot: web, jpa, validation, security, mysql, kafka, swagger, jjwt, lombok, servlet, twilio

## Microservices Overview
1. Ride Service \
Handles ride creation & lifecycle updates and publishes events for other services to react to. 

Endpoints:
* POST /rides/request → creates a ride (Requested); publishes RideRequestedEvent
* PATCH /rides/{id}/start → marks ride as Ongoing; publishes RideStartedEvent
* PATCH /rides/{id}/complete → marks ride as Completed; publishes RideCompletedEvent
* PATCH /rides/{id}/cancel → marks ride as Cancelled; publishes RideCancelledEvent
* GET /rides → returns all rides (or filters by userId, driverId, status)
* GET /rides/{id} → returns ride by id

Event handlers: 
* DriverAssignedEvent → assigns driver and marks ride as assigned
* NoDriverAvailableEvent → marks ride as pending

2. Driver Service \
Manages drivers and their availability.

Endpoints:
* POST /drivers/register → creates a driver
* GET /drivers → returns all drivers
* GET /drivers/{id} → returns driver by id

Event handlers:
* RideRequestedEvent → marks first available driver as busy and publishes DriverAssignedEvent or NoDriverAvailableEvent if there are no available drivers
* RideStartedEvent → marks driver as busy
* RideCompletedEvent → marks driver as available
* RideCancelledEvent → marks driver as available or ignored if no driver was already assigned (for pending rides)

3. User Service \
Manages users’ registration, authentication.

Endpoints:
* POST /users/register → creates a user
* GET /users → returns all users
* GET /users/{username} → returns user by username
* POST /auth/login → authenticates and returns JWT token

4. Notification Service \
Manages notifications for ride status changes; uses Twilio API.

# Sequence Diagrams
![Ride Request](https://github.com/user-attachments/assets/b9728ca5-dd79-41f5-b116-18c7ea9d5f21)

![Ride Commencement, Completion](https://github.com/user-attachments/assets/d46c7212-da23-4968-adcb-98150c90825d)

