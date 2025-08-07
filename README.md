# 계정 설정 도우미

패밀리 링크 우회 및 구글 계정 자동 설정을 위한 Android 앱입니다.

## 주요 기능

- 패밀리 링크 우회 프로세스 시작
- 구글 계정 자동 설정 도우미
- 접근성 서비스를 통한 자동화 지원
- 실시간 상태 모니터링

## 보안 수정사항

✅ 하드코딩된 계정 정보 제거  
✅ 보안 권한 제거 (WRITE_SECURE_SETTINGS, WRITE_SETTINGS)  
✅ 패키지 비활성화 기능 제거  
✅ 안전한 접근성 서비스 구현  

## 온라인 빌드 방법

### GitHub Actions 사용 (권장)

1. **GitHub 저장소 생성**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/yourusername/family-link-bypass.git
   git push -u origin main
   ```

2. **GitHub Actions 실행**
   - GitHub 저장소의 Actions 탭으로 이동
   - "Build APK" 워크플로우가 자동으로 실행됩니다
   - 빌드 완료 후 Artifacts에서 APK 파일 다운로드

### 다른 온라인 빌드 서비스

#### GitLab CI/CD
```yaml
# .gitlab-ci.yml
build:
  image: openjdk:17
  script:
    - chmod +x gradlew
    - ./gradlew assembleRelease
  artifacts:
    paths:
      - app/build/outputs/apk/release/app-release.apk
```

#### CircleCI
```yaml
# .circleci/config.yml
version: 2.1
jobs:
  build:
    docker:
      - image: openjdk:17
    steps:
      - checkout
      - run: chmod +x gradlew
      - run: ./gradlew assembleRelease
      - store_artifacts:
          path: app/build/outputs/apk/release/app-release.apk
```

#### GitHub Codespaces
1. GitHub 저장소에서 "Code" 버튼 클릭
2. "Codespaces" 탭 선택
3. "Create codespace on main" 클릭
4. 터미널에서 다음 명령 실행:
   ```bash
   chmod +x gradlew
   ./gradlew assembleRelease
   ```

## 로컬 빌드 방법

### 요구사항
- Java JDK 17 이상
- Android SDK (선택사항)

### 빌드 명령
```bash
# Windows
.\gradlew.bat assembleRelease

# Linux/macOS
chmod +x gradlew
./gradlew assembleRelease
```

## 설치 및 사용

1. APK 파일을 Android 기기에 설치
2. 필요한 권한 허용:
   - 오버레이 권한
   - 접근성 서비스 권한
3. 앱 실행 후 원하는 기능 선택

## 주의사항

- 이 앱은 교육 목적으로만 사용되어야 합니다
- 실제 계정 정보는 입력하지 마세요
- 패밀리 링크 우회는 법적/윤리적 문제가 있을 수 있습니다

## 라이선스

이 프로젝트는 교육 목적으로만 제공됩니다.
