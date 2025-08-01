# 1. 자바 21 베이스 이미지 사용
FROM openjdk:21-jdk-alpine

# 2. 빌드된 앱 JAR 복사 (JAR 파일명은 빌드시 자동 생성되는 것을 사용)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. 컨테이너 오픈 포트 지정 (예: 8080)
EXPOSE 8080

# 4. 앱 실행 명령
ENTRYPOINT ["java","-jar","/app.jar"]
