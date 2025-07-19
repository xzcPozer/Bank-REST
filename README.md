# Bank-REST

## Запуск

Для запуска в корне проекта есть `docker-compose.yml` файл, который содержит в себе сервисы для запуска бд, api
```bash
docker compose up -d
```

## Тестирование api

при переходе на `http://localhost:8088/api/v1/swagger-ui/index.html` 
для авторизации нужно вызвать `/users/login`

с ролью `ADMIN`
```json
"email" : "user1@mail.com"
"password" : "password"
```

с ролью `USER`
```json
"email" : "user1@mail.com"
"password" : "password"
```

затем нужно скопировать и вставить JWT токен в `Authorize`
