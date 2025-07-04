package gift.application.product;

import gift.domain.product.dto.ProductResponse;
import gift.domain.product.mapper.ProductMapper;
import gift.domain.product.model.Product;
import gift.domain.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetProductUseCase {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public GetProductUseCase(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public ProductResponse execute(Long id) {
        Product product = productService.getProductById(id);
        return productMapper.toResponse(product);
    }
} 