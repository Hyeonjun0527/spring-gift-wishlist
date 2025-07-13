package gift.product.application.usecase;

import gift.common.annotation.UseCase;
import gift.common.pagination.Page;
import gift.common.pagination.Pageable;
import gift.product.adapter.web.mapper.ProductMapper;
import gift.product.application.port.in.ProductUseCase;
import gift.product.application.port.in.dto.ProductRequest;
import gift.product.application.port.in.dto.ProductResponse;
import gift.product.application.port.out.ProductPersistencePort;
import gift.product.domain.model.Product;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@UseCase
@Transactional
public class ProductInteractor implements ProductUseCase {

    private final ProductPersistencePort productPersistencePort;
    private final ProductMapper productMapper;

    public ProductInteractor(ProductPersistencePort productPersistencePort, ProductMapper productMapper) {
        this.productPersistencePort = productPersistencePort;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse addProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productPersistencePort.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProduct(Long id) {
        return productPersistencePort.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getProducts(Pageable pageable) {
        Page<Product> productPage = productPersistencePort.findPage(pageable);
        return productPage.map(productMapper::toResponse);
    }

    @Override
    public void updateProduct(Long id, ProductRequest request) {
        if (!productPersistencePort.existsById(id)) {
            throw new NoSuchElementException("업데이트할 상품을 찾을 수 없습니다. id: " + id);
        }
        Product product = productMapper.toEntity(id, request);
        productPersistencePort.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productPersistencePort.existsById(id)) {
            throw new NoSuchElementException("삭제할 상품을 찾을 수 없습니다. id: " + id);
        }
        productPersistencePort.deleteById(id);
    }
} 