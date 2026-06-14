# 🚀 API автотесты Notes API

Проект содержит backend API-автотесты для учебного сервиса [practice.expandtesting.com Notes API](https://practice.expandtesting.com/notes/api). Тесты написаны на Java 21 с использованием JUnit 5, RestAssured и Allure.

---

## ☕ Технологии и инструменты

| Технология | Назначение |
|------------|-----------|
| ☕ Java 21 | Язык программирования |
| 🏗️ Gradle | Система сборки проекта |
| 🧪 JUnit 5 | Фреймворк для модульного тестирования |
| 🌐 RestAssured | Библиотека для тестирования REST API |
| 📊 Allure | Фреймворк для генерации отчетов о тестировании |
| 🔄 Jenkins | CI/CD сервер для автоматизации сборок |
| 📱 Telegram Bot | Уведомления о результатах тестирования |

---

## 📠 Реализованные проверки

Реализовано **14 API-тестов** по основным группам:

### ✅ Health Check
- Проверка доступности API через `GET /health-check`

### 🔐 Авторизация и регистрация
- Регистрация нового пользователя
- Логин существующего пользователя
- Получение профиля пользователя
- Обновление профиля
- Logout
- Негативные сценарии авторизации (неверный пароль, несуществующий пользователь)

### 📝 Работа с заметками (CRUD)
- Создание новой заметки (`POST`)
- Получение заметки по id (`GET`)
- Получение списка всех заметок (`GET`)
- Обновление заметки через `PUT`
- Изменение статуса `completed` через `PATCH`
- Удаление заметки через `DELETE`
- Негативный сценарий создания заметки без авторизации

---

## 🔄 Jenkins

Сборка и запуск тестов автоматизированы через Jenkins.

🔗 **Ссылка на Jenkins job:**  
[40-rodneystone-diplom-backend](https://jenkins.autotests.cloud/job/40-rodneystone-diplom-backend/)

<!-- TODO: Добавить скриншот Jenkins job -->
<!-- ![Jenkins Job](docs/images/jenkins-job.png) -->

---

## 📊 Allure Report

После выполнения тестов автоматически генерируется Allure-отчет с детальной информацией о каждом тесте, включая request/response body, скриншоты и логи.

🔗 **Ссылка на Allure Report:**  
[Allure Report - Build #7](https://jenkins.autotests.cloud/job/40-rodneystone-diplom-backend/7/allure/)

<!-- TODO: Добавить скриншот Allure Report -->
<!-- ![Allure Report](docs/images/allure-report.png) -->

---

## 📱 Telegram-уведомления

После выполнения тестов Jenkins автоматически отправляет уведомление в Telegram с результатами тестирования через бота.

🤖 **Telegram бот:** [@rodneystone-at-bot](https://t.me/rodneystone-at-bot)

<!-- TODO: Добавить скриншот Telegram уведомления -->
<!-- ![Telegram Notification](docs/images/telegram-notification.png) -->

---

## ✅ Полная проверка проекта

```bash
./gradlew clean test allureReport
