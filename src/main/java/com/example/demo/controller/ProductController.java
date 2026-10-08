package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.service.ProductService;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =========================================================
    // 상품 목록 페이지
    // /product                    → 전체
    // /product?category=A00       → 카테고리(대·중·소)별
    // =========================================================
    @GetMapping
    public String list(
            @RequestParam(value = "category", required = false) String categoryCode,
            Model model
    ) {

        Map<String, Object> category =
                productService.getCategory(categoryCode);

        String topCode =
                productService.getTopCode(category);

        model.addAttribute("category", category);
        model.addAttribute("topCode", topCode);
        model.addAttribute("middleCode", productService.getMiddleCode(category));
        model.addAttribute("topCategories", productService.getTopCategories());
        model.addAttribute("middleCategories", productService.getMiddleCategories(topCode));
        model.addAttribute("products", productService.getProducts(category));

        return "product/list";
    }

    // =========================================================
    // 상품 상세 페이지
    // =========================================================
    @GetMapping("/{code}")
    public String detail(
            @PathVariable("code") String productCode,
            Model model
    ) {

        Map<String, Object> product =
                productService.getProduct(productCode);

        if (product == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "상품을 찾을 수 없습니다."
            );
        }

        model.addAttribute("product", product);
        model.addAttribute("images", productService.getImages(productCode));
        model.addAttribute("details", productService.getDetails(productCode));

        return "product/detail";
    }
}
