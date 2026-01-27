#!/bin/bash

# Скрипт для создания самоподписанного SSL сертификата
# Использование: ./generate-keystore.sh

KEYSTORE_FILE="src/main/resources/keystore.p12"
KEYSTORE_PASSWORD="changeit"
KEY_ALIAS="spring-boot"
VALIDITY_DAYS=365

echo "Генерация самоподписанного SSL сертификата..."

# Создаем директорию если её нет
mkdir -p src/main/resources

# Генерируем keystore с самоподписанным сертификатом
keytool -genkeypair \
    -alias $KEY_ALIAS \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore $KEYSTORE_FILE \
    -validity $VALIDITY_DAYS \
    -storepass $KEYSTORE_PASSWORD \
    -keypass $KEYSTORE_PASSWORD \
    -dname "CN=localhost, OU=Development, O=Spring Boot, L=City, ST=State, C=RU"

echo "Keystore создан: $KEYSTORE_FILE"
echo "Пароль: $KEYSTORE_PASSWORD"
echo "Алиас: $KEY_ALIAS"
echo ""
echo "Для просмотра информации о сертификате используйте:"
echo "keytool -list -v -keystore $KEYSTORE_FILE -storepass $KEYSTORE_PASSWORD"

