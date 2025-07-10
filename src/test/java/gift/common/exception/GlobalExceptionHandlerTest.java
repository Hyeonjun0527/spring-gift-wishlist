package gift.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.product.adapter.web.ProductController;
import gift.product.application.port.in.ProductUseCase;
import gift.product.application.port.in.dto.ProductRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ProductController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductUseCase productUseCase;

    @Test
    @DisplayName("MethodArgumentNotValidException - 검증 실패")
    void handleMethodArgumentNotValidException() throws Exception {
        // given
        ProductRequest invalidRequest = new ProductRequest(null, 0, null);

        // when
        MockHttpServletResponse response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("VALIDATION_FAILED");
        assertThat(response.getContentAsString()).contains("입력하신 정보를 다시 확인해주세요");
        assertThat(response.getContentAsString()).contains("/api/products");
    }
} 