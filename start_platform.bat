@echo off
echo ========================================================
echo   FLOWBILL SAAS PLATFORM - ONE CLICK STARTUP
echo ========================================================
echo.
echo [1/2] Building Project (Common + Services)...
call mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to build project.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [2/2] Starting Docker Environment...
docker-compose down -v
docker-compose up -d --build

echo.
echo ========================================================
echo   PLATFORM STARTED!
echo ========================================================
echo   Frontend:  http://localhost:3000
echo   Gateway:   http://localhost:8080
echo   Auth:      http://localhost:8081
echo   Tenant:    http://localhost:8082
echo   Project:   http://localhost:8083 (via Gateway)
echo.
echo   Check logs with: docker-compose logs -f
echo ========================================================
pause
