# Learning Journal — Northstar GraphQL Inventory System

## Project Overview

The Northstar GraphQL Inventory System is a Java-based inventory management API built using GraphQL.

The project allows users to retrieve and manage inventory products through GraphQL queries and mutations. The system uses JSON as the initial data source and exposes the inventory through a GraphQL endpoint.

---

## Day 1 — Project Setup and Java Environment

### What I Did

- Created the `northstar-graphql` project.
- Set up the Java development environment.
- Installed and configured the JDK.
- Created the Maven project structure.
- Created the `pom.xml` file.
- Added the required GraphQL dependencies.
- Created the initial Java application.
- Created the inventory JSON data file.

### What I Learned

I learned how to create a Java project using Maven and how Maven manages project dependencies.

I also learned the importance of having the correct Java version and project structure before starting development.

### Challenges

One of the initial challenges was getting the Java application to run correctly through Maven. I also encountered a main-class configuration issue.

### How I Solved It

I checked the Maven configuration and corrected the Java main class setup. After making the changes, I ran the project again and confirmed that it worked successfully.

---

## Day 2 — Building the GraphQL API

### What I Did

- Created the GraphQL schema.
- Defined the `Product` type.
- Added product fields such as:
  - `id`
  - `name`
  - `category`
  - `price`
  - `quantity`
- Created the `products` query.
- Connected the GraphQL API to the inventory JSON data.
- Configured the local GraphQL server.

### What I Learned

I learned how GraphQL schemas define the structure of data that an API can provide.

I also learned the difference between a GraphQL query and a traditional REST request. With GraphQL, the client can request only the fields it needs.

For example:

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

### Challenges

I initially had difficulty understanding how the GraphQL schema, Java code, and JSON data were connected.

### How I Solved It

I worked through the project structure step by step and tested the GraphQL endpoint using PowerShell. This helped me understand how a request moves from the client to the GraphQL server and retrieves the inventory data.

---

## Day 3 — Testing the API

### What I Did

I tested the GraphQL endpoint using PowerShell and `Invoke-RestMethod`.

I sent a POST request to:

```text
http://localhost:4567/graphql
```

I tested the following GraphQL query:

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

The API successfully returned the product data.

### What I Learned

I learned how to test a GraphQL API without relying on a graphical API testing tool.

I also learned that testing an API helps identify problems in the server, schema, queries, and data connection.

### Evidence of Testing

The GraphQL endpoint successfully returned the inventory products with their IDs, names, categories, prices, and quantities.

---

## Problems I Encountered

During development, I encountered several problems, including:

1. Java and Maven configuration issues.
2. Main class execution errors.
3. Understanding how GraphQL schemas work.
4. Connecting the GraphQL API to the JSON inventory data.
5. Testing the endpoint from PowerShell.

These challenges helped me understand that debugging is an important part of software development.

---

## Problem-Solving Approach

Instead of immediately rewriting the entire project when an error appeared, I learned to:

1. Read the error message.
2. Identify which part of the project caused the problem.
3. Make one change at a time.
4. Run the application again.
5. Test the result.

This made the debugging process more systematic.

---

## Key Skills I Developed

Through this project I developed experience with:

- Java
- Maven
- GraphQL
- JSON data
- API development
- GraphQL queries
- PowerShell API testing
- Debugging
- Git and GitHub
- Technical documentation

---

## Reflection

This project helped me understand how a backend API is built and tested.

The most important lesson I learned was that building software is not only about writing code. It also involves understanding errors, testing different components, documenting the work, and adapting when something does not work as expected.

I became more comfortable working with Java and learned how GraphQL can be used to expose structured data through an API.

---

## Next Steps

The next stage of the project is to:

- Add GraphQL mutations.
- Implement adding products.
- Implement updating products.
- Implement deleting products.
- Improve validation and error handling.
- Test all queries and mutations.
- Push the completed project to GitHub.
- Update the README with the final functionality.

---

## Final Reflection

Overall, the Northstar GraphQL project gave me practical experience in backend development. I learned that understanding the tools is only one part of software development. Being able to troubleshoot errors, test functionality, and document the development process is equally important.

The project also improved my confidence in working with Java, Maven, GraphQL, APIs, and GitHub.