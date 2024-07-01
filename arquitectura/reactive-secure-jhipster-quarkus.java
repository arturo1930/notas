// Gateway modifications (application.yml)
quarkus:
  http:
    port: 8080
  oidc:
    auth-server-url: http://localhost:8180/auth/realms/jhipster
    client-id: web_app
    credentials:
      secret: ${OIDC_SECRET:web_app_secret}
    tls:
      verification: required
  cache:
    caffeine:
      "products":
        expire-after-write: 60S
  redis:
    hosts: redis://localhost:6379
  reactive-routes:
    enable: true

// ProductResource in Gateway
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ProductResource {
    
    @Inject
    ReactiveProductServiceClient productClient;
    
    @Inject
    CacheManager cacheManager;
    
    @GET
    @RateLimit(limit = 100, period = "1m")
    public Uni<List<ProductDTO>> getAllProducts() {
        return Uni.createFrom().item(() -> cacheManager.getCache("products").get("all", List.class))
            .onItem().ifNull().switchTo(() -> productClient.getAllProducts()
                .onItem().invoke(products -> cacheManager.getCache("products").put("all", products)));
    }
    
    @GET
    @Path("/{id}")
    public Uni<ProductDTO> getProduct(@PathParam("id") Long id) {
        return productClient.getProduct(id);
    }

    @POST
    @RolesAllowed("ROLE_ADMIN")
    public Uni<ProductDTO> createProduct(ProductDTO productDTO) {
        return productClient.createProduct(productDTO);
    }
}

// ReactiveProductServiceClient interface
@RegisterRestClient(configKey="product-api")
@ClientHeaderParam(name="Authorization", value="{generateAuthorizationHeader}")
public interface ReactiveProductServiceClient {
    
    @GET
    Uni<List<ProductDTO>> getAllProducts();
    
    @GET
    @Path("/{id}")
    Uni<ProductDTO> getProduct(@PathParam("id") Long id);
    
    @POST
    Uni<ProductDTO> createProduct(ProductDTO productDTO);
    
    // Other methods...
    
    default String generateAuthorizationHeader() {
        // Implement secure token generation
        return "Bearer " + JwtTokenProvider.generateToken();
    }
}

// Product Microservice modifications (application.yml)
quarkus:
  http:
    port: 8081
  datasource:
    reactive:
      url: vertx-reactive:postgresql://localhost:5432/productdb
  hibernate-orm:
    database:
      generation: update
  oidc:
    auth-server-url: http://localhost:8180/auth/realms/jhipster
    client-id: product-service
    credentials:
      secret: ${OIDC_SECRET:product_service_secret}
    tls:
      verification: required

// ReactiveProductResource in Product Microservice
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ReactiveProductResource {
    
    @Inject
    ReactiveProductRepository productRepository;
    
    @GET
    public Uni<List<Product>> getAllProducts() {
        return productRepository.listAll();
    }
    
    @GET
    @Path("/{id}")
    public Uni<Product> getProduct(@PathParam("id") Long id) {
        return productRepository.findById(id);
    }
    
    @POST
    @RolesAllowed("ROLE_ADMIN")
    @ValidatePayload
    public Uni<Product> createProduct(@Valid ProductDTO productDTO) {
        Product product = mapToEntity(productDTO);
        return productRepository.persist(product);
    }
    
    // Other methods...
}

// ReactiveProductRepository
@ApplicationScoped
public class ReactiveProductRepository implements PanacheRepository<Product> {
    // Panache provides reactive methods out of the box
}
