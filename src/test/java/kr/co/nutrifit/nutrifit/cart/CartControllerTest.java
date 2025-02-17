package kr.co.nutrifit.nutrifit.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserAdapter userAdapter;

    @Test
    void addItemToCart_whenValidRequest_shouldReturn201() throws Exception {
        Long productId = 1L;
        int quantity = 2;

        mockMvc.perform(post("/api/cart/items")
                        .param("productId", String.valueOf(productId))
                        .param("quantity", String.valueOf(quantity))
                        .with(user(userAdapter)))
                .andExpect(status().isCreated())
                .andExpect(content().string("상품이 장바구니에 추가되었습니다."));
    }

    @Test
    void addItemToCart_whenInvalidQuantity_shouldReturn400() throws Exception {
        Long productId = 1L;
        int quantity = 0;

        mockMvc.perform(post("/api/cart/items")
                        .param("productId", String.valueOf(productId))
                        .param("quantity", String.valueOf(quantity))
                        .with(user(userAdapter)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("수량은 1 이상이어야 합니다."));
    }

    @Test
    void getCartItems_shouldReturnListOfItems() throws Exception {

        mockMvc.perform(get("/api/cart/items")
                        .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product1"))
                .andExpect(jsonPath("$[1].name").value("Product2"));
    }

    @Test
    void updateCartItemQuantity_whenValidRequest_shouldReturn200() throws Exception {
        Long productId = 1L;
        int quantity = 3;

        mockMvc.perform(put("/api/cart/items")
                        .param("productId", String.valueOf(productId))
                        .param("quantity", String.valueOf(quantity))
                        .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(content().string("아이템 수량이 변경되었습니다."));
    }

    @Test
    void removeItemFromCart_shouldReturn204() throws Exception {
        Long productId = 1L;

        mockMvc.perform(delete("/api/cart/items/{productId}", productId)
                        .with(user(userAdapter)))
                .andExpect(status().isNoContent());
    }

    @Test
    void clearCart_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/cart/items")
                        .with(user(userAdapter)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeCart_whenValidRequest_shouldReturn200() throws Exception {
    }
}
