@echo off
chcp 65001 >nul
echo ======================================
echo Starting WeChat Payment Management System
echo ======================================

REM Set Java environment variables
set JAVA_HOME=C:\Program Files\Microsoft\jdk-11.0.27.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Checking Java environment...
java -version
if %ERRORLEVEL% neq 0 (
    echo Java environment not configured correctly!
    pause
    exit /b 1
)

echo.
echo Java environment check passed!

echo.
echo Checking certificate files...
if not exist "api_Certificate\apiclient_key.pem" (
    echo Warning: Private key file does not exist!
    echo Please ensure api_Certificate\apiclient_key.pem file exists
)

if not exist "api_Certificate\apiclient_cert.pem" (
    echo Warning: Certificate file does not exist!
    echo Please ensure api_Certificate\apiclient_cert.pem file exists
)

echo.
echo Certificate file check completed!

echo.
echo Entering payment manager application directory...
cd payment-manager

echo.
echo Compiling project...
call ..\gradlew clean build -x test --no-daemon

if %ERRORLEVEL% neq 0 (
    echo Project compilation failed! Trying to use existing jar file...
    if not exist "build\libs\payment-manager-1.0.0.jar" (
        echo No executable jar file found!
        echo Please check network connection and try compiling again.
        pause
        exit /b 1
    )
)

echo.
echo Starting application...
echo Application will start at http://localhost:8080/payment/
echo For external access with ngrok: https://your-ngrok-url.ngrok.io/payment/
echo Press Ctrl+C to stop the application
echo.

java -jar build\libs\payment-manager-1.0.0.jar

echo.
echo Application stopped!
pause 