import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String INVENTORY_FILE = "data/inventory.json";

    private static List<Map<String, Object>> products;

    public static void main(String[] args) throws Exception {

        System.out.println();
        System.out.println("=================================");
        System.out.println(" Northstar GraphQL Inventory System");
        System.out.println("=================================");
        System.out.println();

        // ==========================================
        // LOAD INVENTORY
        // ==========================================

        products = loadInventory();

        System.out.println(
                "Loaded " + products.size() + " products.");

        System.out.println();

        // ==========================================
        // GRAPHQL SCHEMA
        // ==========================================

        String schema = """
                type Product {
                    id: ID!
                    name: String!
                    category: String!
                    price: Float!
                    quantity: Int!
                }

                type Query {
                    products: [Product!]!
                    product(id: ID!): Product
                    productsByCategory(category: String!): [Product!]!
                }

                type Mutation {
                    updateQuantity(
                        id: ID!
                        quantity: Int!
                    ): Product

                    addProduct(
                        id: ID!
                        name: String!
                        category: String!
                        price: Float!
                        quantity: Int!
                    ): Product

                    deleteProduct(
                        id: ID!
                    ): Product
                }
                """;

        // ==========================================
        // PARSE SCHEMA
        // ==========================================

        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);

        // ==========================================
        // GRAPHQL WIRING
        // ==========================================

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()

                // ==================================
                // QUERIES
                // ==================================

                .type(
                        "Query",
                        builder -> builder

                                // All products
                                .dataFetcher(
                                        "products",
                                        environment -> products)

                                // Product by ID
                                .dataFetcher(
                                        "product",
                                        environment -> {

                                            String id = environment
                                                    .getArgument(
                                                            "id");

                                            return findProduct(id);
                                        })

                                // Products by category
                                .dataFetcher(
                                        "productsByCategory",
                                        environment -> {

                                            String category = environment
                                                    .getArgument(
                                                            "category");

                                            List<Map<String, Object>> result = new ArrayList<>();

                                            for (Map<String, Object> product : products) {

                                                String productCategory = product.get(
                                                        "category").toString();

                                                if (productCategory
                                                        .equalsIgnoreCase(
                                                                category)) {

                                                    result.add(product);
                                                }
                                            }

                                            return result;
                                        }))

                // ==================================
                // MUTATIONS
                // ==================================

                .type(
                        "Mutation",
                        builder -> builder

                                // Update quantity
                                .dataFetcher(
                                        "updateQuantity",
                                        environment -> {

                                            String id = environment
                                                    .getArgument(
                                                            "id");

                                            Integer quantity = environment
                                                    .getArgument(
                                                            "quantity");

                                            Map<String, Object> product = findProduct(id);

                                            if (product == null) {
                                                return null;
                                            }

                                            product.put(
                                                    "quantity",
                                                    quantity);

                                            saveInventory();

                                            return product;
                                        })

                                // Add product
                                .dataFetcher(
                                        "addProduct",
                                        environment -> {

                                            String id = environment
                                                    .getArgument(
                                                            "id");

                                            // Prevent duplicate IDs
                                            if (findProduct(id) != null) {
                                                return null;
                                            }

                                            String name = environment
                                                    .getArgument(
                                                            "name");

                                            String category = environment
                                                    .getArgument(
                                                            "category");

                                            Double price = environment
                                                    .getArgument(
                                                            "price");

                                            Integer quantity = environment
                                                    .getArgument(
                                                            "quantity");

                                            Map<String, Object> newProduct = new java.util.LinkedHashMap<>();

                                            newProduct.put(
                                                    "id",
                                                    id);

                                            newProduct.put(
                                                    "name",
                                                    name);

                                            newProduct.put(
                                                    "category",
                                                    category);

                                            newProduct.put(
                                                    "price",
                                                    price);

                                            newProduct.put(
                                                    "quantity",
                                                    quantity);

                                            products.add(
                                                    newProduct);

                                            saveInventory();

                                            return newProduct;
                                        })

                                // Delete product
                                .dataFetcher(
                                        "deleteProduct",
                                        environment -> {

                                            String id = environment
                                                    .getArgument(
                                                            "id");

                                            Map<String, Object> product = findProduct(id);

                                            if (product == null) {
                                                return null;
                                            }

                                            products.remove(product);

                                            saveInventory();

                                            return product;
                                        }))

                .build();

        // ==========================================
        // BUILD GRAPHQL
        // ==========================================

        GraphQLSchema graphQLSchema = new SchemaGenerator()
                .makeExecutableSchema(
                        typeRegistry,
                        wiring);

        GraphQL graphQL = GraphQL.newGraphQL(
                graphQLSchema).build();

        // ==========================================
        // CREATE HTTP SERVER
        // ==========================================

        HttpServer server = HttpServer.create(
                new InetSocketAddress(
                        4567),
                0);

        // ==========================================
        // HOME PAGE
        // ==========================================

        server.createContext(
                "/",
                exchange -> {

                    if (!exchange.getRequestMethod()
                            .equalsIgnoreCase("GET")) {

                        sendResponse(
                                exchange,
                                405,
                                "Method Not Allowed");

                        return;
                    }

                    String html = """
                            <!DOCTYPE html>
                            <html>

                            <head>
                                <title>Northstar GraphQL</title>

                                <style>
                                    body {
                                        font-family: Arial, sans-serif;
                                        margin: 40px;
                                    }

                                    h1 {
                                        color: #0F6A3D;
                                    }

                                    code {
                                        background: #f2f2f2;
                                        padding: 5px;
                                    }
                                </style>
                            </head>

                            <body>

                                <h1>
                                    Northstar GraphQL Inventory API
                                </h1>

                                <p>
                                    The GraphQL inventory server
                                    is running.
                                </p>

                                <h2>Endpoints</h2>

                                <p>
                                    Home:
                                    <code>GET /</code>
                                </p>

                                <p>
                                    GraphQL:
                                    <code>POST /graphql</code>
                                </p>

                                <h2>Queries</h2>

                                <ul>
                                    <li>products</li>
                                    <li>product(id)</li>
                                    <li>productsByCategory(category)</li>
                                </ul>

                                <h2>Mutations</h2>

                                <ul>
                                    <li>updateQuantity</li>
                                    <li>addProduct</li>
                                    <li>deleteProduct</li>
                                </ul>

                            </body>

                            </html>
                            """;

                    sendHtmlResponse(
                            exchange,
                            200,
                            html);
                });

        // ==========================================
        // GRAPHQL ENDPOINT
        // ==========================================

        server.createContext(
                "/graphql",
                exchange -> {

                    if (!exchange.getRequestMethod()
                            .equalsIgnoreCase("POST")) {

                        sendResponse(
                                exchange,
                                405,
                                "Only POST requests are allowed.");

                        return;
                    }

                    try {

                        String requestBody = new String(
                                exchange.getRequestBody()
                                        .readAllBytes(),
                                StandardCharsets.UTF_8);

                        // Parse JSON request
                        ObjectMapper mapper = new ObjectMapper();

                        Map<String, Object> body = mapper.readValue(
                                requestBody,
                                new TypeReference<Map<String, Object>>() {
                                });

                        // Get GraphQL query
                        Object queryObject = body.get("query");

                        if (queryObject == null) {

                            sendJsonResponse(
                                    exchange,
                                    400,
                                    Map.of(
                                            "error",
                                            "Missing 'query' field."));

                            return;
                        }

                        String query = queryObject.toString();

                        // Execute GraphQL
                        ExecutionResult result = graphQL.execute(query);

                        sendJsonResponse(
                                exchange,
                                200,
                                result.toSpecification());

                    } catch (Exception e) {

                        e.printStackTrace();

                        sendJsonResponse(
                                exchange,
                                400,
                                Map.of(
                                        "error",
                                        e.getMessage()));
                    }
                });

        // ==========================================
        // START SERVER
        // ==========================================

        server.start();

        System.out.println(
                "=================================");

        System.out.println(
                " Server started successfully!");

        System.out.println(
                " Open: http://localhost:4567");

        System.out.println(
                " GraphQL: http://localhost:4567/graphql");

        System.out.println(
                "=================================");

        System.out.println();

        System.out.println(
                "Keep this terminal running.");
    }

    // ==========================================
    // FIND PRODUCT
    // ==========================================

    private static Map<String, Object> findProduct(
            String id) {

        for (Map<String, Object> product : products) {

            if (product.get("id")
                    .toString()
                    .equals(id)) {

                return product;
            }
        }

        return null;
    }

    // ==========================================
    // LOAD INVENTORY
    // ==========================================

    private static List<Map<String, Object>> loadInventory() {

        try {

            ObjectMapper mapper = new ObjectMapper();

            File file = new File(INVENTORY_FILE);

            if (!file.exists()) {

                System.out.println(
                        "WARNING: inventory.json was not found.");

                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

        } catch (Exception e) {

            System.out.println(
                    "ERROR: Could not load inventory.json");

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    // ==========================================
    // SAVE INVENTORY
    // ==========================================

    private static void saveInventory() {

        try {

            ObjectMapper mapper = new ObjectMapper();

            File file = new File(INVENTORY_FILE);

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(
                            file,
                            products);

            System.out.println(
                    "Inventory saved successfully.");

        } catch (Exception e) {

            System.out.println(
                    "ERROR: Could not save inventory.json");

            e.printStackTrace();
        }
    }

    // ==========================================
    // SEND TEXT RESPONSE
    // ==========================================

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response) throws IOException {

        byte[] bytes = response.getBytes(
                StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/plain; charset=UTF-8");

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {

            output.write(bytes);
        }
    }

    // ==========================================
    // SEND HTML RESPONSE
    // ==========================================

    private static void sendHtmlResponse(
            HttpExchange exchange,
            int statusCode,
            String response) throws IOException {

        byte[] bytes = response.getBytes(
                StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {

            output.write(bytes);
        }
    }

    // ==========================================
    // SEND JSON RESPONSE
    // ==========================================

    private static void sendJsonResponse(
            HttpExchange exchange,
            int statusCode,
            Object response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(
                response);

        byte[] bytes = json.getBytes(
                StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {

            output.write(bytes);
        }
    }
}