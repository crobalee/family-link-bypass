@echo off
echo 패밀리 링크 우회 APK 빌드를 시작합니다...

REM Java가 설치되어 있는지 확인
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 경고: Java가 설치되어 있지 않습니다.
    echo Android Studio를 설치하거나 Java JDK를 설치해주세요.
    echo Gradle Wrapper가 Java를 자동으로 다운로드할 수 있습니다.
    echo.
    echo 계속 진행하시겠습니까? (Y/N)
    set /p choice=
    if /i "%choice%" neq "Y" (
        pause
        exit /b 1
    )
)

REM Android SDK가 있는지 확인
if not exist "%ANDROID_HOME%" (
    echo 경고: ANDROID_HOME 환경 변수가 설정되지 않았습니다.
    echo Android Studio를 통해 Android SDK를 설치해주세요.
)

echo 빌드를 시작합니다...
echo.

REM Gradle 래퍼를 사용하여 APK 빌드
call gradlew.bat assembleRelease

if %errorlevel% equ 0 (
    echo.
    echo 빌드가 성공적으로 완료되었습니다!
    echo APK 파일 위치: app\build\outputs\apk\release\app-release.apk
    echo.
    echo 이 APK 파일을 삼성 갤럭시 탭 S6 Lite에 설치하세요.
) else (
    echo.
    echo 빌드에 실패했습니다.
    echo 오류 로그를 확인해주세요.
)

pause
