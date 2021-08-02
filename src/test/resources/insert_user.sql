DELETE FROM public.users;
INSERT INTO public.users(user_id, created_at,
                         updated_at, is_active,
                         email, first_name,
                         is_email_verified,
                         last_name, password, username)
VALUES (1, current_timestamp, current_timestamp, TRUE,
        'abc@gmail.com', 'Alex', TRUE, 'Ivanov', 'secret', 'alex123')
