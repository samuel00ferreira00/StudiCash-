-- =============================================
-- STUDYCASH - SCRIPT COMPLETO DA BASE DE DADOS
-- Aplicação de Gestão Financeira para Estudantes
-- Universidade Europeia IADE - Engenharia Informática
-- =============================================
-- Autores:
-- • Samuel Ferreira - 20220755
-- • Constantino Chipopa - 20241231
-- • Gilma Mulanda - 20241087
-- • Lueji Covilhã - 20241725
-- • Marlinda Congo - 20241718
-- =============================================

-- =============================================
-- PARTE 1: CRIAÇÃO DA BASE DE DADOS E TABELAS
-- =============================================

-- Eliminar base de dados se existir (para reinstalação limpa)
DROP DATABASE IF EXISTS studycash;

-- Criar base de dados
CREATE DATABASE studycash
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE studycash;

-- =============================================
-- Tabela: users (Utilizadores)
-- Armazena informações dos utilizadores do sistema
-- =============================================
CREATE TABLE users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    notificacoes TINYINT(1) DEFAULT 1,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabela: carteira (Carteira do Utilizador)
-- Cada utilizador possui uma carteira com saldo
-- =============================================
CREATE TABLE carteira (
    id_carteira INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    saldo DOUBLE NOT NULL DEFAULT 0.00,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_user) REFERENCES users(id_user) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    INDEX idx_user (id_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabela: categoria (Categorias de Transações)
-- Categorias para organizar receitas e despesas
-- =============================================
CREATE TABLE categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL COMMENT 'Receita ou Despesa',
    icone VARCHAR(50) DEFAULT NULL,
    
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabela: transacao (Transações Financeiras)
-- Registos de receitas e despesas dos utilizadores
-- =============================================
CREATE TABLE transacao (
    id_transacao BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_carteira INT NOT NULL,
    id_categoria BIGINT DEFAULT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor DOUBLE NOT NULL,
    tipo VARCHAR(50) NOT NULL COMMENT 'Receita ou Despesa',
    data_transacao DATE NOT NULL,
    localizacao VARCHAR(255) DEFAULT NULL,
    latitude DOUBLE DEFAULT NULL,
    longitude DOUBLE DEFAULT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_carteira) REFERENCES carteira(id_carteira) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) 
        ON DELETE SET NULL 
        ON UPDATE CASCADE,
    INDEX idx_carteira (id_carteira),
    INDEX idx_tipo (tipo),
    INDEX idx_data (data_transacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabela: meta (Metas Financeiras)
-- Objetivos de poupança dos utilizadores
-- =============================================
CREATE TABLE meta (
    id_meta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    valor_objetivo DOUBLE NOT NULL,
    valor_atual DOUBLE NOT NULL DEFAULT 0.00,
    data_inicio DATE DEFAULT NULL,
    data_fim DATE DEFAULT NULL,
    concluida TINYINT(1) DEFAULT 0,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_user) REFERENCES users(id_user) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    INDEX idx_user (id_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabela: orcamento (Orçamentos Mensais)
-- Limites de gastos mensais dos utilizadores
-- =============================================
CREATE TABLE orcamento (
    id_orcamento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    id_carteira BIGINT DEFAULT NULL,
    mes VARCHAR(20) NOT NULL COMMENT 'Formato: Mês Ano',
    limite DOUBLE NOT NULL,
    gasto_atual DOUBLE NOT NULL DEFAULT 0.00,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_user) REFERENCES users(id_user) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    INDEX idx_user (id_user),
    INDEX idx_mes (mes)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- PARTE 2: INSERÇÃO DE DADOS
-- =============================================

-- =============================================
-- Inserir Utilizadores (Equipa do Projeto + Extras)
-- =============================================
INSERT INTO users (nome, email, password, notificacoes) VALUES
('Samuel Ferreira', 'samuel@email.com', 'senha123', 1),
('Maria Silva', 'maria@email.com', 'senha123', 1),
('João Santos', 'joao@email.com', 'senha123', 1),
('Ana Costa', 'ana@email.com', 'senha123', 0),
('Pedro Oliveira', 'pedro@email.com', 'senha123', 1),
('Constantino Chipopa', 'constantino@email.com', 'senha123', 1),
('Gilma Mulanda', 'gilma@email.com', 'senha123', 1),
('Lueji Covilhã', 'lueji@email.com', 'senha123', 1),
('Marlinda Congo', 'marlinda@email.com', 'senha123', 1),
('Carlos Mendes', 'carlos@email.com', 'senha123', 1),
('Sofia Rodrigues', 'sofia@email.com', 'senha123', 1),
('Miguel Almeida', 'miguel@email.com', 'senha123', 0),
('Beatriz Lopes', 'beatriz@email.com', 'senha123', 1),
('Ricardo Sousa', 'ricardo@email.com', 'senha123', 1),
('Inês Martins', 'ines@email.com', 'senha123', 1);

-- =============================================
-- Inserir Carteiras (uma por utilizador)
-- =============================================
INSERT INTO carteira (id_user, saldo) VALUES
(1, 1250.50),   -- Samuel
(2, 890.00),    -- Maria
(3, 450.75),    -- João
(4, 2100.00),   -- Ana
(5, 320.50),    -- Pedro
(6, 1500.00),   -- Constantino
(7, 780.25),    -- Gilma
(8, 950.00),    -- Lueji
(9, 1100.75),   -- Marlinda
(10, 675.30),   -- Carlos
(11, 1890.00),  -- Sofia
(12, 520.00),   -- Miguel
(13, 1340.50),  -- Beatriz
(14, 980.75),   -- Ricardo
(15, 2250.00);  -- Inês

-- =============================================
-- Inserir Categorias Completas
-- =============================================
-- Categorias de Receita (IDs 1-10)
INSERT INTO categoria (nome, tipo, icone) VALUES
('Salário', 'Receita', '💰'),
('Mesada', 'Receita', '🎁'),
('Freelance', 'Receita', '💻'),
('Bolsa de Estudos', 'Receita', '🎓'),
('Investimentos', 'Receita', '📈'),
('Presente', 'Receita', '🎀'),
('Reembolso', 'Receita', '💵'),
('Venda de Itens', 'Receita', '🏷️'),
('Prémios', 'Receita', '🏆'),
('Outros Ganhos', 'Receita', '💵');

-- Categorias de Despesa (IDs 11-25)
INSERT INTO categoria (nome, tipo, icone) VALUES
('Alimentação', 'Despesa', '🍔'),
('Transporte', 'Despesa', '🚌'),
('Educação', 'Despesa', '📚'),
('Lazer', 'Despesa', '🎮'),
('Saúde', 'Despesa', '💊'),
('Vestuário', 'Despesa', '👕'),
('Tecnologia', 'Despesa', '📱'),
('Casa', 'Despesa', '🏠'),
('Assinaturas', 'Despesa', '📺'),
('Comunicações', 'Despesa', '📞'),
('Beleza', 'Despesa', '💄'),
('Desporto', 'Despesa', '⚽'),
('Viagens', 'Despesa', '✈️'),
('Presentes', 'Despesa', '🎁'),
('Outros Gastos', 'Despesa', '💸');

-- =============================================
-- TRANSAÇÕES DO SAMUEL (id_carteira = 1)
-- Histórico completo de 6 meses
-- =============================================

-- JULHO 2024
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(1, 1, 'Salário Part-time Café', 420.00, 'Receita', '2024-07-01', 'Café Central Lisboa', 38.7223, -9.1393),
(1, 2, 'Mesada Julho', 200.00, 'Receita', '2024-07-05', NULL, NULL, NULL),
(1, 11, 'Supermercado Continente', 85.50, 'Despesa', '2024-07-03', 'Continente Colombo', 38.7535, -9.2008),
(1, 12, 'Passe Metro Julho', 40.00, 'Despesa', '2024-07-01', 'Estação Saldanha', 38.7350, -9.1450),
(1, 14, 'Cinema NOS', 12.00, 'Despesa', '2024-07-08', 'NOS Colombo', 38.7535, -9.2008),
(1, 11, 'Almoço McDonald''s', 8.90, 'Despesa', '2024-07-10', 'McDonald''s Rossio', 38.7139, -9.1394),
(1, 19, 'Netflix', 13.99, 'Despesa', '2024-07-01', NULL, NULL, NULL),
(1, 19, 'Spotify', 6.99, 'Despesa', '2024-07-01', NULL, NULL, NULL),
(1, 23, 'Praia com amigos', 35.00, 'Despesa', '2024-07-15', 'Costa da Caparica', 38.6453, -9.2354),
(1, 11, 'Jantar Pizza Hut', 15.50, 'Despesa', '2024-07-20', 'Pizza Hut Amoreiras', 38.7200, -9.1600);

-- AGOSTO 2024
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(1, 1, 'Salário Part-time Café', 450.00, 'Receita', '2024-08-01', 'Café Central Lisboa', 38.7223, -9.1393),
(1, 2, 'Mesada Agosto', 200.00, 'Receita', '2024-08-05', NULL, NULL, NULL),
(1, 6, 'Presente Aniversário Avó', 100.00, 'Receita', '2024-08-15', NULL, NULL, NULL),
(1, 11, 'Supermercado Pingo Doce', 72.30, 'Despesa', '2024-08-02', 'Pingo Doce', 38.7200, -9.1400),
(1, 12, 'Passe Metro Agosto', 40.00, 'Despesa', '2024-08-01', 'Estação Saldanha', 38.7350, -9.1450),
(1, 14, 'Festival de Verão', 45.00, 'Despesa', '2024-08-10', 'Altice Arena', 38.7683, -9.0940),
(1, 23, 'Viagem Algarve', 120.00, 'Despesa', '2024-08-20', 'Albufeira', 37.0882, -8.2503),
(1, 16, 'Roupa de Verão Zara', 65.00, 'Despesa', '2024-08-12', 'Zara Chiado', 38.7107, -9.1403),
(1, 19, 'Netflix', 13.99, 'Despesa', '2024-08-01', NULL, NULL, NULL),
(1, 19, 'Spotify', 6.99, 'Despesa', '2024-08-01', NULL, NULL, NULL),
(1, 11, 'Gelados Santini', 8.50, 'Despesa', '2024-08-18', 'Santini Chiado', 38.7110, -9.1400);

-- SETEMBRO 2024
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(1, 1, 'Salário Part-time Café', 450.00, 'Receita', '2024-09-01', 'Café Central Lisboa', 38.7223, -9.1393),
(1, 2, 'Mesada Setembro', 200.00, 'Receita', '2024-09-05', NULL, NULL, NULL),
(1, 4, 'Bolsa de Estudo IADE', 500.00, 'Receita', '2024-09-15', 'IADE Lisboa', 38.7369, -9.1523),
(1, 11, 'Supermercado Lidl', 55.80, 'Despesa', '2024-09-03', 'Lidl Benfica', 38.7500, -9.2000),
(1, 12, 'Passe Metro Setembro', 40.00, 'Despesa', '2024-09-01', 'Estação Saldanha', 38.7350, -9.1450),
(1, 13, 'Livros Universitários', 95.00, 'Despesa', '2024-09-10', 'FNAC Colombo', 38.7535, -9.2008),
(1, 13, 'Material Escolar', 25.00, 'Despesa', '2024-09-08', 'Staples', 38.7400, -9.1500),
(1, 17, 'Capas e Acessórios Telemóvel', 22.00, 'Despesa', '2024-09-12', 'Worten', 38.7223, -9.1393),
(1, 19, 'Netflix', 13.99, 'Despesa', '2024-09-01', NULL, NULL, NULL),
(1, 19, 'Spotify', 6.99, 'Despesa', '2024-09-01', NULL, NULL, NULL),
(1, 14, 'Escape Room com colegas', 18.00, 'Despesa', '2024-09-20', 'Escape Room Lisboa', 38.7150, -9.1380);

-- OUTUBRO 2024
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(1, 1, 'Salário Part-time Café', 450.00, 'Receita', '2024-10-01', 'Café Central Lisboa', 38.7223, -9.1393),
(1, 2, 'Mesada Outubro', 200.00, 'Receita', '2024-10-05', NULL, NULL, NULL),
(1, 3, 'Projeto Freelance Website', 300.00, 'Receita', '2024-10-20', 'Remoto', NULL, NULL),
(1, 11, 'Supermercado Continente', 78.40, 'Despesa', '2024-10-04', 'Continente', 38.7200, -9.1400),
(1, 12, 'Passe Metro Outubro', 40.00, 'Despesa', '2024-10-01', 'Estação Saldanha', 38.7350, -9.1450),
(1, 11, 'Almoço RU IADE', 3.50, 'Despesa', '2024-10-07', 'Restaurante Universitário', 38.7365, -9.1520),
(1, 11, 'Almoço RU IADE', 3.50, 'Despesa', '2024-10-08', 'Restaurante Universitário', 38.7365, -9.1520),
(1, 11, 'Almoço RU IADE', 3.50, 'Despesa', '2024-10-09', 'Restaurante Universitário', 38.7365, -9.1520),
(1, 11, 'Café e Pastel', 2.80, 'Despesa', '2024-10-10', 'Pastelaria Versailles', 38.7340, -9.1460),
(1, 14, 'Halloween Party', 25.00, 'Despesa', '2024-10-31', 'LX Factory', 38.7040, -9.1780),
(1, 19, 'Netflix', 13.99, 'Despesa', '2024-10-01', NULL, NULL, NULL),
(1, 19, 'Spotify', 6.99, 'Despesa', '2024-10-01', NULL, NULL, NULL),
(1, 22, 'Ginásio Fitness Hut', 29.90, 'Despesa', '2024-10-01', 'Fitness Hut', 38.7300, -9.1500);

-- NOVEMBRO 2024
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(1, 1, 'Salário Part-time Café', 450.00, 'Receita', '2024-11-01', 'Café Central Lisboa', 38.7223, -9.1393),
(1, 2, 'Mesada Novembro', 200.00, 'Receita', '2024-11-05', NULL, NULL, NULL),
(1, 3, 'Freelance App Design', 250.00, 'Receita', '2024-11-15', 'Remoto', NULL, NULL),
(1, 11, 'Supermercado Auchan', 92.30, 'Despesa', '2024-11-02', 'Auchan', 38.7600, -9.2100),
(1, 12, 'Passe Metro Novembro', 40.00, 'Despesa', '2024-11-01', 'Estação Saldanha', 38.7350, -9.1450),
(1, 11, 'Jantar Burger King', 11.50, 'Despesa', '2024-11-05', 'Burger King', 38.7223, -9.1393),
(1, 14, 'Concerto NOS Alive', 55.00, 'Despesa', '2024-11-10', 'Altice Arena', 38.7683, -9.0940),
(1, 16, 'Casaco Inverno', 89.00, 'Despesa', '2024-11-12', 'Pull & Bear', 38.7107, -9.1403),
(1, 15, 'Farmácia Griponal', 8.50, 'Despesa', '2024-11-18', 'Farmácia', 38.7180, -9.1350),
(1, 24, 'Presente Aniversário Mãe', 45.00, 'Despesa', '2024-11-20', 'El Corte Inglés', 38.7285, -9.1492),
(1, 19, 'Netflix', 13.99, 'Despesa', '2024-11-01', NULL, NULL, NULL),
(1, 19, 'Spotify', 6.99, 'Despesa', '2024-11-01', NULL, NULL, NULL),
(1, 22, 'Ginásio', 29.90, 'Despesa', '2024-11-01', 'Fitness Hut', 38.7300, -9.1500),
(1, 11, 'Almoço RU', 3.50, 'Despesa', '2024-11-06', 'RU IADE', 38.7365, -9.1520),
(1, 11, 'Almoço RU', 3.50, 'Despesa', '2024-11-07', 'RU IADE', 38.7365, -9.1520);

-- DEZEMBRO 2024
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(1, 1, 'Salário Part-time Café', 450.00, 'Receita', '2024-12-01', 'Café Central Lisboa', 38.7223, -9.1393),
(1, 2, 'Mesada Dezembro', 200.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(1, 3, 'Projeto Website Freelance', 350.00, 'Receita', '2024-12-10', 'Remoto', NULL, NULL),
(1, 4, 'Bolsa de Mérito IADE', 500.00, 'Receita', '2024-12-15', 'IADE Lisboa', 38.7369, -9.1523),
(1, 6, 'Presente Natal Avós', 150.00, 'Receita', '2024-12-25', NULL, NULL, NULL),
(1, 11, 'Almoço no RU', 3.50, 'Despesa', '2024-12-02', 'Restaurante Universitário', 38.7365, -9.1520),
(1, 11, 'Café e Pão de Queijo', 2.80, 'Despesa', '2024-12-02', 'Padaria do Campus', 38.7360, -9.1525),
(1, 12, 'Passe Mensal Metro', 40.00, 'Despesa', '2024-12-01', 'Estação Saldanha', 38.7350, -9.1450),
(1, 13, 'Livro de Programação', 35.00, 'Despesa', '2024-12-08', 'FNAC Colombo', 38.7535, -9.2008),
(1, 14, 'Cinema com amigos', 8.50, 'Despesa', '2024-12-09', 'UCI Cinemas', 38.7540, -9.2010),
(1, 19, 'Netflix Mensal', 13.99, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(1, 19, 'Spotify Premium', 6.99, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(1, 17, 'Capa para Telemóvel', 15.00, 'Despesa', '2024-12-12', 'Worten', 38.7223, -9.1393),
(1, 24, 'Presentes de Natal', 120.00, 'Despesa', '2024-12-20', 'Centro Comercial Colombo', 38.7535, -9.2008),
(1, 11, 'Ceia de Natal', 35.00, 'Despesa', '2024-12-24', 'Casa', NULL, NULL),
(1, 22, 'Ginásio', 29.90, 'Despesa', '2024-12-01', 'Fitness Hut', 38.7300, -9.1500);

-- =============================================
-- TRANSAÇÕES DA MARIA (id_carteira = 2)
-- =============================================
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(2, 1, 'Estágio Remunerado Outubro', 600.00, 'Receita', '2024-10-01', 'Empresa Tech Lisboa', 38.7100, -9.1300),
(2, 1, 'Estágio Remunerado Novembro', 600.00, 'Receita', '2024-11-01', 'Empresa Tech Lisboa', 38.7100, -9.1300),
(2, 1, 'Estágio Remunerado Dezembro', 600.00, 'Receita', '2024-12-01', 'Empresa Tech Lisboa', 38.7100, -9.1300),
(2, 2, 'Mesada Outubro', 150.00, 'Receita', '2024-10-05', NULL, NULL, NULL),
(2, 2, 'Mesada Novembro', 150.00, 'Receita', '2024-11-05', NULL, NULL, NULL),
(2, 2, 'Mesada Dezembro', 150.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(2, 8, 'Venda Livros Usados', 45.00, 'Receita', '2024-11-10', 'OLX', NULL, NULL),
(2, 11, 'Supermercado Semanal', 45.00, 'Despesa', '2024-12-03', 'Continente', 38.7200, -9.1400),
(2, 11, 'Supermercado', 52.30, 'Despesa', '2024-12-10', 'Pingo Doce', 38.7200, -9.1400),
(2, 12, 'Uber para Universidade', 8.50, 'Despesa', '2024-12-04', 'Lisboa', 38.7223, -9.1393),
(2, 12, 'Passe Metro', 40.00, 'Despesa', '2024-12-01', 'Metro Lisboa', 38.7350, -9.1450),
(2, 15, 'Farmácia', 12.00, 'Despesa', '2024-12-06', 'Farmácia Central', 38.7180, -9.1350),
(2, 21, 'Cabeleireiro', 35.00, 'Despesa', '2024-12-08', 'Salão Beleza', 38.7150, -9.1400),
(2, 16, 'Roupa Bershka', 55.00, 'Despesa', '2024-12-12', 'Bershka', 38.7107, -9.1403),
(2, 19, 'Netflix', 13.99, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(2, 14, 'Jantar com amigas', 22.00, 'Despesa', '2024-12-14', 'Restaurante Italiano', 38.7180, -9.1400),
(2, 24, 'Presente Natal namorado', 80.00, 'Despesa', '2024-12-18', 'El Corte Inglés', 38.7285, -9.1492),
(2, 11, 'Café Starbucks', 4.50, 'Despesa', '2024-12-05', 'Starbucks', 38.7223, -9.1393),
(2, 11, 'Café Starbucks', 4.50, 'Despesa', '2024-12-09', 'Starbucks', 38.7223, -9.1393);

-- =============================================
-- TRANSAÇÕES DO JOÃO (id_carteira = 3)
-- =============================================
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(3, 2, 'Mesada Outubro', 250.00, 'Receita', '2024-10-01', NULL, NULL, NULL),
(3, 2, 'Mesada Novembro', 250.00, 'Receita', '2024-11-01', NULL, NULL, NULL),
(3, 2, 'Mesada Dezembro', 250.00, 'Receita', '2024-12-01', NULL, NULL, NULL),
(3, 6, 'Presente de Aniversário', 50.00, 'Receita', '2024-12-10', NULL, NULL, NULL),
(3, 9, 'Prémio Torneio FIFA', 30.00, 'Receita', '2024-11-15', 'Gaming Center', 38.7200, -9.1500),
(3, 11, 'McDonald''s', 9.50, 'Despesa', '2024-12-02', 'McDonald''s Rossio', 38.7139, -9.1394),
(3, 11, 'KFC', 11.00, 'Despesa', '2024-12-05', 'KFC', 38.7223, -9.1393),
(3, 14, 'Jogo PS5 FC 25', 69.99, 'Despesa', '2024-12-08', 'Game Store', 38.7223, -9.1393),
(3, 17, 'Carregador Telemóvel', 25.00, 'Despesa', '2024-12-11', 'MediaMarkt', 38.7535, -9.2008),
(3, 14, 'PlayStation Plus', 8.99, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(3, 19, 'Netflix', 13.99, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(3, 11, 'Pizza Telepizza', 12.50, 'Despesa', '2024-12-14', 'Telepizza', 38.7200, -9.1400),
(3, 14, 'Bilhete Futebol Benfica', 25.00, 'Despesa', '2024-12-15', 'Estádio da Luz', 38.7527, -9.1847),
(3, 12, 'Uber', 6.50, 'Despesa', '2024-12-15', 'Lisboa', 38.7223, -9.1393),
(3, 11, 'Burger King', 8.90, 'Despesa', '2024-12-18', 'Burger King', 38.7223, -9.1393);

-- =============================================
-- TRANSAÇÕES DA ANA (id_carteira = 4)
-- =============================================
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(4, 1, 'Salário Trabalho Remoto Outubro', 1200.00, 'Receita', '2024-10-01', 'Remoto', NULL, NULL),
(4, 1, 'Salário Trabalho Remoto Novembro', 1200.00, 'Receita', '2024-11-01', 'Remoto', NULL, NULL),
(4, 1, 'Salário Trabalho Remoto Dezembro', 1200.00, 'Receita', '2024-12-01', 'Remoto', NULL, NULL),
(4, 3, 'Tradução de Documentos', 150.00, 'Receita', '2024-12-12', 'Freelance', NULL, NULL),
(4, 3, 'Revisão de Textos', 80.00, 'Receita', '2024-11-20', 'Freelance', NULL, NULL),
(4, 5, 'Dividendos ETF', 45.00, 'Receita', '2024-12-15', NULL, NULL, NULL),
(4, 5, 'Juros Poupança', 12.50, 'Receita', '2024-12-01', NULL, NULL, NULL),
(4, 18, 'Renda Quarto', 350.00, 'Despesa', '2024-12-01', 'Lisboa', 38.7223, -9.1393),
(4, 18, 'Renda Quarto', 350.00, 'Despesa', '2024-11-01', 'Lisboa', 38.7223, -9.1393),
(4, 18, 'Renda Quarto', 350.00, 'Despesa', '2024-10-01', 'Lisboa', 38.7223, -9.1393),
(4, 11, 'Supermercado', 80.00, 'Despesa', '2024-12-05', 'Pingo Doce', 38.7200, -9.1400),
(4, 11, 'Supermercado', 75.00, 'Despesa', '2024-12-12', 'Lidl', 38.7500, -9.2000),
(4, 13, 'Curso Online Udemy', 12.99, 'Despesa', '2024-12-10', 'Online', NULL, NULL),
(4, 13, 'Livro Técnico Amazon', 28.00, 'Despesa', '2024-12-08', 'Amazon', NULL, NULL),
(4, 20, 'Tarifário Telemóvel', 15.00, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(4, 19, 'Adobe Creative Cloud', 29.99, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(4, 14, 'Teatro Nacional', 18.00, 'Despesa', '2024-12-14', 'Teatro D. Maria II', 38.7135, -9.1392),
(4, 11, 'Café e Brunch', 15.00, 'Despesa', '2024-12-16', 'Copenhagen Coffee Lab', 38.7100, -9.1350);

-- =============================================
-- TRANSAÇÕES DO PEDRO (id_carteira = 5)
-- =============================================
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(5, 2, 'Mesada', 180.00, 'Receita', '2024-12-01', NULL, NULL, NULL),
(5, 3, 'Explicações Matemática', 120.00, 'Receita', '2024-12-10', 'Centro Estudos', 38.7300, -9.1400),
(5, 11, 'Supermercado', 45.00, 'Despesa', '2024-12-03', 'Minipreço', 38.7200, -9.1400),
(5, 12, 'Passe Bus', 35.00, 'Despesa', '2024-12-01', 'Carris', 38.7223, -9.1393),
(5, 14, 'Bowling', 12.00, 'Despesa', '2024-12-07', 'Bowling Colombo', 38.7535, -9.2008),
(5, 11, 'Almoço', 7.50, 'Despesa', '2024-12-09', 'Cantina', 38.7365, -9.1520),
(5, 22, 'Material Desporto', 35.00, 'Despesa', '2024-12-12', 'Decathlon', 38.7600, -9.2100);

-- =============================================
-- TRANSAÇÕES DO CONSTANTINO (id_carteira = 6)
-- =============================================
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(6, 1, 'Trabalho Part-time', 550.00, 'Receita', '2024-12-01', 'Loja Centro', 38.7200, -9.1400),
(6, 4, 'Bolsa Estudo', 400.00, 'Receita', '2024-12-15', 'Universidade', 38.7369, -9.1523),
(6, 2, 'Mesada', 200.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(6, 18, 'Renda Quarto', 320.00, 'Despesa', '2024-12-01', 'Lisboa', 38.7223, -9.1393),
(6, 11, 'Supermercado', 95.00, 'Despesa', '2024-12-04', 'Continente', 38.7200, -9.1400),
(6, 12, 'Passe Metro', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(6, 13, 'Propinas Parciais', 150.00, 'Despesa', '2024-12-10', 'Universidade', 38.7369, -9.1523),
(6, 20, 'Internet Casa', 25.00, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(6, 11, 'Almoços Semana', 25.00, 'Despesa', '2024-12-08', 'Cantina', 38.7365, -9.1520);

-- =============================================
-- TRANSAÇÕES DA GILMA (id_carteira = 7)
-- =============================================
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(7, 2, 'Mesada', 250.00, 'Receita', '2024-12-01', NULL, NULL, NULL),
(7, 3, 'Design Gráfico Freelance', 180.00, 'Receita', '2024-12-12', 'Remoto', NULL, NULL),
(7, 8, 'Venda Roupa Usada', 35.00, 'Receita', '2024-12-08', 'Vinted', NULL, NULL),
(7, 11, 'Supermercado', 55.00, 'Despesa', '2024-12-03', 'Pingo Doce', 38.7200, -9.1400),
(7, 12, 'Passe Metro', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(7, 21, 'Maquilhagem Sephora', 45.00, 'Despesa', '2024-12-06', 'Sephora', 38.7107, -9.1403),
(7, 16, 'Roupa H&M', 38.00, 'Despesa', '2024-12-10', 'H&M', 38.7107, -9.1403),
(7, 14, 'Cinema', 8.50, 'Despesa', '2024-12-14', 'NOS', 38.7535, -9.2008),
(7, 19, 'Canva Pro', 11.99, 'Despesa', '2024-12-01', NULL, NULL, NULL);

-- =============================================
-- TRANSAÇÕES DA LUEJI (id_carteira = 8)
-- =============================================
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(8, 1, 'Trabalho Fins-de-semana', 280.00, 'Receita', '2024-12-01', 'Restaurante', 38.7100, -9.1300),
(8, 2, 'Mesada', 200.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(8, 4, 'Bolsa Social', 300.00, 'Receita', '2024-12-15', 'Universidade', 38.7369, -9.1523),
(8, 11, 'Supermercado', 68.00, 'Despesa', '2024-12-02', 'Auchan', 38.7600, -9.2100),
(8, 12, 'Passe Metro', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(8, 13, 'Material Escolar', 22.00, 'Despesa', '2024-12-08', 'Staples', 38.7400, -9.1500),
(8, 11, 'Almoço RU', 3.50, 'Despesa', '2024-12-04', 'RU', 38.7365, -9.1520),
(8, 11, 'Almoço RU', 3.50, 'Despesa', '2024-12-05', 'RU', 38.7365, -9.1520),
(8, 11, 'Almoço RU', 3.50, 'Despesa', '2024-12-06', 'RU', 38.7365, -9.1520),
(8, 20, 'Tarifário', 12.00, 'Despesa', '2024-12-01', NULL, NULL, NULL);

-- =============================================
-- TRANSAÇÕES DA MARLINDA (id_carteira = 9)
-- =============================================
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(9, 1, 'Estágio Curricular', 450.00, 'Receita', '2024-12-01', 'Empresa Marketing', 38.7150, -9.1380),
(9, 2, 'Mesada', 180.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(9, 3, 'Social Media Freelance', 200.00, 'Receita', '2024-12-15', 'Remoto', NULL, NULL),
(9, 11, 'Supermercado', 72.00, 'Despesa', '2024-12-03', 'Continente', 38.7200, -9.1400),
(9, 12, 'Passe Metro', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(9, 16, 'Roupa Stradivarius', 48.00, 'Despesa', '2024-12-07', 'Stradivarius', 38.7107, -9.1403),
(9, 21, 'Cabeleireiro', 40.00, 'Despesa', '2024-12-10', 'Salão', 38.7150, -9.1400),
(9, 14, 'Jantar Aniversário', 25.00, 'Despesa', '2024-12-12', 'Restaurante', 38.7180, -9.1400),
(9, 19, 'Spotify', 6.99, 'Despesa', '2024-12-01', NULL, NULL, NULL),
(9, 24, 'Presentes Natal', 65.00, 'Despesa', '2024-12-18', 'Centro Comercial', 38.7535, -9.2008);

-- =============================================
-- TRANSAÇÕES DOS OUTROS UTILIZADORES (10-15)
-- =============================================
-- Carlos (id_carteira = 10)
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(10, 2, 'Mesada', 220.00, 'Receita', '2024-12-01', NULL, NULL, NULL),
(10, 3, 'Aulas Guitarra', 100.00, 'Receita', '2024-12-10', 'Escola Música', 38.7200, -9.1500),
(10, 11, 'Supermercado', 48.00, 'Despesa', '2024-12-03', 'Lidl', 38.7500, -9.2000),
(10, 14, 'Instrumentos Música', 85.00, 'Despesa', '2024-12-08', 'Fnac', 38.7535, -9.2008),
(10, 12, 'Passe', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450);

-- Sofia (id_carteira = 11)
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(11, 1, 'Trabalho Part-time', 680.00, 'Receita', '2024-12-01', 'Loja Roupa', 38.7107, -9.1403),
(11, 2, 'Mesada', 200.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(11, 3, 'Fotografia Eventos', 250.00, 'Receita', '2024-12-15', 'Lisboa', 38.7223, -9.1393),
(11, 11, 'Supermercado', 85.00, 'Despesa', '2024-12-02', 'Continente', 38.7200, -9.1400),
(11, 17, 'Câmara Nova', 450.00, 'Despesa', '2024-12-10', 'Fnac', 38.7535, -9.2008),
(11, 12, 'Passe', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(11, 16, 'Roupa Zara', 95.00, 'Despesa', '2024-12-12', 'Zara', 38.7107, -9.1403);

-- Miguel (id_carteira = 12)
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(12, 2, 'Mesada', 200.00, 'Receita', '2024-12-01', NULL, NULL, NULL),
(12, 11, 'Supermercado', 42.00, 'Despesa', '2024-12-03', 'Minipreço', 38.7200, -9.1400),
(12, 14, 'Jogos Steam', 35.00, 'Despesa', '2024-12-08', 'Online', NULL, NULL),
(12, 12, 'Passe', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(12, 19, 'Game Pass', 14.99, 'Despesa', '2024-12-01', NULL, NULL, NULL);

-- Beatriz (id_carteira = 13)
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(13, 1, 'Estágio', 550.00, 'Receita', '2024-12-01', 'Hospital', 38.7400, -9.1600),
(13, 4, 'Bolsa Mérito', 350.00, 'Receita', '2024-12-15', 'Universidade', 38.7369, -9.1523),
(13, 2, 'Mesada', 150.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(13, 11, 'Supermercado', 78.00, 'Despesa', '2024-12-03', 'Continente', 38.7200, -9.1400),
(13, 13, 'Livros Medicina', 120.00, 'Despesa', '2024-12-08', 'Livraria', 38.7150, -9.1400),
(13, 12, 'Passe', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(13, 15, 'Farmácia', 25.00, 'Despesa', '2024-12-10', 'Farmácia', 38.7180, -9.1350);

-- Ricardo (id_carteira = 14)
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(14, 1, 'Trabalho Part-time', 420.00, 'Receita', '2024-12-01', 'Supermercado', 38.7200, -9.1400),
(14, 2, 'Mesada', 180.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(14, 11, 'Supermercado', 55.00, 'Despesa', '2024-12-02', 'Lidl', 38.7500, -9.2000),
(14, 22, 'Ginásio', 25.00, 'Despesa', '2024-12-01', 'Ginásio', 38.7300, -9.1500),
(14, 12, 'Passe', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(14, 14, 'Futebol', 15.00, 'Despesa', '2024-12-08', 'Campo', 38.7600, -9.2000);

-- Inês (id_carteira = 15)
INSERT INTO transacao (id_carteira, id_categoria, descricao, valor, tipo, data_transacao, localizacao, latitude, longitude) VALUES
(15, 1, 'Trabalho Remoto', 950.00, 'Receita', '2024-12-01', 'Remoto', NULL, NULL),
(15, 3, 'Consultoria', 350.00, 'Receita', '2024-12-10', 'Cliente', 38.7200, -9.1500),
(15, 2, 'Mesada', 200.00, 'Receita', '2024-12-05', NULL, NULL, NULL),
(15, 5, 'Dividendos', 55.00, 'Receita', '2024-12-15', NULL, NULL, NULL),
(15, 18, 'Renda', 380.00, 'Despesa', '2024-12-01', 'Lisboa', 38.7223, -9.1393),
(15, 11, 'Supermercado', 92.00, 'Despesa', '2024-12-03', 'Continente', 38.7200, -9.1400),
(15, 13, 'Curso Online', 49.00, 'Despesa', '2024-12-08', 'Coursera', NULL, NULL),
(15, 12, 'Passe', 40.00, 'Despesa', '2024-12-01', 'Metro', 38.7350, -9.1450),
(15, 23, 'Viagem Porto', 85.00, 'Despesa', '2024-12-14', 'Porto', 41.1579, -8.6291);

-- =============================================
-- Inserir Metas Financeiras
-- =============================================
INSERT INTO meta (id_user, nome, valor_objetivo, valor_atual, data_inicio, data_fim) VALUES
-- Metas do Samuel
(1, 'MacBook Pro para Programação', 2500.00, 850.00, '2024-01-01', '2025-06-30'),
(1, 'Viagem Erasmus Espanha', 3000.00, 1200.00, '2024-01-01', '2025-08-31'),
(1, 'Fundo de Emergência', 1000.00, 650.00, '2024-06-01', '2024-12-31'),
(1, 'Curso AWS Certification', 350.00, 200.00, '2024-09-01', '2025-03-31'),

-- Metas da Maria
(2, 'iPhone 15 Pro', 1400.00, 720.00, '2024-03-01', '2025-02-28'),
(2, 'Curso de Inglês Cambridge', 600.00, 350.00, '2024-01-01', '2024-12-31'),
(2, 'Férias Grécia', 1200.00, 400.00, '2024-06-01', '2025-07-31'),

-- Metas do João
(3, 'PlayStation 5 + Jogos', 650.00, 380.00, '2024-02-01', '2024-12-31'),
(3, 'Interrail Europa', 1500.00, 350.00, '2024-01-01', '2025-06-30'),
(3, 'Carta de Condução', 800.00, 200.00, '2024-09-01', '2025-06-30'),

-- Metas da Ana
(4, 'Carro Usado', 6000.00, 2800.00, '2024-01-01', '2025-12-31'),
(4, 'Fundo Investimento', 2000.00, 1200.00, '2024-06-01', '2025-06-30'),
(4, 'Mestrado', 5000.00, 1500.00, '2024-01-01', '2026-09-30'),

-- Metas dos outros utilizadores
(5, 'Bicicleta Elétrica', 1200.00, 520.00, '2024-04-01', '2025-04-30'),
(6, 'MacBook Air', 1500.00, 900.00, '2024-01-01', '2025-03-31'),
(7, 'Curso UX Design', 800.00, 500.00, '2024-05-01', '2025-01-31'),
(8, 'Viagem Angola', 1800.00, 600.00, '2024-06-01', '2025-08-31'),
(9, 'Câmara Profissional', 1200.00, 450.00, '2024-07-01', '2025-06-30'),
(10, 'Guitarra Gibson', 2000.00, 750.00, '2024-03-01', '2025-03-31'),
(11, 'Lente Fotográfica', 900.00, 600.00, '2024-08-01', '2025-02-28'),
(12, 'PC Gaming', 1800.00, 400.00, '2024-01-01', '2025-12-31'),
(13, 'Intercâmbio Brasil', 4000.00, 1200.00, '2024-01-01', '2025-08-31'),
(14, 'Equipamento Ginásio', 500.00, 320.00, '2024-09-01', '2025-03-31'),
(15, 'MBA Online', 8000.00, 2500.00, '2024-01-01', '2026-12-31');

-- =============================================
-- Inserir Orçamentos Mensais
-- =============================================
INSERT INTO orcamento (id_user, mes, limite, gasto_atual) VALUES
-- Histórico de orçamentos do Samuel
(1, 'Julho 2024', 350.00, 328.89),
(1, 'Agosto 2024', 400.00, 372.47),
(1, 'Setembro 2024', 400.00, 355.98),
(1, 'Outubro 2024', 400.00, 387.28),
(1, 'Novembro 2024', 450.00, 428.36),
(1, 'Dezembro 2024', 500.00, 389.27),

-- Orçamentos da Maria
(2, 'Outubro 2024', 280.00, 265.50),
(2, 'Novembro 2024', 300.00, 285.00),
(2, 'Dezembro 2024', 350.00, 318.49),

-- Orçamentos do João
(3, 'Outubro 2024', 180.00, 175.00),
(3, 'Novembro 2024', 200.00, 192.00),
(3, 'Dezembro 2024', 250.00, 234.88),

-- Orçamentos da Ana
(4, 'Outubro 2024', 550.00, 512.00),
(4, 'Novembro 2024', 550.00, 535.50),
(4, 'Dezembro 2024', 600.00, 558.98),

-- Orçamentos dos outros utilizadores
(5, 'Dezembro 2024', 200.00, 175.50),
(6, 'Dezembro 2024', 450.00, 410.00),
(7, 'Dezembro 2024', 280.00, 248.49),
(8, 'Dezembro 2024', 250.00, 222.50),
(9, 'Dezembro 2024', 350.00, 331.99),
(10, 'Dezembro 2024', 220.00, 188.00),
(11, 'Dezembro 2024', 500.00, 670.00),
(12, 'Dezembro 2024', 180.00, 166.99),
(13, 'Dezembro 2024', 400.00, 303.00),
(14, 'Dezembro 2024', 250.00, 180.00),
(15, 'Dezembro 2024', 600.00, 646.00);

-- =============================================
-- PARTE 3: CONSULTAS PRINCIPAIS (QUERIES)
-- =============================================

-- =============================================
-- CONSULTAS DE UTILIZADORES
-- =============================================

-- Listar todos os utilizadores com suas carteiras
SELECT 
    u.id_user,
    u.nome,
    u.email,
    c.saldo AS saldo_carteira,
    u.notificacoes
FROM users u
INNER JOIN carteira c ON u.id_user = c.id_user
ORDER BY u.nome;

-- Utilizadores com maior saldo
SELECT 
    u.nome,
    u.email,
    c.saldo
FROM users u
INNER JOIN carteira c ON u.id_user = c.id_user
ORDER BY c.saldo DESC
LIMIT 5;

-- =============================================
-- CONSULTAS DE TRANSAÇÕES
-- =============================================

-- Total de receitas e despesas por utilizador
SELECT 
    u.nome,
    SUM(CASE WHEN t.tipo = 'Receita' THEN t.valor ELSE 0 END) AS total_receitas,
    SUM(CASE WHEN t.tipo = 'Despesa' THEN t.valor ELSE 0 END) AS total_despesas,
    SUM(CASE WHEN t.tipo = 'Receita' THEN t.valor ELSE -t.valor END) AS balanco
FROM users u
INNER JOIN carteira c ON u.id_user = c.id_user
LEFT JOIN transacao t ON c.id_carteira = t.id_carteira
GROUP BY u.id_user, u.nome
ORDER BY balanco DESC;

-- Gastos por categoria (para gráficos)
SELECT 
    cat.nome AS categoria,
    cat.icone,
    COUNT(t.id_transacao) AS quantidade,
    SUM(t.valor) AS total_gasto
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
INNER JOIN categoria cat ON t.id_categoria = cat.id_categoria
WHERE c.id_user = 1 AND t.tipo = 'Despesa'
GROUP BY cat.id_categoria, cat.nome, cat.icone
ORDER BY total_gasto DESC;

-- Transações com localização GPS
SELECT 
    t.descricao,
    t.valor,
    t.tipo,
    t.data_transacao,
    t.localizacao,
    t.latitude,
    t.longitude
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
WHERE c.id_user = 1 
    AND t.latitude IS NOT NULL 
    AND t.longitude IS NOT NULL
ORDER BY t.data_transacao DESC;

-- Resumo financeiro mensal
SELECT 
    DATE_FORMAT(t.data_transacao, '%Y-%m') AS mes,
    SUM(CASE WHEN t.tipo = 'Receita' THEN t.valor ELSE 0 END) AS receitas,
    SUM(CASE WHEN t.tipo = 'Despesa' THEN t.valor ELSE 0 END) AS despesas,
    SUM(CASE WHEN t.tipo = 'Receita' THEN t.valor ELSE -t.valor END) AS saldo_mes
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
WHERE c.id_user = 1
GROUP BY DATE_FORMAT(t.data_transacao, '%Y-%m')
ORDER BY mes DESC;

-- =============================================
-- CONSULTAS DE METAS
-- =============================================

-- Metas de um utilizador com progresso
SELECT 
    m.id_meta,
    m.nome,
    m.valor_objetivo,
    m.valor_atual,
    ROUND((m.valor_atual / m.valor_objetivo) * 100, 2) AS percentagem_concluida,
    m.data_inicio,
    m.data_fim,
    DATEDIFF(m.data_fim, CURRENT_DATE()) AS dias_restantes
FROM meta m
WHERE m.id_user = 1
ORDER BY percentagem_concluida DESC;

-- Metas próximas de serem concluídas (>80%)
SELECT 
    u.nome AS utilizador,
    m.nome AS meta,
    m.valor_objetivo,
    m.valor_atual,
    ROUND((m.valor_atual / m.valor_objetivo) * 100, 2) AS percentagem
FROM meta m
INNER JOIN users u ON m.id_user = u.id_user
WHERE (m.valor_atual / m.valor_objetivo) >= 0.8
    AND m.valor_atual < m.valor_objetivo
ORDER BY percentagem DESC;

-- =============================================
-- CONSULTAS DE ORÇAMENTOS
-- =============================================

-- Orçamentos de um utilizador
SELECT 
    o.id_orcamento,
    o.mes,
    o.limite,
    o.gasto_atual,
    (o.limite - o.gasto_atual) AS disponivel,
    ROUND((o.gasto_atual / o.limite) * 100, 2) AS percentagem_gasta
FROM orcamento o
WHERE o.id_user = 1
ORDER BY o.mes DESC;

-- Orçamentos ultrapassados (alertas)
SELECT 
    u.nome,
    u.email,
    o.mes,
    o.limite,
    o.gasto_atual,
    (o.gasto_atual - o.limite) AS excedido
FROM orcamento o
INNER JOIN users u ON o.id_user = u.id_user
WHERE o.gasto_atual > o.limite
ORDER BY excedido DESC;

-- =============================================
-- ESTATÍSTICAS GERAIS
-- =============================================

-- Total de utilizadores
SELECT COUNT(*) AS total_utilizadores FROM users;

-- Total de transações no sistema
SELECT 
    COUNT(*) AS total_transacoes,
    SUM(CASE WHEN tipo = 'Receita' THEN valor ELSE 0 END) AS volume_receitas,
    SUM(CASE WHEN tipo = 'Despesa' THEN valor ELSE 0 END) AS volume_despesas
FROM transacao;

-- Categorias mais utilizadas
SELECT 
    cat.nome,
    cat.tipo,
    COUNT(t.id_transacao) AS vezes_utilizada,
    SUM(t.valor) AS valor_total
FROM categoria cat
LEFT JOIN transacao t ON cat.id_categoria = t.id_categoria
GROUP BY cat.id_categoria, cat.nome, cat.tipo
ORDER BY vezes_utilizada DESC
LIMIT 10;

-- =============================================
-- FIM DO SCRIPT STUDYCASH
-- =============================================
-- Resumo dos dados:
-- • 15 Utilizadores
-- • 15 Carteiras
-- • 25 Categorias
-- • 150+ Transações (6 meses de histórico)
-- • 25 Metas Financeiras
-- • 30+ Orçamentos
-- =============================================
