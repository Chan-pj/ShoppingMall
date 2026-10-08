package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // 아이디 중복 확인
    // =========================================================
    public boolean existsId(String id) {

        String sql = """
                SELECT COUNT(*)
                FROM SP_User_Info
                WHERE SP_UI_ID = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                id
        );

        return count != null && count > 0;
    }

    // =========================================================
    // 전화번호 중복 확인
    // =========================================================
    public boolean existsPhone(String phone) {

        String sql = """
                SELECT COUNT(*)
                FROM SP_User_Info
                WHERE SP_UI_PhoneNum = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                phone
        );

        return count != null && count > 0;
    }

    // =========================================================
    // 회원번호 중복 확인
    // =========================================================
    public boolean existsUserNumber(String userNumber) {

        String sql = """
                SELECT COUNT(*)
                FROM SP_User_Info
                WHERE SP_UI_No = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userNumber
        );

        return count != null && count > 0;
    }

    // =========================================================
    // 회원가입 완료 후 SP_User_Info 저장
    // =========================================================
    public void insertUser(
            String name,
            String id,
            String phone,
            String birth,
            String email,
            String userNumber
    ) {

        String sql = """
                INSERT INTO SP_User_Info
                (
                    SP_UI_Name,
                    SP_UI_ID,
                    SP_UI_PhoneNum,
                    SP_UI_Birth,
                    SP_UI_Rdate,
                    SP_UI_Email,
                    SP_UI_No
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    ?,
                    CONVERT(DATE, GETDATE()),
                    ?,
                    ?
                )
                """;

        jdbcTemplate.update(
                sql,
                name,
                id,
                phone,
                java.sql.Date.valueOf(birth),
                email,
                userNumber
        );
    }

    // =========================================================
    // 아이디로 회원 조회
    // =========================================================
    public Map<String, Object> findById(String id) {

        String sql = """
                SELECT
                    SP_UI_Name,
                    SP_UI_ID,
                    SP_UI_PhoneNum,
                    SP_UI_Birth,
                    SP_UI_Rdate,
                    SP_UI_Email,
                    SP_UI_No
                FROM SP_User_Info
                WHERE SP_UI_ID = ?
                """;

        try {

            return jdbcTemplate.queryForMap(
                    sql,
                    id
            );

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }

    // =========================================================
    // 아이디 + 전화번호로 회원 확인
    // 로그인 1단계
    // =========================================================
    public Map<String, Object> findByIdAndPhone(
            String id,
            String phone
    ) {

        String sql = """
                SELECT
                    SP_UI_Name,
                    SP_UI_ID,
                    SP_UI_PhoneNum,
                    SP_UI_Birth,
                    SP_UI_Rdate,
                    SP_UI_Email,
                    SP_UI_No
                FROM SP_User_Info
                WHERE SP_UI_ID = ?
                AND SP_UI_PhoneNum = ?
                """;

        try {

            return jdbcTemplate.queryForMap(
                    sql,
                    id,
                    phone
            );

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }

    // =========================================================
    // 회원번호로 회원 조회
    // =========================================================
    public Map<String, Object> findByUserNumber(
            String userNumber
    ) {

        String sql = """
                SELECT
                    SP_UI_Name,
                    SP_UI_ID,
                    SP_UI_PhoneNum,
                    SP_UI_Birth,
                    SP_UI_Rdate,
                    SP_UI_Email,
                    SP_UI_No
                FROM SP_User_Info
                WHERE SP_UI_No = ?
                """;

        try {

            return jdbcTemplate.queryForMap(
                    sql,
                    userNumber
            );

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }

    // =========================================================
    // 기존 인증번호 비활성화
    // 같은 전화번호 + 같은 인증 종류
    // =========================================================
    public void disablePreviousVerification(
            String phone,
            String type
    ) {

        String sql = """
                UPDATE SP_Auth_Info
                SET SP_AI_Status = 'N'
                WHERE SP_UI_PhoneNum = ?
                AND SP_AI_Saparator = ?
                AND SP_AI_Status = 'Y'
                """;

        jdbcTemplate.update(
                sql,
                phone,
                type
        );
    }

    // =========================================================
    // 인증번호 저장
    // =========================================================
    public void insertVerificationCode(
            String type,
            String phone,
            String code,
            LocalDateTime time,
            String verificationNumber
    ) {

        String sql = """
                INSERT INTO SP_Auth_Info
                (
                    SP_AI_Code,
                    SP_AI_Saparator,
                    SP_UI_PhoneNum,
                    SP_AI_AuthNum,
                    SP_AI_Rdate,
                    SP_AI_Status
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    'Y'
                )
                """;

        jdbcTemplate.update(
                sql,
                verificationNumber,
                type,
                phone,
                code,
                time
        );
    }

    // =========================================================
    // 인증번호 조회
    // =========================================================
    public Map<String, Object> findVerificationCode(
            String phone,
            String type,
            String code
    ) {

        String sql = """
                SELECT TOP 1
                    SP_AI_Code,
                    SP_AI_Saparator,
                    SP_UI_PhoneNum,
                    SP_AI_AuthNum,
                    SP_AI_Rdate,
                    SP_AI_Status
                FROM SP_Auth_Info
                WHERE SP_UI_PhoneNum = ?
                AND SP_AI_Saparator = ?
                AND SP_AI_AuthNum = ?
                AND SP_AI_Status = 'Y'
                ORDER BY SP_AI_Rdate DESC
                """;

        try {

            return jdbcTemplate.queryForMap(
                    sql,
                    phone,
                    type,
                    code
            );

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }

    // =========================================================
    // 인증번호 사용 완료
    // =========================================================
    public void disableVerification(
            String verificationNumber
    ) {

        String sql = """
                UPDATE SP_Auth_Info
                SET SP_AI_Status = 'N'
                WHERE SP_AI_Code = ?
                """;

        jdbcTemplate.update(
                sql,
                verificationNumber
        );
    }
}