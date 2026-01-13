# Proxmox LXC 배포 가이드 (Git + Docker)

본 가이드는 **Proxmox LXC 컨테이너**에 **Backend, Frontend, Redis**를 띄우고, **외부의 Database와 Ollama**를 연결하는 구성을 다룹니다.

## 트러블슈팅

### 1. `apt update` 실패 / 인터넷 연결 확인
IP(`8.8.8.8`) 핑은 되는데 `apt update`가 안 된다면 **DNS 문제**입니다.

**해결 방법 (임시)**:
컨테이너 터미널에서 다음 명령어를 입력합니다.
```bash
echo "nameserver 8.8.8.8" > /etc/resolv.conf
```
다시 `apt update`를 시도해 보세요.

**해결 방법 (영구)**:
Proxmox UI에서 해당 컨테이너 선택 > **DNS** 메뉴 > **DNS Servers**를 `8.8.8.8` (또는 `1.1.1.1`)로 변경하고 편집합니다.

### 2. `ERROR: permission denied for schema public`
DB 연결은 되었으나 테이블 생성 권한이 없는 경우입니다.
PostgreSQL에 접속(psql 또는 pgAdmin)해서 해당 유저에게 **스키마 소유권(Owner)**을 줘야 합니다. (단순 `GRANT`로는 해결되지 않는 경우가 많습니다.)

**해결 방법 (반드시 `adab_prod_db`에 접속 후 실행)**:
```sql
-- 관리자(postgres) 계정으로 실행해야 함
ALTER SCHEMA public OWNER TO adab_prod_user;
```

### 3. 테이블이 보이지 않음 / 생성 안 됨
`application-prod.yml`의 `ddl-auto` 설정이 `update` 인지 확인하세요.
권한 문제일 확률이 높으니 위 2번 해결법을 먼저 시도하세요.

---

## 1. Proxmox LXC 컨테이너 생성

1.  **LXC 생성**: Proxmox UI에서 'Create CT'를 클릭합니다.
    *   **Template**: `Ubuntu 22.04` 또는 `Debian 12` 템플릿을 선택합니다.
    *   **Resources**:
        *   CPU: 2~4 Core (넉넉하게 권장)
        *   Memory: 4GB 이상
        *   Disk: 20GB 이상
    *   **Network**: DHCP 또는 고정 IP (Gateway 설정 필수)

2.  **Docker 실행 권한 부여 (필수)**:
    *   생성된 컨테이너를 선택하고 `Options` -> `Features` -> `Edit`를 클릭합니다.
    *   **`Nesting`** 및 **`keyctl`** 옵션을 체크하고 저장합니다.
    *   컨테이너를 **Start** 하고 **Console**을 엽니다.

---

## 2. 기본 환경 설정 & Docker 설치

컨테이너 콘솔(터미널)에서 순서대로 입력하세요.

```bash
# 1. 패키지 업데이트 및 Git 설치
apt update && apt install -y git curl

# 2. Docker 설치
curl -fsSL https://get.docker.com | sh
```

---

## 3. 소스 코드 가져오기 (Git Clone)

GitHub 등에 올린 코드를 가져옵니다.

```bash
# 저장소 주소는 본인의 것으로 변경하세요
git clone <YOUR_GIT_REPO_URL> adab-project
cd adab-project
```

---

## 4. 운영 환경 설정 (.env)

DB와 Ollama가 외부(다른 VM, 호스트 등)에 있으므로, 접속 정보를 설정합니다.

1.  **설정 파일 생성**:
    ```bash
    cp .env.example .env
    nano .env  # 또는 vi .env
    ```

2.  **내용 수정**:
    외부 서비스(DB, Ollama)의 실제 IP 주소를 입력합니다. `localhost` 대신 정확한 IP를 써야 합니다.

    ```ini
    # [운영 DB] 
    # 예: Proxmox 호스트의 5432 포트인 경우 호스트/VM IP 입력
    # 주의: adab_prod_db 데이터베이스와 adab_prod_user 계정을 사용하세요
    SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.1.100:5432/adab_prod_db
    SPRING_DATASOURCE_USERNAME=adab_prod_user
    SPRING_DATASOURCE_PASSWORD=CHANGE_THIS_PASSWORD
    
    # [운영 Ollama]
    # 예: GPU 서버 또는 호스트의 11434 포트
    SPRING_AI_OLLAMA_BASE_URL=http://192.168.1.105:11434
    
    # [설정] 프로필 (건드리지 않음)
    SPRING_PROFILES_ACTIVE=prod
    ```
    *(작성 후 `Ctrl+X` -> `Y` -> `Enter`로 저장)*

---

## 5. 서비스 실행

백엔드, 프론트엔드, 그리고 Redis를 컨테이너 내부에 띄웁니다.

```bash
# docker-compose.prod.yml 파일 사용
docker compose -f docker-compose.prod.yml up -d --build
```
*   `--build`: 소스 코드를 기반으로 이미지를 새로 굽습니다.
*   최초 실행 시 `ddl-auto: update` 설정에 의해 DB 테이블이 자동 생성됩니다.

---

## 6. 확인 및 접속

1.  **상태 확인**:
    ```bash
    docker compose -f docker-compose.prod.yml ps
    ```
    *   `adab-backend`, `adab-frontend`, `adab-redis` 3개가 모두 `Up` 상태여야 합니다.

2.  **로그 확인**:
    설정이 잘 되었는지 로그를 봅니다.
    ```bash
    docker compose -f docker-compose.prod.yml logs -f adab-backend
    ```

3.  **웹 접속**:
    브라우저에서 LXC 컨테이너의 IP 주소로 접속합니다.
    *   `http://<LXC_IP_ADDRESS>`

---

## 7. 업데이트 방법 (재배포)

소스를 수정하고 다시 배포할 때는 다음 명령어를 쓰세요.

```bash
git pull origin main                 # 최신 코드 받기
docker compose -f docker-compose.prod.yml up -d --build  # 다시 빌드해서 실행
```
