package kr.co.nutrifit.nutrifit.product;

import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.ProductDto;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description")
                .stockQuantity(10)
                .category("Category")
                .lowStockThreshold(5)
                .build();

        productDto = ProductDto.builder()
                .id(1L)
                .name("Product 1")
                .build();
    }

    @Test
    void addProduct_ShouldSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.addProduct(productDto);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldUpdateExistingProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productDto.setName("Updated Name");
        productService.updateProduct(productDto);

        verify(productRepository, times(1)).save(product);
        assertEquals("Updated Name", product.getName());
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(productDto));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(product.getId()));
    }

    @Test
    void reduceStock_ShouldReduceStockAndSaveProducts() {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(product.getId());
        orderItemDto.setQuantity(5);
        List<OrderItemDto> orderItems = List.of(orderItemDto);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.reduceStock(orderItems);

        verify(productRepository, times(1)).saveAll(anyList());
        assertEquals(5, product.getStockQuantity());
    }

    @Test
    void reduceStock_ShouldThrowException_WhenStockInsufficient() {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(product.getId());
        orderItemDto.setQuantity(15);
        List<OrderItemDto> orderItems = List.of(orderItemDto);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> productService.reduceStock(orderItems));
    }

    @Test
    void getAllProduct_ShouldReturnProductDtoList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDto> productDtos = productService.getAllProduct();

        assertEquals(1, productDtos.size());
        assertEquals(product.getName(), productDtos.get(0).getName());
    }

    @Test
    void getProductById_ShouldReturnProductDto() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductDto foundProduct = productService.getProductById(product.getId());

        assertEquals(product.getName(), foundProduct.getName());
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.getProductById(product.getId()));
    }

    @Test
    void getProductsByCategory_ShouldReturnProductDtoList() {
        when(productRepository.findByCategory(product.getCategory())).thenReturn(List.of(product));

        List<ProductDto> productDtos = productService.getProductsByCategory(product.getCategory());

        assertEquals(1, productDtos.size());
        assertEquals(product.getCategory(), productDtos.get(0).getCategory());
    }
}
