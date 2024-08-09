# customer-management
The Customer Management Service is designed to handle customer-related operations within our system. Facilitates the end-to-end registration process, verifying new customers through the Fraud Service before finalizing their registration.

## Register Customer Functionality
One of the key features of the Customer Management service is the Register Customer functionality. The process involves:

* Fraud Check: When a customer attempts to register, the Customer Service communicates with the Fraud Service to verify the customer's legitimacy.
* Registration: If the Fraud Service does not flag the customer as fraudulent, the Customer Service proceeds with the registration process, storing the customer's data in the database.
* Integration: The integration between Customer Service and Fraud Service ensures a secure and reliable registration process, maintaining the integrity of our customer base.

## Detailed Overview
For a more in-depth understanding of our project, including detailed architecture, best practices, and technical implementation, please visit our [GitHub Wiki](https://github.com/ayeshawaheed7/customer-management.wiki.git). The Wiki provides comprehensive information on:

* Architecture and Design: A detailed breakdown of our modular monolith and microservices approach.
* Best Practices: Insights into the coding practices, testing strategies, and configuration management.
* CI/CD Automation: An overview of our CI/CD pipelines, including build and deployment processes.
* Technology Stack: Information about the technologies and tools used in our project.

Explore the Wiki to get a complete picture of our development journey and technical setup.

