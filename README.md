# BANK_MANAGEMENT_ADVANCED


### A Microservices-Based Bank Management System

A banking application built using **Java, Spring Boot, and Microservices Architecture**. It provides core banking operations, secure authentication, communication between services, and infrastructure for API routing and deployment.

The project demonstrates how multiple independent services work together to build a structured, maintainable banking application.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [System Architecture](#-system-architecture)
- [System Design Concepts](#-system-design-concepts)
- [Banking Operations](#-banking-operations)
- [Security](#-security)
- [Deployment](#-deployment)
- [Testing](#-testing)
- [Project Goals](#-project-goals)

---

## 📖 Overview

**BANK_MANAGEMENT_PROJECT_ADVANCE** is designed to manage banking operations through separate microservices, with each service responsible for a specific area of the application.

Instead of putting all business logic into one large application, the microservices architecture separates responsibilities and allows services to communicate through REST APIs and messaging.

The project combines banking functionality with authentication, service discovery, API routing, load balancing, and containerization.

## ✨ Key Features

### 👤 User Management
- User registration and login.
- User profile management.
- Authentication for protected resources.

### 🏦 Account Management
- Bank account creation and management.
- Account balance management.
- Banking operations supported through service APIs.

### 💸 Transaction Management
- Deposit and withdrawal operations.
- Money transfers between accounts.
- Communication with other services to perform banking operations.
- Transaction failure handling and compensation where implemented.

### 🔐 Authentication and Security
- JWT-based authentication.
- Spring Security for access control.
- Protection of secured API endpoints.

### 📨 Notifications and Messaging
- Apache Kafka for message-based communication.
- Notification processing through the notification service.
- Event-driven communication where configured.

### 🌐 Infrastructure and Deployment
- Netflix Eureka for service discovery.
- Apache APISIX for API routing and gateway functionality.
- NGINX for load balancing.
- Docker and Docker Compose for containerized deployment.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Main programming language |
| Spring Boot | Building backend microservices |
| Spring Security | Authentication and access control |
| JWT | Carrying authentication information in tokens |
| Spring Data JPA | Database access and persistence |
| MySQL | Storing application data |
| REST APIs | Communication between services |
| RestClient | Making HTTP requests to other services |
| Apache Kafka | Message-based communication and events |
| Netflix Eureka | Service registration and discovery |
| NGINX | Load balancing and request distribution |
| Apache APISIX | API gateway and request routing |
| Docker | Packaging services into containers |
| Docker Compose | Defining and running multiple containers |
| Postman | Testing APIs and request/response behavior |

---

## 🏗️ System Architecture

The following diagram illustrates the high-level architecture and intended communication paths. The exact routing and service connections should be verified against the project configuration.


flowchart TB
    U["User / Web Application"]

    subgraph EDGE["API Entry Layer"]
        N["NGINX<br/>Load Balancing"]
        G["Apache APISIX<br/>API Gateway & Routing"]
    end

    subgraph SERVICES["Banking Microservices"]
        US["User Service"]
        AS["Account Service"]
        TS["Transaction Service"]
        NS["Notification Service"]
    end

    subgraph INFRA["Infrastructure & Messaging"]
        E["Netflix Eureka<br/>Service Discovery"]
        K[("Apache Kafka")]
    end

    subgraph DATA["Data Persistence"]
        DB1[("MySQL<br/>User Data")]
        DB2[("MySQL<br/>Account Data")]
        DB3[("MySQL<br/>Transaction Data")]
        DB4[("MySQL<br/>Notification Data")]
    end

    U -->|HTTP Requests| N
    N -->|Forward Requests| G

    G --> US
    G --> AS
    G --> TS

    US --> DB1
    AS --> DB2
    TS --> DB3
    NS --> DB4

    US -. Registration / Discovery .-> E
    AS -. Registration / Discovery .-> E
    TS -. Registration / Discovery .-> E
    NS -. Registration / Discovery .-> E

    TS -->|REST API via RestClient| AS
    TS -->|Publish Events| K
    K -->|Consume Events| NS

    classDef entry fill:#DCEBFA,stroke:#3973AC,color:#17365D,stroke-width:1.5px
    classDef service fill:#DDF3E4,stroke:#398354,color:#174D2B,stroke-width:1.5px
    classDef infra fill:#FBE8D5,stroke:#C47A30,color:#713F12,stroke-width:1.5px
    classDef data fill:#EAE1F7,stroke:#8262AD,color:#3F2865,stroke-width:1.5px

    class N,G entry
    class US,AS,TS,NS service
    class E,K infra
    class DB1,DB2,DB3,DB4 data


### 🔄 How the Components Work Together

- **Web Application:** Provides the user-facing interface for banking operations.
- **NGINX:** Handles configured request distribution and load balancing.
- **Apache APISIX:** Routes incoming API requests to the appropriate services.
- **User Service:** Handles user-related operations.
- **Account Service:** Manages bank accounts and account-related operations.
- **Transaction Service:** Coordinates deposits, withdrawals, and transfers.
- **Eureka Server:** Helps registered services discover one another.
- **Apache Kafka:** Supports asynchronous message communication.
- **Notification Service:** Processes notification messages according to its configuration.
- **MySQL:** Stores application data according to the database configuration of each service.

*Note: The diagram is conceptual. It does not imply that every service has a separate physical MySQL server or that every illustrated connection is already implemented.*

---

## 🧠 System Design Concepts

### 1. Microservices Architecture
The application is divided into smaller services with separate responsibilities, making the codebase easier to organize and maintain.

### 2. Separation of Responsibilities
User management, account management, transaction processing, and notifications are handled by their respective services.

### 3. REST-Based Communication
Services can exchange information through HTTP APIs. RestClient is used for service-to-service HTTP requests where implemented.

### 4. Event-Driven Architecture
Apache Kafka enables services to exchange messages asynchronously, reducing the need for every operation to depend on an immediate response from another service.

### 5. Service Discovery
Netflix Eureka provides a registry where configured services can register and discover other services.

### 6. API Gateway
Apache APISIX provides a central entry point for API routing and related gateway capabilities.

### 7. Load Balancing
NGINX can distribute incoming requests across configured backend instances.

### 8. Authentication and Authorization
JWT and Spring Security are used to authenticate users and protect authorized operations.

### 9. Transaction Failure Handling
Money transfers may involve multiple service operations. Compensation logic can be used to reverse an earlier operation if a later step fails, where that logic is implemented.

### 10. Containerization
Docker packages application services and their runtime dependencies. Docker Compose helps define and run the configured application environment.

---

## 💳 Banking Operations

The application focuses on common banking operations:

- **Registration and Login:** Users access the application through the configured authentication flow.
- **Account Creation:** Users can create and manage bank accounts through supported APIs.
- **Deposit:** Add money to an account.
- **Withdrawal:** Deduct money from an account, subject to business rules.
- **Money Transfer:** Transfer money between accounts through the transaction workflow.
- **Notifications:** Process configured messages related to application or banking events.

Banking operations must enforce appropriate validation, authorization, and balance checks. Successful behavior should be verified through the application's tests.

---

## 🔐 Security

Security-related technologies used in the project include:

- **Spring Security:** Configures access control for protected resources.
- **JWT:** Supports token-based authentication.
- **Protected APIs:** Restrict access according to configured security rules.

**Security reminder:** Never commit real database passwords, JWT signing keys, access tokens, or other credentials to GitHub. Keep local secrets outside version control and provide safe configuration examples where needed.

---

## 🐳 Deployment

Docker and Docker Compose are part of the project's deployment stack.

The deployment configuration can define the application services and their dependencies, including the database, messaging infrastructure, service discovery, and gateway components.

For the exact startup procedure, refer to the project's Docker Compose file, Dockerfiles, and service configuration. Required environment variables and exposed ports should be documented from the actual configuration rather than guessed.

---

## 🧪 Testing

Postman can be used to test the application's REST APIs, including:

- User registration and authentication.
- Account creation and retrieval.
- Deposit and withdrawal operations.
- Money transfers.
- Authentication and authorization behavior.
- Error handling and invalid requests.

Testing should also cover failure scenarios, especially money-transfer compensation, to check whether balances remain consistent when a service operation fails.

---

## 🎯 Project Goals

This project aims to:

- Build a banking application using microservices.
- Understand communication between independent services.
- Implement secure API access with JWT and Spring Security.
- Explore event-driven messaging with Kafka.
- Understand service discovery, gateway routing, and load balancing.
- Package application components using Docker.
- Learn how to design, test, and maintain a multi-service application.

---

## 👨‍💻 About the Project

**Project Name:** BANK_MANAGEMENT_PROJECT_ADVANCE

**Category:** Banking Application / Backend Development

**Architecture:** Microservices

**Primary Language:** Java

**Backend Framework:** Spring Boot

This project is a practical exploration of backend development, distributed application design, API security, and deployment infrastructure.

---

⭐ If you find this project useful, feel free to explore the code, review the architecture, and suggest improvements.
