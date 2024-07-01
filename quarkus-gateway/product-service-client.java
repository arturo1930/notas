package com.mycompany.product.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/products")
@RegisterRestClient(configKey="product-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ClientHeaderParam(name="Authorization", value="{generateAuthorizationHeader}")
public interface ProductServiceClient {

    @GET
    @Timeout(1000) // 1 segundo de timeout
    @CircuitBreaker(requestVolumeThreshold=4, failureRatio=0.5, delay=1000, successThreshold=3)
    List<ProductDTO> getAllProducts();

    @GET
    @Path("/{id}")
    @Timeout(500) // 500 ms de timeout
    @CircuitBreaker(requestVolumeThreshold=4, failureRatio=0.5, delay=1000, successThreshold=3)
    ProductDTO getProduct(@PathParam("id") Long id);

    @POST
    @Timeout(2000) // 2 segundos de timeout
    ProductDTO createProduct(ProductDTO productDTO);

    @PUT
    @Path("/{id}")
    @Timeout(2000) // 2 segundos de timeout
    ProductDTO updateProduct(@PathParam("id") Long id, ProductDTO productDTO);

    @DELETE
    @Path("/{id}")
    @Timeout(1000) // 1 segundo de timeout
    void deleteProduct(@PathParam("id") Long id);

    @GET
    @Path("/search")
    @Timeout(1500) // 1.5 segundos de timeout
    List<ProductDTO> searchProducts(@QueryParam("query") String query);

    // Método para generar el encabezado de autorización
    default String generateAuthorizationHeader() {
        // Aquí implementarías la lógica para obtener y devolver el token JWT
        // Por ejemplo:
        // return "Bearer " + JwtTokenProvider.getToken();
        return "Bearer dummy-jwt-token";
    }
}

// DTO para los productos
class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;

    // Constructores, getters y setters
}
