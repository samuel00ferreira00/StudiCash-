-- Dados de exemplo para o banco de dados studyCash

-- UTILIZADORES
INSERT INTO utilizadores (nome_do_utilizador, email, senha) VALUES
('Samuel Ferreira', 'samuel.ferreira@email.com', '123456'),
('Ana Santos', 'ana.santos@email.com', 'senha123'),
('João Pereira', 'joao.pereira@email.com', 'abc123');

-- TIPO_CONTA (Corrente ou Poupança)
INSERT INTO tipo_conta (nome_tipo, data_criacao) VALUES
('Conta Corrente', '2025-01-01'),
('Conta Poupança', '2025-01-01');

-- CONTAS
INSERT INTO contas (nome_conta, saldo, data_criacao, id_utilizador, id_tipo_conta) VALUES
('Conta Principal Samuel', 2500.00, '2025-01-05', 1, 1),
('Poupança Samuel', 1200.00, '2025-02-01', 1, 2),
('Conta Corrente Ana', 1800.00, '2025-01-10', 2, 1),
('Poupança Ana', 900.00, '2025-03-01', 2, 2),
('Conta João', 1500.00, '2025-02-15', 3, 1);

-- TIPO_TRANSACAO (Receitas e Despesas)
INSERT INTO tipo_transacao (nome_tipo, data_criacao) VALUES
('Receita', '2025-01-01'),
('Despesa', '2025-01-01');

-- TRANSACOES
INSERT INTO transacoes (descricao, valor, data_transacao, id_conta, id_tipo_transacao) VALUES
('Salário de Janeiro', 1500.00, '2025-01-31', 1, 1),
('Compra no supermercado', 80.00, '2025-02-02', 1, 2),
('Pagamento de energia', 45.00, '2025-02-10', 1, 2),
('Depósito poupança', 200.00, '2025-02-15', 2, 1),
('Aluguel apartamento', 650.00, '2025-03-01', 3, 2),
('Freelance design', 400.00, '2025-03-15', 3, 1),
('Cinema', 25.00, '2025-03-20', 4, 2),
('Venda de acessórios', 150.00, '2025-04-01', 5, 1);

-- TRANSFERENCIAS (Entre contas)
INSERT INTO transferencias (valor, data_transferencia, descricao, id_conta_origem, id_conta_destino) VALUES
(200.00, '2025-03-05', 'Transferência para poupança Samuel', 1, 2),
(150.00, '2025-03-10', 'Transferência de Ana para João', 3, 5);

-- METAS (viagens, acessórios eletrónicos, etc.)
INSERT INTO metas (titulo, descricao, categoria, valor_objetivo, valor_atual, progresso, data_criacao, data_limite, id_utilizador) VALUES
('Viagem a Paris', 'Guardar para uma viagem a Paris em 2026', 'Viagem', 3000.00, 1200.00, 40.00, '2025-02-01', '2026-05-01', 1),
('Novo Smartphone', 'Compra de um iPhone 15 Pro', 'Acessórios eletrónicos', 1500.00, 300.00, 20.00, '2025-01-15', '2025-10-01', 2),
('Laptop Gamer', 'Atualizar o laptop para um modelo mais potente', 'Acessórios eletrónicos', 2000.00, 800.00, 40.00, '2025-03-10', '2025-12-31', 3);

-- ORCAMENTOS
INSERT INTO orcamentos (nome_orcamento, valor_total, progresso, data_criacao, id_utilizador) VALUES
('Orçamento Mensal Samuel', 2000.00, 75.00, '2025-02-01', 1),
('Orçamento Viagem Ana', 1500.00, 60.00, '2025-02-15', 2),
('Orçamento Geral João', 1800.00, 80.00, '2025-03-01', 3);

-- ORCAMENTO_TRANSACAO
INSERT INTO orcamento_transacao (id_orcamento, id_transacao, data_associacao) VALUES
(1, 2, '2025-02-02'),
(1, 3, '2025-02-10'),
(2, 7, '2025-03-20'),
(3, 6, '2025-03-15');
