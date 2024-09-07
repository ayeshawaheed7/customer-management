import requests
import json

def post_customer_registration_endpoint(data, x_idempotency_key):
    url = "http://localhost:9090/v1/customers"
    headers = {
        "Content-Type": "application/json",
        "X-Idempotency-Key": x_idempotency_key
    }

    try:
        response = requests.post(url, data=json.dumps(data), headers=headers)
        response.raise_for_status()  # Raise an exception for HTTP errors
        print("Status Code:", response.status_code)
    except requests.exceptions.RequestException as e:
        print("Error:", e)

if __name__ == "__main__":
    x_idempotency_key = "54f614bf-9e82-452e-aa75-8e86fced2a24"
    data = {
        "firstName": "Albus",
        "lastName": "Dumbledore",
        "email": "dumbledore@hogwarts.com"
    }
    post_customer_registration_endpoint(data, x_idempotency_key)