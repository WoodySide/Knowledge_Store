DELETE FROM public.users;
INSERT  INTO public.users(user_id, created_at,
                         updated_at, is_active,
                         email, first_name,
                         is_email_verified,
                         last_name, password, username)
VALUES (1, current_timestamp, current_timestamp, TRUE,
        'alexwoodyside@gmail.com', 'Alex', TRUE, 'Ivanov', '$2a$10$7zM6arzlLK/wn4hqPFn1EecmNdQkLe4GdEFLBqsZhIIZzvXuUq6iS', 'bobby');
INSERT INTO public.users(user_id, created_at,
                         updated_at, is_active,
                         email, first_name,
                         is_email_verified,
                         last_name, password, username)
VALUES (2, current_timestamp, current_timestamp, TRUE,
        'whatever@mail.ru', 'Steven', TRUE, 'Petrov', '$2a$10$.FJPOp79RdeSvs8MdOARqeaNLrbC1FaNTZsqMO9ZsIyaWlEj/CJgq', 'steve')
