package gift.product.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.common.pagination.Page;
import gift.common.pagination.PageImpl;
import gift.common.pagination.Pageable;
import gift.product.application.port.in.ProductUseCase;
import gift.product.application.port.in.dto.ProductRequest;
import gift.product.application.port.in.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductUseCase productUseCase;

    @Test
    @DisplayName("페이지 상품 조회")
    void getAllProducts() throws Exception {
        // given
        ProductResponse product1 = new ProductResponse(1L, "상품1", 1000, "image1.jpg");
        ProductResponse product2 = new ProductResponse(2L, "상품2", 2000, "image2.jpg");
        
        Page<ProductResponse> productPage = new PageImpl<>(
            List.of(product1, product2), 
            Pageable.of(0, 20), 
            1000
        );
        given(productUseCase.getProducts(any(Pageable.class))).willReturn(productPage);

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        
        // 응답 내용에 상품 데이터가 포함되어 있는지 확인
        String responseContent = response.getContentAsString();
        assertThat(responseContent).contains("상품1");
        assertThat(responseContent).contains("상품2");
        assertThat(responseContent).contains("1000");
    }

    @Test
    @DisplayName("ID로 상품 조회")
    void getProductById() throws Exception {
        // given
        Long productId = 1L;
        ProductResponse productResponse = new ProductResponse(productId, "Test Product", 100, "test.jpg");
        given(productUseCase.getProduct(productId)).willReturn(productResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/api/products/{id}", productId))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        ProductResponse result = objectMapper.readValue(response.getContentAsString(), ProductResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.id()).isEqualTo(productId);
    }

    @Test
    @DisplayName("상품 추가")
    void addProduct() throws Exception {
        // given
        ProductRequest request = new ProductRequest("New Product", 100, "new.jpg");
        ProductResponse responseDto = new ProductResponse(1L, "New Product", 100, "new.jpg");
        given(productUseCase.addProduct(any(ProductRequest.class))).willReturn(responseDto);

        // when
        MockHttpServletResponse response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).isEqualTo("/api/products/1");
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() throws Exception {
        // given
        Long productId = 1L;
        ProductRequest request = new ProductRequest("Updated Product", 150, "updated.jpg");

        // when
        MockHttpServletResponse response = mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(productUseCase).updateProduct(productId, request);
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteProduct() throws Exception {
        // given
        Long productId = 1L;

        // when
        MockHttpServletResponse response = mockMvc.perform(delete("/api/products/{id}", productId))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(productUseCase).deleteProduct(productId);
    }
}