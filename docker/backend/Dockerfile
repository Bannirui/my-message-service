# ========== 构建阶段 ==========
FROM maven:3.9.9-eclipse-temurin-21 AS build

# 工作目录放在/build
WORKDIR /build

# 1 先复制根pom和子模块pom 利用Docker缓存
COPY pom.xml .
COPY mms-client/pom.xml mms-client/
COPY mms-common/pom.xml mms-common/
COPY mms-dal/pom.xml mms-dal/
COPY mms-service/pom.xml mms-service/
COPY mms-web/pom.xml mms-web/

# 2 预下载依赖
RUN mvn -pl mms-web -am dependency:go-offline -B

# 3 再复制全部源码
COPY . .

# 4. 编译打包 只打包mms-web
RUN mvn -pl mms-web -am clean package -DskipTests

# ========== 运行阶段 ==========
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 只复制Spring Boot打好的fat jar
COPY --from=build /build/mms-web/target/mms-web-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
