package kr.co.nutrifit.nutrifit.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.controllers.ProductController;
import kr.co.nutrifit.nutrifit.backend.dto.ProductDto;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private User admin;
    private ProductDto productDto;
    private Product product;
    private UserAdapter userAdapter;
    private UserAdapter adminAdapter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        admin = User.builder()
                .username("adminuser")
                .email("adminuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_ADMIN)
                .build();

        user = userRepository.save(user);
        admin = userRepository.save(admin);

        product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .stockQuantity(100)
                .imageUrl("http://example.com/image.png")
                .category("Test Category")
                .build();

        productDto = ProductDto.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .stockQuantity(100)
                .imageUrl("http://example.com/image.png")
                .category("Test Category")
                .build();

        product = productRepository.save(product);
        productDto.setId(product.getId());

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        userAdapter = new UserAdapter(user);
        adminAdapter = new UserAdapter(admin);
        Authentication authentication = new UsernamePasswordAuthenticationToken(adminAdapter, null, adminAdapter.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void addProduct_ShouldReturnCreated_ForAdmin() throws Exception {
        ProductDto productDto = ProductDto.builder()
                .name("Test Product2")
                .description("Test Description")
                .price(1000L)
                .stockQuantity(100)
                .imageUrl("http://example.com/image.png")
                .category("Test Category")
                .build();
        mockMvc.perform(post("/api/products/admin")
                        .with(SecurityMockMvcRequestPostProcessors.user(adminAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void addProduct_ShouldReturnForbidden_ForNonAdmin() throws Exception {
        mockMvc.perform(post("/api/products/admin")
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProduct_ShouldReturnOk_ForAdmin() throws Exception {
        mockMvc.perform(put("/api/products/admin")
                        .with(SecurityMockMvcRequestPostProcessors.user(adminAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct_ShouldReturnForbidden_ForNonAdmin() throws Exception {
        mockMvc.perform(put("/api/products/admin")
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_ShouldReturnNoContent_ForAdmin() throws Exception {
        mockMvc.perform(delete("/api/products/admin/{id}", productDto.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(adminAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_ShouldReturnForbidden_ForNonAdmin() throws Exception {
        mockMvc.perform(delete("/api/products/admin/{id}", productDto.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllProducts_ShouldReturnProductList() throws Exception {
        List<ProductDto> productList = new ArrayList<>();
        productList.add(productDto);

        mockMvc.perform(get("/api/products")
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/{id}", productDto.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProductsByCategory_ShouldReturnProductList() throws Exception {
        List<ProductDto> productList = new ArrayList<>();
        productList.add(productDto);

        mockMvc.perform(get("/api/products/category/{category}", "Test Category")
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
}

