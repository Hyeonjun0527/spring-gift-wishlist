
-- 이메일: admin@example.com
-- 비밀번호: admin123 (SHA-256 해시: jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=)
INSERT INTO member (email, password, role, created_at) VALUES 
('admin@example.com', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'ADMIN', CURRENT_TIMESTAMP);

INSERT INTO product (name, price, image_url) VALUES
('카카오 라이언 인형', 15000, 'https://example.com/ryan.jpg'),
('카카오 어피치 인형', 12000, 'https://example.com/apeach.jpg'),
('무지 머그컵', 8000, 'https://example.com/muzi-cup.jpg'),
('프로도 키링', 5000, 'https://example.com/frodo-keyring.jpg'),
('네오 쿠션', 20000, 'https://example.com/neo-cushion.jpg'); 