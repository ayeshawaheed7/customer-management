# customer-management
The Customer Management Service is designed to handle customer-related operations within our system. Facilitates the end-to-end registration process, verifying new customers through the Fraud Service before finalizing their registration.

## Register Customer Functionality
One of the key features of the Customer Management service is the Register Customer functionality. The process involves:

- **Fraud Check:** When a customer attempts to register, the Customer Service uses Feign Client to communicate with the Fraud Service, verifying the customer's legitimacy. This step is crucial to detect and prevent fraudulent activities.
- **Notification:** Once the Fraud Service confirms that the customer is not flagged as fraudulent, the Customer Service sends a notification via the Notification Service to inform the customer of their registration status.
- **Registration:** Following the successful fraud check and notification, the Customer Service proceeds with the registration process, storing the customer’s data in the database.
- **Integration:** The integration between Customer Service, Fraud Service, and Notification Service ensures a secure, efficient, and reliable registration process, maintaining the integrity of our customer base and enhancing the user experience.

## Detailed Overview
For a more in-depth understanding of our project, including detailed architecture, best practices, and technical implementation, please visit our GitHub Wiki.

