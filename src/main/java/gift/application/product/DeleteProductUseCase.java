package gift.application.product;

import gift.domain.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteProductUseCase {

    private final ProductService productService;

    public DeleteProductUseCase(ProductService productService) {
        this.productService = productService;
    }

    @Transactional
    public void execute(Long id) {
        productService.deleteProduct(id);
    }
} 