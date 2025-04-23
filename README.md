# BoroMusic Backend

**BoroMusic Backend** — это серверная часть музыкальной стриминговой платформы, реализованная на Java с использованием Spring Boot. Приложение обеспечивает REST API для управления пользователями, треками и рекомендациями, с безопасной аутентификацией и авторизацией на основе JWT. Музыка хранится и передаётся потоково через облачное хранилище Backblaze.

## Основной функционал

- Аутентификация и авторизация пользователей (JWT + Spring Security)
- Потоковое воспроизведение музыки из облачного хранилища Backblaze B2
- REST API для управления треками, пользователями и метаданными
- Интеграция с сервисом машинного обучения для генерации персонализированных рекомендаций
- PostgreSQL для хранения метаданных

## Технологии

- Java
- Spring Boot
- Spring Security + JWT
- PostgreSQL
- Backblaze B2 Cloud Storage
- Maven

## Установка и запуск

1. Клонируйте репозиторий:

```bash
git clone https://github.com/birmay95/BoroMusic-backend.git
cd BoroMusic-backend
```

2. Настройте переменные окружения (`application.properties`)

3. Запустите приложение:

```bash
./mvnw spring-boot:run
```

## Сервис рекомендаций

Отдельный Python-сервис с нейросетью для персонализированных рекомендаций. Интеграция осуществляется через HTTP API.

📎 [См. ML репозиторий](https://github.com/birmay95/BoroMusic-ml)

## Клиентская часть

Android-приложение, разработанное на Kotlin с использованием Jetpack Compose.

📎 [См. frontend репозиторий](https://github.com/birmay95/BoroMusic-frontend)
