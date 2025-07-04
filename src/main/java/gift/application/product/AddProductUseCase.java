package gift.application.product;

import gift.domain.product.dto.ProductRequest;
import gift.domain.product.dto.ProductResponse;
import gift.domain.product.mapper.ProductMapper;
import gift.domain.product.model.Product;
import gift.domain.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddProductUseCase {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public AddProductUseCase(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse execute(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productService.addProduct(product);
        return productMapper.toResponse(savedProduct);
    }
} 