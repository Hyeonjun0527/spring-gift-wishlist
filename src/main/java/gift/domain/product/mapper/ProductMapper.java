package gift.domain.product.mapper;

import gift.domain.product.dto.ProductRequest;
import gift.domain.product.dto.ProductResponse;
import gift.domain.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest productRequest) {
        return toEntity(null, productRequest);
    }

    public Product toEntity(Long id, ProductRequest productRequest) {
        return Product.of(
                id,
                productRequest.name(),
                productRequest.price(),
                productRequest.imageUrl()
        );
    }

    /* Product → ProductResponse (조회·수정 후) */
    public ProductResponse toResponse(Product product) {
        return ProductResponse.of(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
    }

}
