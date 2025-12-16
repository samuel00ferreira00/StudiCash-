-- =============================================
-- StudyCash - Script de Criação da Base de Dados
-- Gestão Financeira para Estudantes
-- =============================================

-- Criar base de dados
CREATE DATABASE IF NOT EXISTS studycash
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE studycash;

-- =============================================
-- Tabela: users (Utilizadores)
-- Armazena informações dos utilizadores do sistema
-- =============================================
CREATE TABLE IF NOT EXISTS users (
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
CREATE TABLE IF NOT EXISTS carteira (
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
CREATE TABLE IF NOT EXISTS categoria (
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
CREATE TABLE IF NOT EXISTS transacao (
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
CREATE TABLE IF NOT EXISTS meta (
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
CREATE TABLE IF NOT EXISTS orcamento (
    id_orcamento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    id_carteira BIGINT DEFAULT NULL,
    mes VARCHAR(20) NOT NULL COMMENT 'Formato: YYYY-MM ou nome do mês',
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
-- Triggers para atualização automática do saldo
-- =============================================

-- Trigger: Atualizar saldo após inserir transação
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_after_insert_transacao
AFTER INSERT ON transacao
FOR EACH ROW
BEGIN
    IF NEW.tipo = 'Receita' THEN
        UPDATE carteira SET saldo = saldo + NEW.valor WHERE id_carteira = NEW.id_carteira;
    ELSE
        UPDATE carteira SET saldo = saldo - NEW.valor WHERE id_carteira = NEW.id_carteira;
    END IF;
END//
DELIMITER ;

-- Trigger: Atualizar saldo após eliminar transação
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_after_delete_transacao
AFTER DELETE ON transacao
FOR EACH ROW
BEGIN
    IF OLD.tipo = 'Receita' THEN
        UPDATE carteira SET saldo = saldo - OLD.valor WHERE id_carteira = OLD.id_carteira;
    ELSE
        UPDATE carteira SET saldo = saldo + OLD.valor WHERE id_carteira = OLD.id_carteira;
    END IF;
END//
DELIMITER ;

-- =============================================
-- Fim do Script de Criação
-- =============================================

