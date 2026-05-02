-- 1. Tạo bảng Danh Mục
CREATE TABLE DanhMuc (
                         ID_DM VARCHAR(20) PRIMARY KEY,
                         TenDM VARCHAR(100) NOT NULL
);

-- 2. Tạo bảng Kho Hàng
CREATE TABLE KhoHang (
                         ID_Kho VARCHAR(20) PRIMARY KEY,
                         KhuVuc VARCHAR(50) NOT NULL,
                         DiaChi VARCHAR(255)
);

-- 3. Tạo bảng Sản Phẩm
CREATE TABLE SanPham (
                         ID_SP VARCHAR(20) PRIMARY KEY,
                         TenSP VARCHAR(255) NOT NULL,
                         MoTa TEXT,
                         Gia DECIMAL(15, 2) NOT NULL,
                         ID_DM VARCHAR(20),
                         FOREIGN KEY (ID_DM) REFERENCES DanhMuc(ID_DM)
);

-- 4. Tạo bảng Tồn Kho (Khóa phức hợp)
CREATE TABLE TonKho (
                        ID_Kho VARCHAR(20),
                        ID_SP VARCHAR(20),
                        SoLuong INT DEFAULT 0,
                        PRIMARY KEY (ID_Kho, ID_SP),
                        FOREIGN KEY (ID_Kho) REFERENCES KhoHang(ID_Kho),
                        FOREIGN KEY (ID_SP) REFERENCES SanPham(ID_SP)
);

-- 5. Tạo bảng Khách Hàng
CREATE TABLE KhachHang (
                           ID_KH VARCHAR(20) PRIMARY KEY,
                           HoTen VARCHAR(100) NOT NULL,
                           Email VARCHAR(100) UNIQUE,
                           SDT VARCHAR(20),
                           DiaChi VARCHAR(255),
                           MatKhau VARCHAR(255) NOT NULL
);

-- 6. Tạo bảng Nhân Viên
CREATE TABLE NhanVien (
                          ID_NV VARCHAR(20) PRIMARY KEY,
                          HoTen VARCHAR(100) NOT NULL,
                          Email VARCHAR(100) UNIQUE,
                          MatKhau VARCHAR(255) NOT NULL,
                          VaiTro VARCHAR(50),
                          ID_Kho VARCHAR(20),
                          FOREIGN KEY (ID_Kho) REFERENCES KhoHang(ID_Kho)
);

-- 7. Tạo bảng Đơn Hàng (Không có ID_Kho)
CREATE TABLE DonHang (
                         ID_DH VARCHAR(20) PRIMARY KEY,
                         NgayDat DATETIME DEFAULT CURRENT_TIMESTAMP,
                         TongTien DECIMAL(15, 2),
                         TrangThai VARCHAR(50),
                         ID_KH VARCHAR(20),
                         FOREIGN KEY (ID_KH) REFERENCES KhachHang(ID_KH)
);

-- 8. Tạo bảng Chi Tiết Đơn Hàng (Có ID_Kho, Khóa phức hợp)
CREATE TABLE ChiTietDH (
                           ID_DH VARCHAR(20),
                           ID_SP VARCHAR(20),
                           SoLuong INT NOT NULL,
                           DonGia DECIMAL(15, 2) NOT NULL,
                           ID_Kho VARCHAR(20) NOT NULL,
                           PRIMARY KEY (ID_DH, ID_SP),
                           FOREIGN KEY (ID_DH) REFERENCES DonHang(ID_DH),
                           FOREIGN KEY (ID_SP) REFERENCES SanPham(ID_SP),
                           FOREIGN KEY (ID_Kho) REFERENCES KhoHang(ID_Kho)
);

-- 9. Tạo bảng Phiếu Điều Chuyển
CREATE TABLE PhieuDieuChuyen (
                                 ID_PDC VARCHAR(20) PRIMARY KEY,
                                 NgayChuyen DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 TrangThai VARCHAR(50),
                                 KhoXuat_ID VARCHAR(20) NOT NULL,
                                 KhoNhap_ID VARCHAR(20) NOT NULL,
                                 ID_NV VARCHAR(20),
                                 FOREIGN KEY (KhoXuat_ID) REFERENCES KhoHang(ID_Kho),
                                 FOREIGN KEY (KhoNhap_ID) REFERENCES KhoHang(ID_Kho),
                                 FOREIGN KEY (ID_NV) REFERENCES NhanVien(ID_NV)
);

-- 10. Tạo bảng Chi Tiết Phiếu Điều Chuyển (Khóa phức hợp)
CREATE TABLE ChiTietPDC (
                            ID_PDC VARCHAR(20),
                            ID_SP VARCHAR(20),
                            SoLuong INT NOT NULL,
                            PRIMARY KEY (ID_PDC, ID_SP),
                            FOREIGN KEY (ID_PDC) REFERENCES PhieuDieuChuyen(ID_PDC),
                            FOREIGN KEY (ID_SP) REFERENCES SanPham(ID_SP)
);

-- =============================================
-- INIT.SQL - DỮ LIỆU MẪU HỆ THỐNG KHO HÀNG
-- =============================================

-- 1. DANH MỤC
-- =============================================
INSERT INTO DanhMuc (ID_DM, TenDM) VALUES
                                       ('DM-PHONE',    'Điện thoại'),
                                       ('DM-LAPTOP',   'Laptop'),
                                       ('DM-TABLET',   'Máy tính bảng'),
                                       ('DM-ACCESSORY','Phụ kiện'),
                                       ('DM-AUDIO',    'Âm thanh');

-- =============================================
-- 2. KHO HÀNG
-- =============================================
INSERT INTO KhoHang (ID_Kho, KhuVuc, DiaChi) VALUES
                                                 ('KHO-NORTH', 'Miền Bắc', '18 Tam Trinh, Hoàng Mai, Hà Nội'),
                                                 ('KHO-SOUTH', 'Miền Nam',  '45 Đinh Tiên Hoàng, Bình Thạnh, TP.HCM');

-- =============================================
-- 3. SẢN PHẨM
-- =============================================
INSERT INTO SanPham (ID_SP, TenSP, MoTa, Gia, ID_DM) VALUES
-- Điện thoại
('SP-IPH15PM',   'iPhone 15 Pro Max 256GB',     'Chip A17 Pro, camera 48MP',           34990000, 'DM-PHONE'),
('SP-IPH15',     'iPhone 15 128GB',              'Chip A16, Dynamic Island',            22990000, 'DM-PHONE'),
('SP-SS-S24U',   'Samsung Galaxy S24 Ultra',     'Bút S Pen, RAM 12GB',                 31990000, 'DM-PHONE'),
('SP-SS-A55',    'Samsung Galaxy A55',            'AMOLED 120Hz, 5G',                    9990000, 'DM-PHONE'),
('SP-XIAOMI14',  'Xiaomi 14',                    'Leica camera, Snapdragon 8 Gen3',     18990000, 'DM-PHONE'),
-- Laptop
('SP-MBP14',     'MacBook Pro 14" M3 Pro',       'Chip M3 Pro, RAM 18GB',               52990000, 'DM-LAPTOP'),
('SP-MBP16',     'MacBook Pro 16" M3 Max',       'Chip M3 Max, RAM 36GB',               89990000, 'DM-LAPTOP'),
('SP-DELL-XPS',  'Dell XPS 15 9530',             'Intel i7, RTX 4060, 32GB',            45990000, 'DM-LAPTOP'),
('SP-ASUS-ROG',  'ASUS ROG Zephyrus G14',        'Ryzen 9, RTX 4070, 16GB',             42990000, 'DM-LAPTOP'),
-- Máy tính bảng
('SP-IPAD-PRO',  'iPad Pro M2 12.9"',            'Chip M2, Liquid Retina XDR',          26990000, 'DM-TABLET'),
('SP-IPAD-AIR',  'iPad Air M1 10.9"',            'Chip M1, 5G',                         16990000, 'DM-TABLET'),
('SP-SS-TAB9',   'Samsung Galaxy Tab S9+',       'AMOLED 12.4", S Pen',                 22990000, 'DM-TABLET'),
-- Phụ kiện
('SP-OPLUNG-15', 'Ốp lưng iPhone 15 chính hãng','Silicon Apple',                          790000, 'DM-ACCESSORY'),
('SP-OPLUNG-S24','Ốp lưng Samsung S24 Ultra',    'Carbon fiber',                           490000, 'DM-ACCESSORY'),
('SP-SACMAGSAFE','Sạc MagSafe 15W',              'Từ tính, tương thích iPhone 12+',      1290000, 'DM-ACCESSORY'),
('SP-KINH-CL',   'Kính cường lực iPhone 15 PM',  'Chống va đập 9H',                       290000, 'DM-ACCESSORY'),
-- Âm thanh
('SP-AIRPODS3',  'AirPods 3rd Generation',       'Spatial Audio, IPX4',                  5990000, 'DM-AUDIO'),
('SP-AIRPODSPRO','AirPods Pro 2nd Generation',   'ANC chủ động, USB-C',                  7990000, 'DM-AUDIO'),
('SP-SONY-XM5',  'Sony WH-1000XM5',              'ANC tốt nhất phân khúc',               8990000, 'DM-AUDIO'),
('SP-BOSE-QC45', 'Bose QuietComfort 45',         'Chống ồn, 24h pin',                    8490000, 'DM-AUDIO');

-- =============================================
-- 4. TỒN KHO
-- Ghi chú:
--   [NORTH only] = chỉ có Kho Bắc, Kho Nam = 0  → test lấy hàng từ Bắc
--   [SOUTH only] = chỉ có Kho Nam, Kho Bắc = 0  → test lấy hàng từ Nam
--   [BOTH]       = cả 2 kho đều có
-- =============================================
INSERT INTO TonKho (ID_Kho, ID_SP, SoLuong) VALUES

-- iPhone 15 Pro Max [NORTH only]
('KHO-NORTH', 'SP-IPH15PM',    15),
('KHO-SOUTH', 'SP-IPH15PM',     0),

-- iPhone 15 [BOTH]
('KHO-NORTH', 'SP-IPH15',      20),
('KHO-SOUTH', 'SP-IPH15',      25),

-- Samsung S24 Ultra [SOUTH only]
('KHO-NORTH', 'SP-SS-S24U',     0),
('KHO-SOUTH', 'SP-SS-S24U',    18),

-- Samsung A55 [BOTH]
('KHO-NORTH', 'SP-SS-A55',     30),
('KHO-SOUTH', 'SP-SS-A55',     35),

-- Xiaomi 14 [NORTH only]
('KHO-NORTH', 'SP-XIAOMI14',   10),
('KHO-SOUTH', 'SP-XIAOMI14',    0),

-- MacBook Pro 14 [BOTH - số lượng ít]
('KHO-NORTH', 'SP-MBP14',       5),
('KHO-SOUTH', 'SP-MBP14',       7),

-- MacBook Pro 16 [NORTH only - hàng cao cấp]
('KHO-NORTH', 'SP-MBP16',       4),
('KHO-SOUTH', 'SP-MBP16',       0),

-- Dell XPS 15 [SOUTH only]
('KHO-NORTH', 'SP-DELL-XPS',    0),
('KHO-SOUTH', 'SP-DELL-XPS',    6),

-- ASUS ROG [BOTH]
('KHO-NORTH', 'SP-ASUS-ROG',    8),
('KHO-SOUTH', 'SP-ASUS-ROG',   10),

-- iPad Pro [BOTH]
('KHO-NORTH', 'SP-IPAD-PRO',    6),
('KHO-SOUTH', 'SP-IPAD-PRO',    9),

-- iPad Air [BOTH]
('KHO-NORTH', 'SP-IPAD-AIR',   12),
('KHO-SOUTH', 'SP-IPAD-AIR',   15),

-- Samsung Tab S9+ [SOUTH only]
('KHO-NORTH', 'SP-SS-TAB9',     0),
('KHO-SOUTH', 'SP-SS-TAB9',     8),

-- Ốp lưng iPhone 15 [BOTH]
('KHO-NORTH', 'SP-OPLUNG-15',  50),
('KHO-SOUTH', 'SP-OPLUNG-15',  60),

-- Ốp lưng S24 [SOUTH only]
('KHO-NORTH', 'SP-OPLUNG-S24',  0),
('KHO-SOUTH', 'SP-OPLUNG-S24', 45),

-- Sạc MagSafe [NORTH only]
('KHO-NORTH', 'SP-SACMAGSAFE', 25),
('KHO-SOUTH', 'SP-SACMAGSAFE',  0),

-- Kính cường lực [BOTH]
('KHO-NORTH', 'SP-KINH-CL',   100),
('KHO-SOUTH', 'SP-KINH-CL',   120),

-- AirPods 3 [BOTH]
('KHO-NORTH', 'SP-AIRPODS3',   15),
('KHO-SOUTH', 'SP-AIRPODS3',   20),

-- AirPods Pro [NORTH only]
('KHO-NORTH', 'SP-AIRPODSPRO', 10),
('KHO-SOUTH', 'SP-AIRPODSPRO',  0),

-- Sony XM5 [SOUTH only]
('KHO-NORTH', 'SP-SONY-XM5',    0),
('KHO-SOUTH', 'SP-SONY-XM5',    8),

-- Bose QC45 [BOTH - số lượng ít]
('KHO-NORTH', 'SP-BOSE-QC45',   3),
('KHO-SOUTH', 'SP-BOSE-QC45',   4);

-- =============================================
-- 5. KHÁCH HÀNG
-- =============================================
INSERT INTO KhachHang (ID_KH, HoTen, Email, SDT, DiaChi, MatKhau) VALUES
                                                                      ('KH-APP-001', 'Nguyễn Văn An',   'an.nguyen@gmail.com',  '0901111001', 'Hoàn Kiếm, Hà Nội',      'hashed_pw_001'),
                                                                      ('KH-APP-002', 'Trần Thị Bích',   'bich.tran@gmail.com',  '0902222002', 'Cầu Giấy, Hà Nội',       'hashed_pw_002'),
                                                                      ('KH-APP-003', 'Lê Minh Cường',   'cuong.le@gmail.com',   '0903333003', 'Hải Châu, Đà Nẵng',      'hashed_pw_003'),
                                                                      ('KH-APP-004', 'Phạm Thị Dung',   'dung.pham@gmail.com',  '0904444004', 'Thanh Khê, Đà Nẵng',     'hashed_pw_004'),
                                                                      ('KH-APP-005', 'Hoàng Văn Em',    'em.hoang@gmail.com',   '0905555005', 'Bình Thạnh, TP.HCM',     'hashed_pw_005'),
                                                                      ('KH-APP-006', 'Vũ Thị Phương',   'phuong.vu@gmail.com',  '0906666006', 'Quận 7, TP.HCM',         'hashed_pw_006'),
                                                                      ('KH-APP-007', 'Đặng Quốc Hùng',  'hung.dang@gmail.com',  '0907777007', 'Ngũ Hành Sơn, Đà Nẵng', 'hashed_pw_007'),
                                                                      ('KH-APP-008', 'Bùi Thị Lan',     'lan.bui@gmail.com',    '0908888008', 'Đống Đa, Hà Nội',        'hashed_pw_008');

-- =============================================
-- 6. NHÂN VIÊN
-- =============================================
INSERT INTO NhanVien (ID_NV, HoTen, Email, MatKhau, VaiTro, ID_Kho) VALUES
-- Kho Bắc
('NV-NORTH-001', 'Ngô Đức Anh',   'duc.anh@warehouse.vn',   'hashed_nv_001', 'QuanLyKho',   'KHO-NORTH'),
('NV-NORTH-002', 'Đinh Thị Hoa',  'thi.hoa@warehouse.vn',   'hashed_nv_002', 'NhanVienKho', 'KHO-NORTH'),
('NV-NORTH-003', 'Lý Văn Khoa',   'van.khoa@warehouse.vn',  'hashed_nv_003', 'NhanVienKho', 'KHO-NORTH'),
-- Kho Nam
('NV-SOUTH-001', 'Trịnh Thị Oanh','thi.oanh@warehouse.vn',  'hashed_nv_004', 'QuanLyKho',   'KHO-SOUTH'),
('NV-SOUTH-002', 'Cao Văn Phúc',  'van.phuc@warehouse.vn',  'hashed_nv_005', 'NhanVienKho', 'KHO-SOUTH'),
('NV-SOUTH-003', 'Hà Thị Quỳnh',  'thi.quynh@warehouse.vn', 'hashed_nv_006', 'NhanVienKho', 'KHO-SOUTH');