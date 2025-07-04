package gift.application.product;

import gift.domain.product.dto.ProductRequest;
import gift.domain.product.mapper.ProductMapper;
import gift.domain.product.model.Product;
import gift.domain.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProductUseCase {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public UpdateProductUseCase(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Transactional
    public void execute(Long id, ProductRequest request) {
        Product product = productMapper.toEntity(id, request);
        productService.updateProduct(product);
    }
} 