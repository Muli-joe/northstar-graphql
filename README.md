# Northstar GraphQL Inventory System

## Overview

Northstar GraphQL Inventory System is a Java-based inventory management API built using GraphQL.

The system allows users to retrieve, add, update, and delete inventory products through GraphQL queries and mutations.

Inventory data is stored in a JSON file and automatically saved whenever a mutation changes the inventory.

## Technologies Used

- Java 21
- Maven
- GraphQL Java
- Jackson
- JSON
- Java HTTP Server

## Project Structure

```text
northstar-graphql/
├── src/
│   └── main/
│       └── java/
│           └── Main.java
├── data/
│   └── inventory.json
├── pom.xml
├── README.md
└── LEARNING-JOURNAL.md
```

## Features

### Queries

The system supports the following queries:

- Get all products
- Get a product by ID
- Get products by category

### Mutations

The system supports:

- Update product quantity
- Add a new product
- Delete a product

## GraphQL Examples

### Get All Products

```graphql
{
    products {
        id
        name
        category
        price
        quantity
    }
}
```

### Get Product by ID

```graphql
{
    product(id: "1") {
        id
        name
        category
        price
        quantity
    }
}
```

### Get Products by Category

```graphql
{
    productsByCategory(category: "Electronics") {
        id
        name
        category
        price
        quantity
    }
}
```

### Update Quantity

```graphql
mutation {
    updateQuantity(id: "1", quantity: 50) {
        id
        name
        quantity
    }
}
```

### Add Product

```graphql
mutation {
    addProduct(
        id: "7"
        name: "Webcam"
        category: "Electronics"
        price: 7500
        quantity: 12
    ) {
        id
        name
        category
        price
        quantity
    }
}
```

### Delete Product

```graphql
mutation {
    deleteProduct(id: "7") {
        id
        name
        category
        price
        quantity
    }
}
```

## Running the Project

### 1. Clone the repository

```bash
git clone <your-github-repository-url>
```

### 2. Open the project

```bash
cd northstar-graphql
```

### 3. Compile the project

```bash
mvn clean compile
```

### 4. Start the server

```bash
mvn exec:java
```

The server runs at:

```text
http://localhost:4567
```

The GraphQL endpoint is:

```text
http://localhost:4567/graphql
```

## Testing

The GraphQL API can be tested using PowerShell.

Example:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:4567/graphql" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"query":"{ products { id name category price quantity } }"}'
```

## Data Storage

Inventory data is stored in:

```text
data/inventory.json
```

The application loads the inventory from this file when it starts.

When a mutation changes the inventory, the updated inventory is saved back to the JSON file.

## Example Inventory

| ID | Product | Category | Price | Quantity |
|---|---|---|---:|---:|
| 1 | Laptop | Electronics | 85000 | 50 |
| 2 | Wireless Mouse | Accessories | 2500 | 25 |
| 3 | Keyboard | Accessories | 4500 | 15 |
| 4 | Monitor | Electronics | 30000 | 8 |
| 5 | Headphones | Audio | 6500 | 20 |
| 6 | USB Cable | Accessories | 1200 | 30 |

## Project Goal

The goal of this project is to demonstrate how GraphQL can be used to build a simple inventory management API using Java.

The project demonstrates:

- GraphQL queries
- GraphQL mutations
- JSON data storage
- HTTP API communication
- Maven project management
- Java backend development

## Testing Results

The following operations have been successfully tested:

- Retrieve all products
- Retrieve product by ID
- Filter products by category
- Update product quantity
- Add product
- Delete product
- Save inventory changes to JSON
- Access GraphQL through HTTP

## Author

Northstar GraphQL Project