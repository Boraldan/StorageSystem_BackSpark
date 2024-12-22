CREATE TABLE t_sock (
                        id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                        model VARCHAR(255),
                        color VARCHAR(50),
                        cotton_percentage INTEGER CHECK (cotton_percentage >= 0 AND cotton_percentage <= 100),
                        quantity INTEGER CHECK (quantity >= 0),
                        is_active BOOLEAN DEFAULT TRUE
);
