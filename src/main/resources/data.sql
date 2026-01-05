-- ========== Dados Iniciais do Sistema ==========

-- Inserir Usuários
INSERT INTO usuarios (name, email, password, role, class_name, balance, phone, created_at) VALUES
('João Silva', 'joao.silva@sesi.edu.br', 'aluno123', 'STUDENT', '3º Ano A', 150.00, '11987654321', NOW()),
('Maria Santos', 'maria.santos@sesi.edu.br', 'aluno123', 'STUDENT', '2º Ano B', 200.00, '11912345678', NOW()),
('Administrador Cantina', 'admin@sesi.edu.br', 'admin123', 'ADMIN', NULL, NULL, NULL, NOW());

-- Inserir Produtos
INSERT INTO produtos (name, description, price, category, image, available, stock) VALUES
('Salgado Assado', 'Coxinha, esfiha ou risoles assados', 4.50, 'Salgados', 'https://images.unsplash.com/photo-1579954115545-a95591f28bfc?w=400', true, 25),
('Salgado Frito', 'Coxinha, pastel ou bolinha de queijo', 5.00, 'Salgados', 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400', true, 30),
('Hambúrguer', 'Hambúrguer artesanal com queijo', 12.00, 'Lanches', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400', true, 15),
('Hot Dog', 'Hot dog completo com batata palha', 8.00, 'Lanches', 'https://images.unsplash.com/photo-1612392062798-2510c7b11573?w=400', true, 20),
('Sanduíche Natural', 'Pão integral com peito de peru e queijo', 7.50, 'Lanches', 'https://images.unsplash.com/photo-1509722747041-616f39b57569?w=400', true, 18),
('Suco Natural', 'Laranja, limão ou morango - 300ml', 5.50, 'Bebidas', 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400', true, 40),
('Refrigerante Lata', 'Coca-Cola, Guaraná ou Fanta - 350ml', 4.00, 'Bebidas', 'https://images.unsplash.com/photo-1629203851122-3726ecdf080e?w=400', true, 50),
('Água Mineral', 'Água mineral sem gás - 500ml', 2.50, 'Bebidas', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400', true, 60),
('Açaí no Copo', 'Açaí com banana e granola - 300ml', 10.00, 'Sobremesas', 'https://images.unsplash.com/photo-1590301157890-4810ed352733?w=400', true, 12),
('Brownie', 'Brownie de chocolate com nozes', 6.00, 'Sobremesas', 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400', true, 22),
('Sorvete', 'Picolé de frutas variadas', 3.50, 'Sobremesas', 'https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400', true, 35),
('Salada de Frutas', 'Mix de frutas frescas - 250g', 8.00, 'Sobremesas', 'https://images.unsplash.com/photo-1564093497595-593b96d80180?w=400', true, 10);