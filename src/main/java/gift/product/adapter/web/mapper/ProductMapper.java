package gift.product.adapter.web.mapper;

import gift.product.application.port.in.dto.ProductRequest;
import gift.product.application.port.in.dto.ProductResponse;
import gift.product.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return Product.of(null, request.name(), request.price(), request.imageUrl());
    }

    public Product toEntity(Long id, ProductRequest request) {
        return Product.of(id, request.name(), request.price(), request.imageUrl());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
    }
} 