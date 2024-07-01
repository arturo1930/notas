// Asumiendo que ya has generado un proyecto Quarkus JHipster, 
// modifica el archivo application.yml para incluir la configuración del gateway:

quarkus:
  http:
    port: 8080
  oidc:
    auth-server-url: http://localhost:8180/auth/realms/jhipster
    client-id: web_app
    credentials:
      secret: web_app_secret
  jhipster:
    gateway:
      rate-limiting:
        enabled: false
      authorized-microservices-endpoints:
        product-service: /api/products/**

  rest-client:
    "com.mycompany.product.client.ProductServiceClient":
      url: http://localhost:8081
      scope: javax.inject.Singleton

mp:
  jwt:
    verify:
      publickey:
        location: META-INF/resources/publicKey.pem

// Crea una interfaz para el cliente del servicio de productos
package com.mycompany.product.client;

import javax.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/products")
@RegisterRestClient
public interface ProductServiceClient {
    
    @GET
    String getAllProducts();
    
    @GET
    @Path("/{id}")
    String getProduct(@PathParam("id") Long id);
}

// Crea un recurso para el gateway
package com.mycompany.gateway.web.rest;

import javax.inject.Inject;
import javax.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import com.mycompany.product.client.ProductServiceClient;

@Path("/api/products")
public class ProductResource {
    
    @Inject
    @RestClient
    ProductServiceClient productServiceClient;
    
    @GET
    public String getAllProducts() {
        return productServiceClient.getAllProducts();
    }
    
    @GET
    @Path("/{id}")
    public String getProduct(@PathParam("id") Long id) {
        return productServiceClient.getProduct(id);
    }
}
