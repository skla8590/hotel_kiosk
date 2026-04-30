-- pgvector 확장을 설치해 벡터 타입과 벡터 연산 기능을 추가
CREATE EXTENSION IF NOT EXISTS vector;

-- 키-값 저장소 기능을 제공하는 hstore 확장을 추가
CREATE EXTENSION IF NOT EXISTS hstore;

-- UUID 생성 함수를 제공하는 uuid-ossp 확장을 추가
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- pgvector 전용 스키마 생성
CREATE SCHEMA IF NOT EXISTS pgvector;

-- 스키마 사용 권한 부여
GRANT ALL ON SCHEMA pgvector TO postgres;

-- 테이블 생성
CREATE TABLE IF NOT EXISTS vector_store
(
    id        uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    content   text,
    metadata  json,
    embedding vector(1536) -- 기본 임베딩 차원: 1536
);

-- 인덱스 생성
CREATE INDEX ON vector_store
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);