package com.example.demo.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 목록 공통 SELECT
    // 대표 이미지 = 이미지 코드가 가장 작은 이미지
    private static final String PRODUCT_LIST_SQL = """
            SELECT
                p.SP_PI_Code,
                p.SP_PI_Name,
                p.SP_PI_Price,
                p.SP_PI_Count,
                c.SP_CI_Name,
                img.SP_II_Name
            FROM SP_Product_Info p
            JOIN SP_Category_Info c
                ON c.SP_CI_Code = p.SP_CI_Code
            OUTER APPLY (
                SELECT TOP 1 i.SP_II_Name
                FROM SP_Image_Info i
                WHERE i.SP_PI_Code = p.SP_PI_Code
                ORDER BY i.SP_II_Code
            ) img
            WHERE p.SP_PI_Status = 'Y'
            AND c.SP_CI_Status = 'Y'
            """;

    private static final String PRODUCT_LIST_ORDER = """
            ORDER BY p.SP_PI_Rdate DESC, p.SP_PI_Code DESC
            """;

    // =========================================================
    // 판매 중인 전체 상품 목록
    // =========================================================
    public List<Map<String, Object>> findProducts() {

        return jdbcTemplate.queryForList(
                PRODUCT_LIST_SQL + PRODUCT_LIST_ORDER
        );
    }

    // =========================================================
    // 카테고리별 상품 목록
    // 대·중·소 어느 코드를 넘겨도 그 아래 상품이 모두 조회됨
    // (상품은 소분류에 연결, 소분류는 상위코드 1·2를 가짐)
    // =========================================================
    public List<Map<String, Object>> findProductsByCategory(
            String categoryCode
    ) {

        String sql = PRODUCT_LIST_SQL + """
                AND (
                    p.SP_CI_Code = ?
                    OR c.SP_CI_TopLayer = ?
                    OR c.SP_CI_BottomLayer = ?
                )
                """ + PRODUCT_LIST_ORDER;

        return jdbcTemplate.queryForList(
                sql,
                categoryCode,
                categoryCode,
                categoryCode
        );
    }

    // =========================================================
    // 상품 1건 + 카테고리 경로(대 > 중 > 소)
    // =========================================================
    public Map<String, Object> findProduct(
            String productCode
    ) {

        String sql = """
                SELECT
                    p.SP_PI_Code,
                    p.SP_PI_Name,
                    p.SP_PI_Price,
                    p.SP_PI_Count,
                    p.SP_PI_Supply,
                    c.SP_CI_Code,
                    c.SP_CI_Name,
                    top1.SP_CI_Code AS TOP_Code,
                    top1.SP_CI_Name AS TOP_Name,
                    mid.SP_CI_Code AS MID_Code,
                    mid.SP_CI_Name AS MID_Name
                FROM SP_Product_Info p
                JOIN SP_Category_Info c
                    ON c.SP_CI_Code = p.SP_CI_Code
                LEFT JOIN SP_Category_Info top1
                    ON top1.SP_CI_Code = c.SP_CI_TopLayer
                LEFT JOIN SP_Category_Info mid
                    ON mid.SP_CI_Code = c.SP_CI_BottomLayer
                WHERE p.SP_PI_Code = ?
                AND p.SP_PI_Status = 'Y'
                """;

        try {

            return jdbcTemplate.queryForMap(
                    sql,
                    productCode
            );

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }

    // =========================================================
    // 상품 이미지 목록
    // =========================================================
    public List<Map<String, Object>> findImages(
            String productCode
    ) {

        String sql = """
                SELECT SP_II_Code, SP_II_Name
                FROM SP_Image_Info
                WHERE SP_PI_Code = ?
                ORDER BY SP_II_Code
                """;

        return jdbcTemplate.queryForList(
                sql,
                productCode
        );
    }

    // =========================================================
    // 상품 상세정보 목록 (항목명 / 항목값)
    // =========================================================
    public List<Map<String, Object>> findDetails(
            String productCode
    ) {

        String sql = """
                SELECT SP_DI_Name, SP_DI_Value
                FROM SP_Detail_Info
                WHERE SP_PI_Code = ?
                ORDER BY SP_DI_Code
                """;

        return jdbcTemplate.queryForList(
                sql,
                productCode
        );
    }

    // =========================================================
    // 사용 중인 대분류 목록
    // =========================================================
    public List<Map<String, Object>> findTopCategories() {

        String sql = """
                SELECT SP_CI_Code, SP_CI_Name
                FROM SP_Category_Info
                WHERE SP_CI_Level = 1
                AND SP_CI_Status = 'Y'
                ORDER BY SP_CI_Code
                """;

        return jdbcTemplate.queryForList(sql);
    }

    // =========================================================
    // 대분류 아래 중분류 목록
    // =========================================================
    public List<Map<String, Object>> findMiddleCategories(
            String topCode
    ) {

        String sql = """
                SELECT SP_CI_Code, SP_CI_Name
                FROM SP_Category_Info
                WHERE SP_CI_Level = 2
                AND SP_CI_TopLayer = ?
                AND SP_CI_Status = 'Y'
                ORDER BY SP_CI_Code
                """;

        return jdbcTemplate.queryForList(
                sql,
                topCode
        );
    }

    // =========================================================
    // 카테고리 1건
    // =========================================================
    public Map<String, Object> findCategory(
            String categoryCode
    ) {

        String sql = """
                SELECT
                    SP_CI_Code,
                    SP_CI_Name,
                    SP_CI_Level,
                    SP_CI_TopLayer,
                    SP_CI_BottomLayer
                FROM SP_Category_Info
                WHERE SP_CI_Code = ?
                AND SP_CI_Status = 'Y'
                """;

        try {

            return jdbcTemplate.queryForMap(
                    sql,
                    categoryCode
            );

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }
}
