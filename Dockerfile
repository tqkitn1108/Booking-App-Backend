# Sử dụng một ảnh OpenJDK làm ảnh cơ sở
FROM openjdk:17-jdk-slim as build

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file Maven vào container để lấy các dependency
COPY pom.xml ./
COPY src ./src

# Chạy lệnh build để tạo file JAR từ mã nguồn
RUN ./mvnw clean package -DskipTests

# Tạo ảnh cuối cùng
FROM openjdk:17-jdk-slim
WORKDIR /app

# Sao chép file JAR đã build từ bước trước vào container
COPY --from=build /app/target/booking-api-0.0.1.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]