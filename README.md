# Агрегатор Маршрутного такси
## Back End part
### Для запуска проекта
1. Создайте Базу данных minibuses в СУБД PostgreSQL версии >= 10. 
Для создания базы данных можно использовать файл ./DBMS/createDB.sql, в комментариях в этом файле приведен образец коммандной строки.
2. Создайте схемы. Для создания схем можно использовать файл ./DBMS/createSchema.sql, в комментариях в этом файле приведен образец коммандной строки
3. Только после создания базы данных и схем Java запустится без ошибок.
### Также необходимо выполнить следующее
1. При создании базы данных minibuses указать правильный Host, где работает сервер базы данных PostgreSQL (опция -h);
2. Указать нужного владельца базы данных minibuses (owner = ваш владелец, файл ./DBMS/createDB.sql, строка 9);
3. Указать нужных владельцев в Schema (authorization);
4. В файле ./src/main/resources/application.yaml задать параметры подключения к базе данных которые были сформированы в вышеуказанных пунктах.
### Дополнительная информация
1. Spring Boot version 2.5.7
2. Maven version 3.9.6
3. PostgreSQL 15.7 on powerpc-apple-darwin9.8.0, compiled by powerpc-apple-darwin9-gcc-4.2.1 (GCC) 4.2.1 (Apple Inc. build 5577), 32-bit.