#!/bin/bash

# Start Minikube with 4GB memory
minikube start --memory=4g

# Apply PostgreSQL configurations
kubectl apply -f k8s/minikube/bootstrap/postgres/customer
kubectl apply -f k8s/minikube/bootstrap/postgres/fraud
kubectl apply -f k8s/minikube/bootstrap/postgres/notification

# Apply Kafka Cluster configurations
kubectl apply -f k8s/minikube/bootstrap/kafka-cluster/zookeeper
kubectl apply -f k8s/minikube/bootstrap/kafka-cluster/kafka
kubectl apply -f k8s/minikube/bootstrap/kafka-cluster/kafka-ui

# Apply observability configurations
kubectl apply -f k8s/minikube/bootstrap/otel
kubectl apply -f k8s/minikube/bootstrap/zipkin

# Apply microservices configurations
kubectl apply -f k8s/minikube/services/customer
kubectl apply -f k8s/minikube/services/fraud
kubectl apply -f k8s/minikube/services/notification

echo "All services have been successfully deployed."

