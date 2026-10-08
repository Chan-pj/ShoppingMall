-- =========================================================
-- 쇼핑몰 DB 스키마 (SQL Server)
-- 기준: 쇼핑몰 설계 명세_최소수정.xlsx
-- 실행: SSMS에서 열어 실행 (기존 테이블은 삭제 후 다시 생성)
-- =========================================================
USE ShoppingMall_System;
GO

-- 참조 관계 역순으로 삭제
DROP TABLE IF EXISTS SP_Detail_Info;
DROP TABLE IF EXISTS SP_Image_Info;
DROP TABLE IF EXISTS SP_Product_Info;
DROP TABLE IF EXISTS SP_Category_Info;
DROP TABLE IF EXISTS SP_Auth_Info;
DROP TABLE IF EXISTS SP_User_Info;
GO

-- =========================================================
-- 회원정보
-- =========================================================
CREATE TABLE SP_User_Info (
    SP_UI_No       VARCHAR(20)  NOT NULL,
    SP_UI_ID       VARCHAR(20)  NOT NULL,
    SP_UI_Name     VARCHAR(20)  NOT NULL,
    SP_UI_PhoneNum VARCHAR(20)  NOT NULL,
    SP_UI_Birth    DATE         NOT NULL,
    SP_UI_Email    VARCHAR(100) NOT NULL,
    SP_UI_Rdate    DATE         NOT NULL CONSTRAINT DF_SP_UI_Rdate DEFAULT (GETDATE()),
    CONSTRAINT PK_SP_User_Info PRIMARY KEY (SP_UI_No),
    CONSTRAINT UK_SP_UI_ID UNIQUE (SP_UI_ID),
    CONSTRAINT UK_SP_UI_PhoneNum UNIQUE (SP_UI_PhoneNum),
    CONSTRAINT UK_SP_UI_Email UNIQUE (SP_UI_Email)
);
GO

-- =========================================================
-- 인증번호
-- 전화번호는 FK가 아님 (회원가입 인증은 회원이 생기기 전에 저장됨)
-- =========================================================
CREATE TABLE SP_Auth_Info (
    SP_AI_Code      VARCHAR(20) NOT NULL,
    SP_UI_PhoneNum  VARCHAR(20) NOT NULL,
    SP_AI_Saparator CHAR(1)     NOT NULL,
    SP_AI_AuthNum   CHAR(6)     NOT NULL,
    SP_AI_Rdate     DATETIME    NOT NULL CONSTRAINT DF_SP_AI_Rdate DEFAULT (GETDATE()),
    SP_AI_Status    CHAR(1)     NOT NULL CONSTRAINT DF_SP_AI_Status DEFAULT ('Y'),
    CONSTRAINT PK_SP_Auth_Info PRIMARY KEY (SP_AI_Code),
    CONSTRAINT CK_SP_AI_Saparator CHECK (SP_AI_Saparator IN ('L', 'R')),
    CONSTRAINT CK_SP_AI_Status CHECK (SP_AI_Status IN ('Y', 'N'))
);
GO

CREATE INDEX IX_SP_Auth_Info_Phone
    ON SP_Auth_Info (SP_UI_PhoneNum, SP_AI_Saparator, SP_AI_Status);
GO

-- =========================================================
-- 카테고리 정보
-- 대분류: 상위코드 1·2 NULL / 중분류: 상위코드 1만 / 소분류: 1·2 모두
-- =========================================================
CREATE TABLE SP_Category_Info (
    SP_CI_Code        VARCHAR(20) NOT NULL,
    SP_CI_Name        VARCHAR(50) NOT NULL,
    SP_CI_Level       INT         NOT NULL,
    SP_CI_TopLayer    VARCHAR(20) NULL,
    SP_CI_BottomLayer VARCHAR(20) NULL,
    SP_CI_Rdate       DATE        NOT NULL CONSTRAINT DF_SP_CI_Rdate DEFAULT (GETDATE()),
    SP_CI_Status      CHAR(1)     NOT NULL CONSTRAINT DF_SP_CI_Status DEFAULT ('Y'),
    CONSTRAINT PK_SP_Category_Info PRIMARY KEY (SP_CI_Code),
    CONSTRAINT FK_SP_CI_TopLayer FOREIGN KEY (SP_CI_TopLayer)
        REFERENCES SP_Category_Info (SP_CI_Code),
    CONSTRAINT FK_SP_CI_BottomLayer FOREIGN KEY (SP_CI_BottomLayer)
        REFERENCES SP_Category_Info (SP_CI_Code),
    CONSTRAINT CK_SP_CI_Level CHECK (SP_CI_Level BETWEEN 1 AND 3),
    CONSTRAINT CK_SP_CI_Status CHECK (SP_CI_Status IN ('Y', 'N'))
);
GO

-- =========================================================
-- 상품정보
-- =========================================================
CREATE TABLE SP_Product_Info (
    SP_PI_Code   VARCHAR(20)  NOT NULL,
    SP_PI_Name   VARCHAR(100) NOT NULL,
    SP_PI_Price  INT          NOT NULL,
    SP_PI_Count  INT          NOT NULL CONSTRAINT DF_SP_PI_Count DEFAULT (0),
    SP_PI_Supply VARCHAR(100) NOT NULL,
    SP_CI_Code   VARCHAR(20)  NOT NULL,
    SP_PI_Rdate  DATE         NOT NULL CONSTRAINT DF_SP_PI_Rdate DEFAULT (GETDATE()),
    SP_PI_Status CHAR(1)      NOT NULL CONSTRAINT DF_SP_PI_Status DEFAULT ('Y'),
    CONSTRAINT PK_SP_Product_Info PRIMARY KEY (SP_PI_Code),
    CONSTRAINT FK_SP_PI_Category FOREIGN KEY (SP_CI_Code)
        REFERENCES SP_Category_Info (SP_CI_Code),
    CONSTRAINT CK_SP_PI_Price CHECK (SP_PI_Price >= 0),
    CONSTRAINT CK_SP_PI_Count CHECK (SP_PI_Count >= 0),
    CONSTRAINT CK_SP_PI_Status CHECK (SP_PI_Status IN ('Y', 'N'))
);
GO

-- =========================================================
-- 이미지
-- =========================================================
CREATE TABLE SP_Image_Info (
    SP_II_Code  VARCHAR(20)  NOT NULL,
    SP_PI_Code  VARCHAR(20)  NOT NULL,
    SP_II_Name  VARCHAR(255) NOT NULL,
    SP_II_Rdate DATE         NOT NULL CONSTRAINT DF_SP_II_Rdate DEFAULT (GETDATE()),
    CONSTRAINT PK_SP_Image_Info PRIMARY KEY (SP_II_Code),
    CONSTRAINT FK_SP_II_Product FOREIGN KEY (SP_PI_Code)
        REFERENCES SP_Product_Info (SP_PI_Code) ON DELETE CASCADE
);
GO

-- =========================================================
-- 상세정보
-- =========================================================
CREATE TABLE SP_Detail_Info (
    SP_DI_Code  VARCHAR(20)  NOT NULL,
    SP_PI_Code  VARCHAR(20)  NOT NULL,
    SP_DI_Name  VARCHAR(50)  NOT NULL,
    SP_DI_Value VARCHAR(500) NOT NULL,
    SP_DI_Rdate DATE         NOT NULL CONSTRAINT DF_SP_DI_Rdate DEFAULT (GETDATE()),
    CONSTRAINT PK_SP_Detail_Info PRIMARY KEY (SP_DI_Code),
    CONSTRAINT FK_SP_DI_Product FOREIGN KEY (SP_PI_Code)
        REFERENCES SP_Product_Info (SP_PI_Code) ON DELETE CASCADE
);
GO
