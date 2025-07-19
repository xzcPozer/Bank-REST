CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO _user (
    first_name,
    last_name,
    email,
    password
) VALUES
      (
          'Петр',
          'Петров',
          'user1@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Сидор',
          'Сидоров',
          'user2@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Анна',
          'Кузнецова',
          'user3@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Мария',
          'Васильева',
          'user4@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Дмитрий',
          'Смирнов',
          'user5@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Елена',
          'Попова',
          'user6@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Алексей',
          'Козлов',
          'user7@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Ольга',
          'Лебедева',
          'user8@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Сергей',
          'Морозов',
          'user9@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Татьяна',
          'Николаева',
          'user10@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Андрей',
          'Федоров',
          'user11@mail.com',
          crypt('password', gen_salt('bf', 10))
      ),
      (
          'Ирина',
          'Захарова',
          'user12@mail.com',
          crypt('password', gen_salt('bf', 10))
      );