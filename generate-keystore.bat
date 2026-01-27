@echo off
REM Скрипт для создания самоподписанного SSL сертификата (Windows)
REM Использование: generate-keystore.bat

set KEYSTORE_FILE=src\main\resources\keystore.p12
set KEYSTORE_PASSWORD=changeit
set KEY_ALIAS=spring-boot
set VALIDITY_DAYS=365

echo Генерация самоподписанного SSL сертификата...

REM Создаем директорию если её нет
if not exist "src\main\resources" mkdir "src\main\resources"

REM Генерируем keystore с самоподписанным сертификатом
keytool -genkeypair ^
    -alias %KEY_ALIAS% ^
    -keyalg RSA ^
    -keysize 2048 ^
    -storetype PKCS12 ^
    -keystore %KEYSTORE_FILE% ^
    -validity %VALIDITY_DAYS% ^
    -storepass %KEYSTORE_PASSWORD% ^
    -keypass %KEYSTORE_PASSWORD% ^
    -dname "CN=localhost, OU=Development, O=Spring Boot, L=City, ST=State, C=RU"

echo.
echo Keystore создан: %KEYSTORE_FILE%
echo Пароль: %KEYSTORE_PASSWORD%
echo Алиас: %KEY_ALIAS%
echo.
echo Для просмотра информации о сертификате используйте:
echo keytool -list -v -keystore %KEYSTORE_FILE% -storepass %KEYSTORE_PASSWORD%

pause