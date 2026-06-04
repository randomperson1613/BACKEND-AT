# API автотесты Notes API

Проект содержит backend API-автотесты для учебного сервиса [practice.expandtesting.com Notes API](https://practice.expandtesting.com/notes/api/api-docs/). Тесты написаны на Java 21 с использованием JUnit 5, RestAssured и Allure.

## Что покрыто тестами

Реализовано 14 API-тестов по основным группам:

- `HealthTest` - проверка доступности API через `GET /health-check`.
- `AuthTest` - регистрация, логин, профиль, обновление профиля, logout и негативные сценарии авторизации.
- `NotesTest` - CRUD заметок: создание, получение по id, список, обновление через `PUT`, изменение `completed` через `PATCH`, удаление через `DELETE`, негативный сценарий создания.

Требования ТЗ закрыты:

- Есть больше 5 тестов.
- Используются HTTP-методы `GET`, `POST`, `DELETE`; дополнительно покрыты `PUT` и `PATCH`.
- `RestAssured.baseURI` задается централизованно в базовой конфигурации.
- Запросы выполняются только через endpoint, без полного URL в тестах.
- Созданы базовые request/response specifications.
- Подключен `AllureRestAssured` filter с tpl-шаблонами в базовой request specification.
- Request/response body описаны DTO-классами.
- В тестах есть проверки status code, JSON path и десериализованных моделей.

## Требования

- JDK 21.
- Доступ в интернет, так как тестируется внешний API.
- Gradle устанавливать отдельно не нужно, используется Gradle Wrapper: `./gradlew`.
- Allure CLI отдельно устанавливать не нужно: генерация отчета выполняется через Gradle-плагин.

## Структура проекта

```text
src/test/java/ru/at/backend/config          # RestAssured baseURI, request/response specifications
src/test/java/ru/at/backend/client          # API-клиент с endpoint-only запросами
src/test/java/ru/at/backend/endpoints       # константы endpoint'ов
src/test/java/ru/at/backend/model           # enum и DTO request/response body
src/test/java/ru/at/backend/support         # тестовые support-модели
src/test/java/ru/at/backend/tests           # JUnit 5 тесты
src/test/resources/tpl                      # Allure REST Assured Freemarker-шаблоны
src/test/resources/allure.properties        # директория Allure results
notifications                               # Telegram-уведомления Jenkins по Allure summary
Jenkinsfile                                 # CI pipeline для backend API-тестов
```

## Быстрый запуск

Запуск всех тестов:

```bash
./gradlew clean test
```

Запуск с явным API URL:

```bash
./gradlew clean test -DbaseUri=https://practice.expandtesting.com/notes/api
```

Запуск конкретного тестового класса:

```bash
./gradlew test --tests ru.at.backend.tests.NotesTest
```

Запуск конкретного теста:

```bash
./gradlew test --tests ru.at.backend.tests.AuthTest.shouldRegisterAndLoginNewUser
```

## Параметры запуска

| Параметр | Значение по умолчанию | Описание |
| --- | --- | --- |
| `baseUri` | `https://practice.expandtesting.com/notes/api` | Базовый URI тестируемого API |
| `baseUrl` | не задан | Алиас для `baseUri`, оставлен для совместимости |
| `BASE_URI` | не задан | Environment variable для Jenkins/CI |

Приоритет конфигурации: `-DbaseUri`, затем `-DbaseUrl`, затем `BASE_URI`, затем значение по умолчанию.

## Отчеты

JUnit HTML-отчет после запуска тестов:

```text
build/reports/tests/test/index.html
```

Allure results сохраняются в:

```text
build/allure-results
```

Сгенерировать Allure HTML-отчет:

```bash
./gradlew allureReport
```

Готовый Allure HTML-отчет будет лежать здесь:

```text
build/reports/allure-report/allureReport
```

Запустить тесты и сразу собрать Allure HTML-отчет:

```bash
./gradlew clean test allureReport
```

Открыть Allure-отчет через локальный сервер Gradle-плагина:

```bash
./gradlew allureServe
```

Запустить тесты и после них открыть Allure-отчет через локальный сервер:

```bash
./gradlew clean allureServeWithTests
```

## Jenkins

`Jenkinsfile` запускает backend API-тесты без браузера, Selenoid и видео. Нужен Jenkins-agent с JDK 21 и доступом в интернет.

Параметры job:

- `BASE_URI=https://practice.expandtesting.com/notes/api` - базовый URI API.

Основной шаг pipeline:

```bash
./gradlew clean test -DbaseUri="$BASE_URI" --no-daemon
```

После тестов Jenkins:

- публикует JUnit XML из `build/test-results/test/*.xml`;
- генерирует Allure HTML-отчет;
- публикует Allure results через Jenkins Allure plugin;
- архивирует JUnit/Allure артефакты;
- отправляет Telegram-уведомление, если найден `summary.json`.

## Telegram-уведомления в Jenkins

После выполнения тестов Jenkins запускает `notifications/allure-notifications-4.11.0.jar`. Базовый шаблон лежит в `notifications/config.json`, но токен бота и chat id в репозиторий не записываются.

В Jenkins создайте два `Secret text` credentials:

- `telegram-bot-token-rodneystone` - токен Telegram-бота.
- `telegram-chat-id-rodneystone` - id чата, куда бот отправляет отчет.

Runtime-конфиг `notifications/config-runtime.json` создается Jenkinsfile автоматически и игнорируется в git.

## Allure-разметка

В проекте используются:

- `@Epic`, `@Feature`, `@Story` для группировки тестов.
- `@DisplayName`, `@Owner`, `@Severity` для описания тест-кейсов.
- `@Step` в API-клиенте для отображения запросов в Allure.
- `AllureRestAssured` для вложений HTTP request/response.

В request-шаблоне заголовки `x-auth-token` и `Authorization` маскируются.

## Полная проверка проекта

```bash
./gradlew clean test allureReport
```

Ожидаемый результат:

```text
BUILD SUCCESSFUL
```
