package com.example.demo.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================================================
    // 6자리 인증번호 생성
    // =========================================================
    private String generateVerificationCode() {

        int number =
                ThreadLocalRandom.current()
                        .nextInt(100000, 1000000);

        return String.valueOf(number);
    }

    // =========================================================
    // 회원번호 생성
    // U_ + 18자리 UUID
    // =========================================================
    private String generateUserNumber() {

        String userNumber;

        do {

            userNumber =
                    "U_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 18);

        } while (
                userRepository.existsUserNumber(
                        userNumber
                )
        );

        return userNumber;
    }

    // =========================================================
    // 인증번호 레코드 번호 생성
    // =========================================================
    private String generateVerificationNumber() {

        return "V_" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 18);
    }

    // =========================================================
    // 회원가입 정보 유효성 확인
    //
    // 중요:
    // 여기서는 SP_User_Info에 INSERT하지 않는다.
    // =========================================================
    public void checkRegisterInfo(
            String id,
            String phone
    ) {

        if (userRepository.existsId(id)) {

            throw new RuntimeException(
                    "이미 사용 중인 아이디입니다."
            );
        }

        if (userRepository.existsPhone(phone)) {

            throw new RuntimeException(
                    "이미 등록된 전화번호입니다."
            );
        }
    }

    // =========================================================
    // 회원번호 생성
    // =========================================================
    public String createUserNumber() {

        return generateUserNumber();
    }

    // =========================================================
    // 인증번호 생성
    // =========================================================
    public String createVerificationCode(
            String phone,
            String type
    ) {

        userRepository.disablePreviousVerification(
                phone,
                type
        );

        String code =
                generateVerificationCode();

        String verificationNumber =
                generateVerificationNumber();

        LocalDateTime now =
                LocalDateTime.now();

        userRepository.insertVerificationCode(
                type,
                phone,
                code,
                now,
                verificationNumber
        );

        return code;
    }

    // =========================================================
    // 인증번호 확인
    // =========================================================
    public boolean verifyCode(
            String phone,
            String type,
            String code
    ) {

        Map<String, Object> verification =
                userRepository.findVerificationCode(
                        phone,
                        type,
                        code
                );

        if (verification == null) {

            return false;
        }

        Object timeObject =
                verification.get("SP_AI_Rdate");

        LocalDateTime verificationTime;

        if (timeObject instanceof Timestamp timestamp) {

            verificationTime =
                    timestamp.toLocalDateTime();

        } else if (
                timeObject instanceof LocalDateTime localDateTime
        ) {

            verificationTime =
                    localDateTime;

        } else {

            throw new RuntimeException(
                    "SP_AI_Rdate 형식을 확인하세요."
            );
        }

        // 3분 제한
        LocalDateTime expireTime =
                verificationTime.plusMinutes(3);

        // 만료
        if (
                LocalDateTime.now()
                        .isAfter(expireTime)
        ) {

            userRepository.disableVerification(
                    (String)
                            verification.get(
                                    "SP_AI_Code"
                            )
            );

            return false;
        }

        // 인증 성공 → 사용 완료
        userRepository.disableVerification(
                (String)
                        verification.get(
                                "SP_AI_Code"
                        )
        );

        return true;
    }

    // =========================================================
    // 회원가입 완료
    //
    // 인증번호가 정상적으로 확인된 후에만 호출
    // =========================================================
    public String completeRegister(
            String name,
            String id,
            String phone,
            String birth,
            String email
    ) {

        if (userRepository.existsId(id)) {

            throw new RuntimeException(
                    "이미 사용 중인 아이디입니다."
            );
        }

        if (userRepository.existsPhone(phone)) {

            throw new RuntimeException(
                    "이미 등록된 전화번호입니다."
            );
        }

        String userNumber =
                generateUserNumber();

        userRepository.insertUser(
                name,
                id,
                phone,
                birth,
                email,
                userNumber
        );

        return userNumber;
    }

    // =========================================================
    // 아이디 + 전화번호 확인
    // 로그인 1단계
    // =========================================================
    public Map<String, Object> findUserForLogin(
            String id,
            String phone
    ) {

        return userRepository.findByIdAndPhone(
                id,
                phone
        );
    }
}