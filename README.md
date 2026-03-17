# Distributed E-Commerce System

A distributed e-commerce system for online product sales, implemented as a university project using Java EE, GlassFish, JMS, JPA, and MySQL.

The system is organized into three independent subsystems that communicate through message-based requests, with a central server exposing REST endpoints and coordinating client interaction.

## Overview

This project simulates an online sales platform where users can browse products, place orders, and manage transactions.  
The application is built as a distributed system with separate responsibilities delegated to different subsystems.

The architecture includes:

- **Subsystem 1** – user, city, and category-related operations
- **Subsystem 2** – item, cart, and order management
- **Subsystem 3** – payment, transaction, and final purchase processing
- **Central Server** – REST API layer and communication bridge toward subsystems via JMS

This design demonstrates distributed communication, modular backend architecture, separation of concerns, and asynchronous request handling.

## Tech Stack

- Java EE
- GlassFish
- JMS
- JAX-RS
- JPA
- MySQL
- Maven / Ant
- NetBeans

## System Architecture

The application consists of:

- **Central Server**
  - exposes REST endpoints
  - receives client requests
  - forwards commands to the appropriate subsystem
  - aggregates and returns responses

- **PS1**
  - manages cities
  - manages users
  - handles categories and related base data

- **PS2**
  - manages items
  - supports item search and filtering
  - handles carts and order preparation

- **PS3**
  - handles transactions
  - processes completed purchases
  - stores final order/payment-related data

Communication between the central server and subsystems is implemented using **JMS queues**.

## Main Features

- user creation and management
- city and category management
- item creation and browsing
- cart and order handling
- purchase processing
- transaction recording
- distributed request-response communication between subsystems
- REST-based access through a central server

## Project Goal

The goal of this project was to design and implement a distributed backend system that simulates a realistic online sales platform while applying concepts such as:

- modular system decomposition
- asynchronous communication
- distributed processing
- persistence and database management
- clean separation between API, business logic, and subsystem responsibilities

## My Contribution

I worked on the design and implementation of the backend architecture, including:

- subsystem communication through JMS
- request and response handling
- REST endpoint integration
- database entity modeling
- service-layer logic
- organization of responsibilities across the three subsystems
- integration of GlassFish, JPA, and MySQL components

## Repository Structure

A possible high-level structure of the project:

- `CentralServer/` – REST API and communication layer
- `Podsistem1/` – user, city, category logic
- `Podsistem2/` – item, cart, order logic
- `Podsistem3/` – transaction and payment logic
- `common/` – shared commands, enums, request/response models
- `database/` – SQL scripts or schema files

## How to Run

1. Configure GlassFish server and JMS resources.
2. Create and configure MySQL databases for all subsystems.
3. Build shared/common module.
4. Start all three subsystems.
5. Start the central server.
6. Test the REST endpoints through Postman or another API client.

## Notes

This project was developed as part of a university coursework assignment focused on distributed systems and enterprise Java development.

## Future Improvements

- authentication and authorization
- better error handling and validation
- Docker-based local setup
- frontend client application
- logging and monitoring improvements
- automated integration testing
