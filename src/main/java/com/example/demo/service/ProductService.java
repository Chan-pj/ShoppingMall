package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // =========================================================
    // 상품 목록
    // categoryCode가 없거나 존재하지 않으면 전체 목록
    // =========================================================
    public List<Map<String, Object>> getProducts(
            Map<String, Object> category
    ) {

        if (category == null) {

            return productRepository.findProducts();
        }

        return productRepository.findProductsByCategory(
                (String) category.get("SP_CI_Code")
        );
    }

    // =========================================================
    // 카테고리 1건 (없으면 null)
    // =========================================================
    public Map<String, Object> getCategory(
            String categoryCode
    ) {

        if (categoryCode == null || categoryCode.isBlank()) {

            return null;
        }

        return productRepository.findCategory(categoryCode);
    }

    // =========================================================
    // 대분류 목록
    // =========================================================
    public List<Map<String, Object>> getTopCategories() {

        return productRepository.findTopCategories();
    }

    // =========================================================
    // 선택한 카테고리의 대분류 코드
    // 대분류면 자기 자신, 중·소분류면 상위코드 1
    // =========================================================
    public String getTopCode(
            Map<String, Object> category
    ) {

        if (category == null) {

            return null;
        }

        Object topLayer = category.get("SP_CI_TopLayer");

        return topLayer != null
                ? (String) topLayer
                : (String) category.get("SP_CI_Code");
    }

    // =========================================================
    // 선택한 카테고리의 중분류 코드
    // 대분류면 null, 중분류면 자기 자신, 소분류면 상위코드 2
    // =========================================================
    public String getMiddleCode(
            Map<String, Object> category
    ) {

        if (category == null) {

            return null;
        }

        int level = ((Number) category.get("SP_CI_Level")).intValue();

        if (level == 2) {

            return (String) category.get("SP_CI_Code");
        }

        if (level == 3) {

            return (String) category.get("SP_CI_BottomLayer");
        }

        return null;
    }

    // =========================================================
    // 대분류 아래 중분류 목록
    // =========================================================
    public List<Map<String, Object>> getMiddleCategories(
            String topCode
    ) {

        if (topCode == null) {

            return List.of();
        }

        return productRepository.findMiddleCategories(topCode);
    }

    // =========================================================
    // 상품 상세 (없거나 판매 중지면 null)
    // =========================================================
    public Map<String, Object> getProduct(
            String productCode
    ) {

        return productRepository.findProduct(productCode);
    }

    // =========================================================
    // 상품 이미지 목록
    // =========================================================
    public List<Map<String, Object>> getImages(
            String productCode
    ) {

        return productRepository.findImages(productCode);
    }

    // =========================================================
    // 상품 상세정보 목록
    // =========================================================
    public List<Map<String, Object>> getDetails(
            String productCode
    ) {

        return productRepository.findDetails(productCode);
    }
}
