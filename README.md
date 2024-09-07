# customer-management
The Customer Management Service is designed to handle customer-related operations within our system. Facilitates the end-to-end registration process, verifying new customers through the Fraud Service before finalizing their registration.

## Register Customer Functionality
One of the key features of the Customer Management service is the Register Customer functionality. The process involves:

- **Fraud Check:** When a customer attempts to register, the Customer Service uses Feign Client to communicate with the Fraud Service, verifying the customer's legitimacy. This step is crucial to detect and prevent fraudulent activities.
- **Notification:** Once the Fraud Service confirms that the customer is not flagged as fraudulent, the Customer Service publishes an event to notify the Notification Service. The Notification Service then sends a notification to the customer.
- **Registration:** Following the successful fraud check and notification, the Customer Service proceeds with the registration process, storing the customer’s data in the database.
- **Integration:** The integration between Customer Service, Fraud Service, and Notification Service is partially event-driven. Notifications are managed via events for a decoupled and reliable process. However, Customer Service and Fraud Service still use Feign Client for synchronous communication. Idempotency keys in event handling ensure data integrity and enhance the user experience, while the event-driven approach improves scalability and reduces dependencies.

### Observability
To monitor and trace these interactions, we utilize distributed tracing. For more details on how to view and analyze the distributed traces, please visit the [Observability Documentation](https://github.com/ayeshawaheed7/customer-management/wiki#observability).

## Running the Application with Docker
### Prerequisites:
- Ensure Docker and Docker Compose are installed.
- Python 3.x installed
- `requests` library installed (`pip install requests`)
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
- API Gateway: http://localhost:9093 - Use for routing requests.
- Customer Service: http://localhost:9090 - Direct access to customer-related services.

You can choose to use the API Gateway or access the Customer Service directly.

### Using the Python Script:
- Run the script with:
```
python3 post_customer_registration.py
```
The script will send a POST request to the `Register Customer` endpoint.

### Check container status with:
```
docker ps
```
### Stop the Environment:
- To stop and clean up the environment, run:
```
docker-compose -f docker-compose-containerization.yml down
```
## Running the Application with Kubernetes
### Prerequisites:
- Install Minikube and kubectl.
- Python 3.x installed
- `requests` library installed (`pip install requests`)
### Start and Deploy:
1. Run the following command to start Minikube and deploy the entire environment:
```
./deploy.sh
```
This will:

- Start Minikube with 4GB of memory.
- Deploy PostgreSQL, Kafka, and observability services (Otel and Zipkin).
- Deploy the microservices for Customer, Fraud, and Notification.

2. Expose Services Using Minikube Tunnel: Some services use `LoadBalancer`  type, which requires running `minikube tunnel`  to make them accessible:

Open a new terminal window and run:
```
minikube tunnel
```
This command will expose services with LoadBalancer type and make them accessible via external IP addresses.

### Accessing the Application:
- Customer Service: http://localhost:9090

### Using the Python Script:
- Run the script with:
```
python3 post_customer_registration.py
```
The script will send a POST request to the `Register Customer` endpoint.

You can now access services once the deployment completes and the Minikube tunnel is running.

## Detailed Overview
For a more in-depth understanding of project, including detailed architecture, best practices, and technical implementation, please visit the [project Wiki](https://github.com/ayeshawaheed7/customer-management/wiki).

