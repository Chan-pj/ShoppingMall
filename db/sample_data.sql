-- =========================================================
-- 상품 목록/상세 화면 확인용 샘플 데이터
-- 여러 번 실행해도 같은 결과 (샘플 코드만 지우고 다시 넣음)
-- 이미지 파일은 src/main/resources/static/images/product/ 에 넣으면 표시됨
-- =========================================================
USE ShoppingMall_System;
GO

DELETE FROM SP_Detail_Info  WHERE SP_PI_Code LIKE 'P9%';
DELETE FROM SP_Image_Info   WHERE SP_PI_Code LIKE 'P9%';
DELETE FROM SP_Product_Info WHERE SP_PI_Code LIKE 'P9%';
DELETE FROM SP_Category_Info WHERE SP_CI_Level = 3 AND SP_CI_Code IN ('A00B000C000', 'A00B000C001', 'A00B001C000', 'B00B000C000');
DELETE FROM SP_Category_Info WHERE SP_CI_Level = 2 AND SP_CI_Code IN ('A00B000', 'A00B001', 'B00B000');
DELETE FROM SP_Category_Info WHERE SP_CI_Level = 1 AND SP_CI_Code IN ('A00', 'B00');
GO

-- 카테고리 (대 → 중 → 소 순서로 입력)
INSERT INTO SP_Category_Info (SP_CI_Code, SP_CI_Name, SP_CI_Level, SP_CI_TopLayer, SP_CI_BottomLayer) VALUES
('A00',         N'의류',   1, NULL,  NULL),
('B00',         N'잡화',   1, NULL,  NULL),
('A00B000',     N'상의',   2, 'A00', NULL),
('A00B001',     N'하의',   2, 'A00', NULL),
('B00B000',     N'가방',   2, 'B00', NULL),
('A00B000C000', N'티셔츠', 3, 'A00', 'A00B000'),
('A00B000C001', N'셔츠',   3, 'A00', 'A00B000'),
('A00B001C000', N'청바지', 3, 'A00', 'A00B001'),
('B00B000C000', N'백팩',   3, 'B00', 'B00B000');

-- 상품
INSERT INTO SP_Product_Info (SP_PI_Code, SP_PI_Name, SP_PI_Price, SP_PI_Count, SP_PI_Supply, SP_CI_Code) VALUES
('P9001', N'베이직 코튼 반팔 티셔츠', 15900, 120, N'그린섬유',   'A00B000C000'),
('P9002', N'오버핏 스트라이프 티셔츠', 22900,  35, N'그린섬유',   'A00B000C000'),
('P9003', N'옥스퍼드 버튼다운 셔츠',  39000,   0, N'모던셔츠',   'A00B000C001'),
('P9004', N'스트레이트 데님 팬츠',    49000,  60, N'블루데님',   'A00B001C000'),
('P9005', N'데일리 캔버스 백팩',      59000,  18, N'어반백',     'B00B000C000');

-- 이미지 (파일명만 저장, 파일이 없으면 화면에 기본 아이콘 표시)
INSERT INTO SP_Image_Info (SP_II_Code, SP_PI_Code, SP_II_Name) VALUES
('I9001', 'P9001', 'P9001_1.jpg'),
('I9002', 'P9001', 'P9001_2.jpg'),
('I9003', 'P9004', 'P9004_1.jpg');

-- 상세정보
INSERT INTO SP_Detail_Info (SP_DI_Code, SP_PI_Code, SP_DI_Name, SP_DI_Value) VALUES
('D9001', 'P9001', N'소재',     N'면 100%'),
('D9002', 'P9001', N'색상',     N'화이트, 블랙, 네이비'),
('D9003', 'P9001', N'제조국',   N'대한민국'),
('D9004', 'P9001', N'세탁방법', N'30도 이하 단독 손세탁'),
('D9005', 'P9002', N'소재',     N'면 95%, 폴리우레탄 5%'),
('D9006', 'P9003', N'소재',     N'면 100% (옥스퍼드)'),
('D9007', 'P9004', N'소재',     N'면 98%, 스판덱스 2%'),
('D9008', 'P9004', N'핏',       N'스트레이트'),
('D9009', 'P9005', N'소재',     N'캔버스, 소가죽 트림'),
('D9010', 'P9005', N'크기',     N'30 x 44 x 14 cm');
GO
