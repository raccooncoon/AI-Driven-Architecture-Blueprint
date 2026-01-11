#!/bin/bash

# Exit on error
set -e

echo "🚀 ADAB Docker Build & Run Script 시작..."

# 1. Check if Docker is running
if ! docker info > /dev/null 2>&1; then
  echo "❌ Docker가 실행 중이지 않습니다. Docker를 먼저 실행해 주세요."
  exit 1
fi

# 2. Build backend
echo "📦 백엔드 빌드 중 (Dockerfile 사용)..."
# Note: Dockerfile inside adab-api handles the gradle build now (multi-stage)

# 3. Build whole stack
echo "🏗️ Docker Compose로 서비스 구축 및 시작 중..."
docker-compose up --build -d

echo "✅ 모든 서비스가 정상적으로 시작되었습니다!"
echo "🌐 프론트엔드 접속 주소: http://localhost"
echo "⚙️ 백엔드 API 주소: http://localhost:8080"
echo "📊 로그 확인: docker-compose logs -f"
