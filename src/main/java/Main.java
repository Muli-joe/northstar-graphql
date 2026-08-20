import graphql.GraphQL;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLArgument;
import graphql.Scalars;
import graphql.schema.DataFetcher;

import java.util.List;
import java.util.Map;

public class Main {

    static List<Map<String, Object>> inventory = List.of(
        Map.of(
            "id", "PROD001",
            "name", "Laptop",
            "quantity", 25
        ),
        Map.of(
            "id", "PROD002",
            "name", "Keyboard",
            "quantity", 10
        ),
        Map.of(
            "id", "PROD003",
            "name", "Mouse",
            "quantity", 0
        )
    );

    public static void main(String[] args) {

        GraphQLObjectType productType = GraphQLObjectType.newObject()
            .name("Product")

            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("id")
                .type(Scalars.GraphQLString)
                .build())

            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("name")
                .type(Scalars.GraphQLString)
                .build())

            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("quantity")
                .type(Scalars.GraphQLInt)
                .build())

            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("inStock")
                .type(Scalars.GraphQLBoolean)
                .dataFetcher(environment -> {
                    Map<String, Object> product = environment.getSource();

                    int quantity = (int) product.get("quantity");

                    return quantity > 0;
                })
                .build())

            .build();

        DataFetcher<Map<String, Object>> productFetcher = environment -> {

            String productId = environment.getArgument("id");

            return inventory.stream()
                .filter(product ->
                    product.get("id").equals(productId))
                .findFirst()
                .orElse(null);
        };

        GraphQLFieldDefinition productField =
            GraphQLFieldDefinition.newFieldDefinition()
                .name("product")
                .type(productType)
                .argument(GraphQLArgument.newArgument()
                    .name("id")
                    .type(Scalars.GraphQLString)
                    .build())
                .dataFetcher(environment -> productFetcher)
                .build();

        GraphQLObjectType queryType =
            GraphQLObjectType.newObject()
                .name("Query")
                .field(productField)
                .build();

        GraphQLSchema schema =
            GraphQLSchema.newSchema()
                .query(queryType)
                .build();

        GraphQL graphQL = GraphQL.newGraphQL(schema).build();

        String query = """
            {
                product(id: "PROD001") {
                    id
                    name
                    quantity
                    inStock
                }
            }
            """;

        var result = graphQL.execute(query);

        System.out.println("=== Northstar GraphQL Inventory Prototype ===");
        System.out.println();
        System.out.println("Query:");
        System.out.println(query);
        System.out.println("Result:");
        System.out.println(result.toSpecification());
    }
}