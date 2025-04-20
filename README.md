## 🧠 Nutrifit Backend
Nutrifit Mall의 백엔드 서버입니다.
Spring Boot를 기반으로 하며, 프론트엔드(Next.js), Redis, PostgreSQL과 통신하여 건강식품 이커머스 서비스를 제공합니다.

## 📦 기술 스택
Framework: Spring Boot 3.x

Language: Java 17

Database: PostgreSQL

Cache: Redis

Build Tool: Gradle

Authentication: JWT + Google OAuth

ORM: Hibernate (JPA)

## 🚀 실행 순서
PostgreSQL DB 실행

Backend 서버 실행 (./gradlew bootRun)

Frontend 서버 실행

## 🔑 환경 변수 (.env and application.properties)
src/main/resources/application.properties 와 .env를 사용해 다음 정보를 설정해야 합니다:

spring.application.name=nutrifit

server.port=5000

spring.datasource.url=jdbc:postgresql://localhost:5432/nutrifit_mall
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.sql.init.mode=always
spring.datasource.initialization-mode=always

spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql

logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.max-lifetime=1800000

spring.http.encoding.charset=UTF-8
spring.http.encoding.enabled=true
spring.http.encoding.force=true

#cache
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
spring.cache.cache-names=products
spring.cache.redis.time-to-live=604800000


#SMTP
#spring.mail.host=smtp.example.com
#spring.mail.port=587
#spring.mail.username=your-email@example.com
#spring.mail.password=your-email-password
#spring.mail.properties.mail.smtp.auth=true
#spring.mail.properties.mail.smtp.starttls.enable=true

#jwt
jwt.secret=${JWT_SECRET}

#PGPayments
iamport.apiKey=${IAMPORT_API_KEY}
iamport.apiSecret=${IAMPORT_API_SECRET}

#OAUTH2
oauth.google.client-id=${OAUTH_GOOGLE_CLIENT_ID}
oauth.google.client-secret=${OAUTH_GOOGLE_CLIENT_SECRET}
oauth.google.redirect-uri=https://nutrifit-front.vercel.app/auth/callback/google
oauth.naver.client-id=${OAUTH_NAVER_CLIENT_ID}
oauth.naver.client-secret=${OAUTH_NAVER_CLIENT_SECRET}
oauth.naver.redirect-uri=https://nutrifit-front.vercel.app/auth/callback/naver
oauth.kakao.client-id=${OAUTH_KAKAO_CLIENT_ID}
oauth.kakao.client-secret=${OAUTH_KAKAO_CLIENT_SECRET}
oauth.kakao.redirect-uri=https://nutrifit-front.vercel.app/auth/callback/kakao

.env에 ${}에 들어갈 것들을 넣어주세요~

## 📁 주요 폴더 구조
- security        : 인증 및 보안 관련 로직
- configuration   : Spring 설정 파일
- controller      : API 컨트롤러
- dto             : 데이터 전송 객체
- persistence     : JPA 리포지토리 인터페이스와 엔티티
- service         : 비즈니스 로직 처리
- lib             : 공통 유틸성 클래스

resources/
  - application.properties  : 환경 설정 파일
  - static/          : 정적 리소스 (이미지, HTML 등)

## 🙋 문의
개발 관련 이슈나 문의는 Issues 탭을 통해 남겨주세요.
