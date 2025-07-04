package gift.application.product;

import gift.common.pagination.Page;
import gift.common.pagination.Pageable;
import gift.domain.product.dto.ProductRequest;
import gift.domain.product.dto.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductApplicationService {

    private final AddProductUseCase addProductUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductApplicationService(AddProductUseCase addProductUseCase,
                                     GetAllProductsUseCase getAllProductsUseCase,
                                     GetProductUseCase getProductUseCase,
                                     UpdateProductUseCase updateProductUseCase,
                                     DeleteProductUseCase deleteProductUseCase) {
        this.addProductUseCase = addProductUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @Transactional
    public ProductResponse addProduct(ProductRequest request) {
        return addProductUseCase.execute(request);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return getAllProductsUseCase.execute(pageable);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return getProductUseCase.execute(id);
    }

    @Transactional
    public void updateProduct(Long id, ProductRequest request) {
        updateProductUseCase.execute(id, request);
    }

    @Transactional
    public void deleteProduct(Long id) {
        deleteProductUseCase.execute(id);
    }
} 