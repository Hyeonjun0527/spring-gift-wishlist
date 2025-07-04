package gift.application.product;

import gift.common.pagination.Page;
import gift.common.pagination.Pageable;
import gift.domain.product.dto.ProductResponse;
import gift.domain.product.mapper.ProductMapper;
import gift.domain.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllProductsUseCase {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public GetAllProductsUseCase(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> execute(Pageable pageable) {
        return productService.getAllProducts(pageable).map(productMapper::toResponse);
    }
} 