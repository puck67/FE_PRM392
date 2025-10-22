# Hướng dẫn cài đặt ứng dụng Android Honda

## Cấu hình cần thiết

### 1. Cập nhật URL API Backend

Mở file `app/src/main/java/com/example/prm/network/ApiClient.java` và thay đổi URL:

```java
private static final String BASE_URL = "https://your-actual-api-domain.com/api/";
```

Thay `https://your-actual-api-domain.com/api/` bằng URL thực tế của backend tại `D:\prm2\SPRING25-SWP391-SE1825-GROUP5-BE`.

### 2. Chạy Backend Server

Trước khi test ứng dụng Android, hãy đảm bảo backend server đang chạy:

1. Mở terminal tại `D:\prm2\SPRING25-SWP391-SE1825-GROUP5-BE`
2. Chạy lệnh để start server (tùy thuộc vào cấu hình .NET của bạn)

### 3. Cấu hình Android Studio

1. Mở Android Studio
2. Mở dự án tại `D:\prm3`
3. Sync Gradle project
4. Đợi tất cả dependencies được tải về

## Tính năng đã hoàn thành

### ✅ Màn hình đăng nhập
- Giao diện giống hình 1 với logo Honda
- Trường nhập số điện thoại
- Trường nhập mật khẩu với toggle show/hide
- Nút đăng nhập màu đỏ Honda
- Link "Quên mật khẩu?"
- Link "Đăng ký" ở dưới

### ✅ Màn hình đăng ký  
- Giao diện giống hình 2
- Nút back
- Logo Honda nhỏ hơn
- Trường số điện thoại với prefix +84
- Trường mật khẩu với validation rules hiển thị
- Checkbox đồng ý điều khoản
- Nút đăng ký màu đỏ Honda

### ✅ Tích hợp API
- Model classes cho Request/Response
- Retrofit client để gọi API
- Xử lý lỗi và loading states
- Lưu token và thông tin user vào SharedPreferences

## Cấu trúc dự án

```
app/src/main/java/com/example/prm/
├── MainActivity.java (launcher)
├── models/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java  
│   ├── LoginResponse.java
│   └── ApiResponse.java
├── network/
│   ├── ApiClient.java
│   └── ApiService.java
└── ui/auth/
    ├── LoginActivity.java
    └── RegisterActivity.java

app/src/main/res/
├── layout/
│   ├── activity_login.xml
│   └── activity_register.xml
└── drawable/
    ├── honda_logo_bg.xml
    ├── edit_text_background.xml
    ├── button_background.xml
    └── ic_*.xml (icons)
```

## Validation Rules

### Đăng nhập
- Số điện thoại: Bắt buộc, format 0xxxxxxxxx (10 số)
- Mật khẩu: Không được trống

### Đăng ký
- Số điện thoại: Bắt buộc, format 0xxxxxxxxx (10 số)  
- Mật khẩu: Ít nhất 8 ký tự, có chữ hoa, chữ thường, số, ký tự đặc biệt
- Checkbox điều khoản: Bắt buộc check

## Test ứng dụng

1. Build và chạy ứng dụng trên emulator hoặc thiết bị thật
2. Ứng dụng sẽ tự động mở màn hình đăng nhập
3. Nhấn "Đăng ký" để chuyển đến màn hình đăng ký
4. Test validation với dữ liệu không hợp lệ
5. Test kết nối API với backend server

## Lưu ý quan trọng

- Cần cập nhật BASE_URL trong ApiClient để kết nối đúng backend
- Backend server phải đang chạy trước khi test
- Kiểm tra network permissions trong AndroidManifest.xml
- Giao diện được tối ưu cho điện thoại Android, responsive với các kích thước màn hình khác nhau

## Màu sắc Honda Brand

- Màu đỏ chính: `#E53E3E`
- Màu xám: `#666666`, `#999999`
- Background: `#F5F5F5`
- Border: `#E0E0E0`
