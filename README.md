# API для учета складских позиций

## Описание
Это приложение реализует REST API для управления складом. Оно предоставляет возможность регистрировать поступление носок, их отпуск, а также получать информацию о текущем состоянии склада с возможностью фильтрации и сортировки. Также поддерживается загрузка партий носок из файлов Excel или CSV. Все операции логируются, а ошибки обрабатываются централизованно.

Приложение использует PostgreSQL для хранения данных и Spring Boot для реализации бизнес-логики. Все компоненты запускаются в Docker-контейнерах, что обеспечивает удобство развертывания и масштабируемость.

## Запуск в Docker
Для удобства развертывания и работы приложение и база данных PostgreSQL запускаются в Docker-контейнерах.

- **PostgreSQL** используется для хранения данных о носках.
- **Spring Boot приложение** (storage) зависит от контейнера с PostgreSQL и подключается к нему через соответствующие настройки в `docker-compose`.

### Конфигурация
- **PostgreSQL контейнер** запускается с настройками по умолчанию: база данных `postgres_backspark`, пользователь `postgres`, пароль `123`.
- **Spring Boot приложение** настроено для подключения к базе данных через переменные окружения и использует профиль `secret`.

### Запуск
1. Соберите и запустите контейнеры с помощью Docker Compose:
   ```bash
   docker-compose up --build
