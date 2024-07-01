// Configuración del microservicio de productos (application.yml)
quarkus:
  http:
    port: 8081
  oidc:
    auth-server-url: http://localhost:8180/auth/realms/jhipster
    client-id: internal
    credentials:
      secret: internal_secret

// Entidad de Producto
package com.mycompany.product.domain;

import javax.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    
    // Getters y setters
}

// Repositorio de Producto
package com.mycompany.product.repository;

import com.mycompany.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

// Recurso de Producto
package com.mycompany.product.web.rest;

import com.mycompany.product.domain.Product;
import com.mycompany.product.repository.ProductRepository;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductRepository productRepository;

    @GET
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Product getProduct(@PathParam("id") Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}
