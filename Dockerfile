# =============================================
# Stage 1：使用 Maven 編譯專案，產出 JAR 檔
# =============================================
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# 再複製完整原始碼並編譯（跳過測試以縮短建構時間）
COPY . .
RUN mvn clean package -DskipTests

# =============================================
# Stage 2：只帶 JAR 到精簡的 JRE 執行環境
# =============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 從 Stage 1 複製編譯好的 JAR（版本號用萬用字元處理）
COPY --from=builder /app/target/*.jar app.jar

# 容器啟動時執行 JAR，並明確指定使用 prod Profile
ENTRYPOINT ["java", "-jar", "app.jar"]

# 宣告服務使用的 port（Render 預設讀取此值）
EXPOSE 8080
