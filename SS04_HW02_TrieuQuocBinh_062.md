## 1. Tổng Quan Hai Phương Án

| Tiêu chí | Phương án 1: Trực tiếp map AI Payload vào JPA Entity | Phương án 2: Tách biệt DTO Record Phòng Thủ + Rich JPA Entity (Áp dụng trong bài) |
| :--- | :--- | :--- |
| **Mô hình** | Single-layer (AI JSON $\rightarrow$ `@Entity` có Setter/Lombok `@Data`) | Two-layer (AI JSON $\rightarrow$ `Record DTO` $\rightarrow$ `Mapper` $\rightarrow$ Rich `Entity`) |
| **Tính biến thể** | Mutable (Entity chứa Setter mở cho toàn bộ trường) | Immutable DTO + Encapsulated Entity |

---

## 2. Phân Tích Chi Tiết Theo 3 Góc Nhìn Kỹ Thuật

### 2.1. Góc Nhìn Lập Trình Phòng Thủ (Defensive Programming)

* **Phương án 1 (Map trực tiếp vào Entity):**
  * **Nhược điểm:** AI payload không đáng tin cậy (có thể null, sai định dạng enum, thiếu key, chuỗi chứa khoảng trắng hoặc markdown thừa). Đổ trực tiếp vào Entity dễ gây `NullPointerException`, lỗi serialize enum hoặc làm nhiễm bẩn dữ liệu cơ sở dữ liệu.
  * **Khó gán giá trị mặc định:** Không có chốt chặn để fallback an toàn trước khi vào persistence context.

* **Phương án 2 (DTO Record + Entity):**
  * **Ưu điểm:**
    * **Chốt chặn 1 (DTO Record - Compact Constructor):** Tự động sanitize (cắt khoảng trắng, chuẩn hóa chữ hoa), gán giá trị mặc định (`0`, `0.5`, `UNKNOWN_*`) ngay khi Jackson parse JSON.
    * **Chốt chặn 2 (Mapper & Domain Enums):** Phân loại enum linh hoạt (`fromStringOrDefault`), xử lý logic suy diễn (ví dụ: tai nạn + cần cứu hộ $\rightarrow$ nâng mức `CRITICAL`).
    * **Chốt chặn 3 (Entity Validation):** Sử dụng `Objects.requireNonNull()` và kiểm tra logic nghiệp vụ tại Static Factory Method `createFromValidatedExtraction()`.

---

### 2.2. Góc Nhìn Tính Đóng Gói (Encapsulation)

* **Phương án 1 (Entity Anemic / Getter-Setter):**
  * **Phá vỡ đóng gói:** Việc mở `public setters` cho phép bất kỳ tầng nào (Service, Controller, Runner) thay đổi tùy tiện trạng thái Entity (ví dụ: tự ý đổi `status = RESOLVED` mà không ghi log lý do).
  * **Mất tính toàn vẹn:** Entity không tự bảo vệ được trạng thái hợp lệ của chính nó.

* **Phương án 2 (Rich Domain Entity):**
  * **Bảo vệ toàn vẹn trạng thái:**
    * Toàn bộ trường được bảo vệ, chỉ mở `@Getter`.
    * Không dùng Setter mở; việc chuyển đổi trạng thái bắt buộc thông qua các phương thức nghiệp vụ có kiểm tra điều kiện: `verify()`, `markInProgress()`, `resolve()`, `reject()`.
    * Khởi tạo bắt buộc qua Static Factory Method đảm bảo không có object "nửa vời" (rỗng trường bắt buộc).

---

### 2.3. Góc Nhìn Ràng Buộc Kỹ Thuật Của Hibernate/JPA

| Ràng buộc kỹ thuật | Phương án 1 (Map trực tiếp) | Phương án 2 (DTO Record + Rich Entity) |
| :--- | :--- | :--- |
| **No-arg Constructor** | Bắt buộc `public` để Jackson và Hibernate cùng dùng, làm lộ rủi ro khởi tạo Entity rỗng ngoài luồng. | Dùng `@NoArgsConstructor(access = AccessLevel.PROTECTED)`: Vừa đủ cho Hibernate proxy/reflection, vừa chặn tạo instance tùy tiện từ code ngoài. |
| **ID Auto-Generated** | Dùng chung field ID cho cả hứng JSON và DB, dễ bị ID injection từ payload AI. | Tách biệt hoàn toàn: DTO không có field ID. ID do database sinh (`GenerationType.IDENTITY`), Entity chỉ có getter cho ID. |
| **Nullable & Kiểu dữ liệu** | Xung đột giữa kiểu nguyên thủy (`int`, `boolean`) và Wrapper (`Integer`, `Boolean`): AI trả `null` sẽ gây văng lỗi ép kiểu nguyên thủy. | DTO dùng Wrapper type (`Integer`, `Boolean`, `Double`) để hứng an toàn $\rightarrow$ Chuyển sang primitive/not-null type an toàn khi gán vào Entity. |
| **Equals & HashCode** | Thường dùng Lombok `@EqualsAndHashCode` trên toàn bộ fields, gây sai lệch khi ID thay đổi sau khi `persist()`. | Override `equals`/`hashCode` chỉ dựa trên `id` và class type, an toàn với Hibernate proxy. |

---

## 3. Tổng Kết & Đánh Giá

* **Phương án 1:** Thích hợp với ứng dụng CRUD đơn giản, prototype nhanh, dữ liệu đầu vào nội bộ đáng tin cậy. Tuy nhiên, không phù hợp khi làm việc với AI do tính bất định của LLM.
* **Phương án 2 (Khuyên dùng):** Tạo kiến trúc đa tầng vững chắc, tuân thủ nguyên lý Clean Architecture và Defensive Programming, triệt tiêu lỗi runtime do AI sinh ra và đáp ứng trọn vẹn đặc tả của Hibernate/JPA.
