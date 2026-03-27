//package Cart_Service.App.Service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Service
//public class CTService {
//
//    private final WebClient webClient;
//
//    public CTService(WebClient.Builder builder) {
//        this.webClient = builder
//                .baseUrl("https://api.us-east-2.aws.commercetools.com")
//                .build();
//    }
//
//    private final String TOKEN = "Bearer 0ylTCsGDqC-09OVMBVmJnLsG1hqhrShM";
//    private final String PROJECT_KEY = "/ecom-app-123456";
//
//
//    public String createCart() {
//
//        String body = """
//        {
//          "currency": "USD",
//          "taxMode": "ExternalAmount"
//        }
//        """;
//
//        return webClient.post()
//                .uri(PROJECT_KEY + "/carts")
//                .header("Authorization", TOKEN)
//                .bodyValue(body)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
//
//
//    public String addToCart(String cartId, int version, String productId, int quantity) {
//
////        String body = """
////        {
////          "version": %d,
////          "actions": [
////            {
////              "action": "addLineItem",
////              "productId": "%s",
////              "quantity": %d
////            }
////          ]
////        }
////        """.formatted(version, productId, quantity);
//
//        String body = """
//{
//  "version": %d,
//  "actions": [
//    {
//      "action": "addLineItem",
//      "productId": "%s",
//      "variantId": 1,
//      "quantity": %d
//    }
//  ]
//}
//""".formatted(version, productId, quantity);
//
//        return webClient.post()
//                .uri(PROJECT_KEY + "/carts/" + cartId)
//                .header("Authorization", TOKEN)
//                .bodyValue(body)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
//
//
//    public String createCartAndAddItem(String productId, int quantity) {
//
//        // 1. Create Cart
//        String cartResponse = createCart();
//
//        // 2. Extract values
//        String cartId = extractCartId(cartResponse);
//        int version = extractVersion(cartResponse);
//
//        // 3. Add item
//        return addToCart(cartId, version, productId, quantity);
//    }
//
//
//    private String extractCartId(String response) {
//        return response.split("\"id\":\"")[1].split("\"")[0];
//    }
//
//
//    private int extractVersion(String response) {
//        return Integer.parseInt(
//                response.split("\"version\":")[1].split(",")[0]
//        );
//    }
//}