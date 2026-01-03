@echo off
echo ========================================================
echo  FLOWBILL SEED DATA LOADER
echo ========================================================

:: Check if docker container is running
docker ps -q -f name=postgres-master > nul
if errorlevel 1 (
    echo Error: 'postgres-master' container is not running.
    echo Please run 'docker-compose up -d postgres' first.
    pause
    exit /b 1
)

echo Loading data.sql into postgres-master...
:: Copy file to container
docker cp data.sql postgres-master:/tmp/data.sql

:: Execute SQL
:: We use admin_platform and flowbill_master as defined in .env
docker exec -i postgres-master psql -U admin_platform -d flowbill_master -f /tmp/data.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================================
    echo  ^[OK^] Data loaded successfully!
    echo ========================================================
    echo You can now login as:
    echo   - superadmin@flowbill.com / password
    echo   - admin@acme.com / password
    echo   - ceo@startupflow.io / password
    echo ========================================================
) else (
    echo.
    echo Error: Failed to load data.
)

pause
