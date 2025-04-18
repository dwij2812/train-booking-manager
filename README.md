# Train Booking Manager

The **Train Booking Manager** is a Spring Boot-based application designed to manage train ticket bookings. It provides APIs for purchasing tickets, modifying seat allocations, retrieving ticket receipts, and managing user and seat information.

---

## Features

- **Ticket Management**:
  - Purchase tickets for specific train sections.
  - Retrieve ticket receipts by user email.
  - Modify seat allocations for existing tickets.
  - Cancel tickets and remove users from the train.

- **User Management**:
  - Add new users to the system.
  - Retrieve user details by email.

- **Seat Management**:
  - View available seats by train section.
  - Retrieve allocated seats and associated users by section.

- **Error Handling**:
  - Comprehensive error handling with meaningful HTTP status codes and error messages.

---

## Getting Started

### Prerequisites

- **Java**: Ensure you have Java 21 or later installed.
- **Maven**: Ensure Maven is installed and configured.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/dwij2812/train-booking-manager.git
   cd train-booking-manager
   ```

2. Build the application using Maven:
    ```bash
    mvn clean install
    ```

3. Run the application:
    ```bash
    mvn spring-boot:run
    ```

4. Use the [Postman collection](https://github.com/dwij2812/train-booking-manager/blob/main/Train%20Booking%20Manager.postman_collection.json) available in the repository to invoke the APIs.
    
