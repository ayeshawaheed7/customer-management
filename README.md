# customer-management
The Customer Management Service is designed to handle customer-related operations within our system. Facilitates the end-to-end registration process, verifying new customers through the Fraud Service before finalizing their registration.

## Register Customer Functionality
One of the key features of the Customer Management service is the Register Customer functionality. The process involves:

- **Fraud Check:** When a customer attempts to register, the Customer Service uses Feign Client to communicate with the Fraud Service, verifying the customer's legitimacy. This step is crucial to detect and prevent fraudulent activities.
- **Notification:** Once the Fraud Service confirms that the customer is not flagged as fraudulent, the Customer Service publishes an event to notify the Notification Service. The Notification Service then sends a notification to the customer.
- **Registration:** Following the successful fraud check and notification, the Customer Service proceeds with the registration process, storing the customer’s data in the database.
- **Integration:** The integration between Customer Service, Fraud Service, and Notification Service is partially event-driven. Notifications are managed via events for a decoupled and reliable process. However, Customer Service and Fraud Service still use Feign Client for synchronous communication. Idempotency keys in event handling ensure data integrity and enhance the user experience, while the event-driven approach improves scalability and reduces dependencies.

## Running the Application with Docker
### Prerequisites:
- Ensure Docker and Docker Compose are installed.
### Start the Environment:
1. Open a terminal in the project directory.
2. Run the following command to set up and start the services:
```
docker-compose -f docker-compose-containerization.yml up -d
```
This will:

- Build the necessary Docker images.
- Start all services, including the database.
- Run everything in detached mode (-d).

### Access the Application:
- API Gateway: http://localhost:9093
- Customer Service: http://localhost:9090

Check container status with:
```
docker ps
```
### Stop the Environment:
- To stop and clean up the environment, run:
```
docker-compose -f docker-compose-containerization.yml down
```
## Detailed Overview
For a more in-depth understanding of our project, including detailed architecture, best practices, and technical implementation, please visit our GitHub Wiki.

