package gift.product.application.port.in;

import gift.common.pagination.Page;
import gift.common.pagination.Pageable;
import gift.product.application.port.in.dto.ProductRequest;
import gift.product.application.port.in.dto.ProductResponse;

public interface ProductUseCase {
    ProductResponse addProduct(ProductRequest request);
    ProductResponse getProduct(Long id);
    Page<ProductResponse> getProducts(Pageable pageable);
    void updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
} 