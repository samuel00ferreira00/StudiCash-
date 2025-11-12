
-- 1. Listar todos os utilizadores
SELECT id, nome_do_utilizador, email
FROM utilizadores;

-- 2. Mostrar todas as contas e o nome do respetivo utilizador
SELECT c.id, c.nome_conta, c.saldo, u.nome_do_utilizador
FROM contas c
JOIN utilizadores u ON c.id_utilizador = u.id;

-- 3. Mostrar as contas de poupança de cada utilizador
SELECT u.nome_do_utilizador, c.nome_conta, c.saldo
FROM contas c
JOIN tipo_conta tc ON c.id_tipo_conta = tc.id
JOIN utilizadores u ON c.id_utilizador = u.id
WHERE tc.nome_tipo = 'Conta Poupança';

-- 4. Listar todas as despesas (tipo_transacao = Despesa)
SELECT t.descricao, t.valor, t.data_transacao, c.nome_conta
FROM transacoes t
JOIN tipo_transacao tt ON t.id_tipo_transacao = tt.id
JOIN contas c ON t.id_conta = c.id
WHERE tt.nome_tipo = 'Despesa'
ORDER BY t.data_transacao DESC;

-- 5. Calcular o total de receitas e despesas de um utilizador (ex: Samuel)
SELECT 
    u.nome_do_utilizador,
    SUM(CASE WHEN tt.nome_tipo = 'Receita' THEN t.valor ELSE 0 END) AS total_receitas,
    SUM(CASE WHEN tt.nome_tipo = 'Despesa' THEN t.valor ELSE 0 END) AS total_despesas
FROM transacoes t
JOIN contas c ON t.id_conta = c.id
JOIN utilizadores u ON c.id_utilizador = u.id
JOIN tipo_transacao tt ON t.id_tipo_transacao = tt.id
WHERE u.nome_do_utilizador = 'Samuel Ferreira'
GROUP BY u.nome_do_utilizador;

-- 6. Transações acima de 100 €
SELECT descricao, valor, data_transacao
FROM transacoes
WHERE valor > 100
ORDER BY valor DESC;

-- 7. Listar todas as transferências entre contas
SELECT t.id, t.valor, t.data_transferencia, 
       co.nome_conta AS conta_origem,
       cd.nome_conta AS conta_destino,
       t.descricao
FROM transferencias t
JOIN contas co ON t.id_conta_origem = co.id
JOIN contas cd ON t.id_conta_destino = cd.id
ORDER BY t.data_transferencia DESC;

-- 8. Metas de cada utilizador e respetivo progresso
SELECT u.nome_do_utilizador, m.titulo, m.categoria, m.valor_objetivo, 
       m.valor_atual, CONCAT(m.progresso, '%') AS progresso
FROM metas m
JOIN utilizadores u ON m.id_utilizador = u.id
ORDER BY m.progresso DESC;

-- 9. Orçamentos com maior progresso
SELECT o.nome_orcamento, u.nome_do_utilizador, o.valor_total, o.progresso
FROM orcamentos o
JOIN utilizadores u ON o.id_utilizador = u.id
ORDER BY o.progresso DESC
LIMIT 5;

-- 10. Transações associadas a um orçamento específico
SELECT o.nome_orcamento, t.descricao, t.valor, t.data_transacao
FROM orcamento_transacao ot
JOIN orcamentos o ON ot.id_orcamento = o.id
JOIN transacoes t ON ot.id_transacao = t.id
WHERE o.nome_orcamento = 'Orçamento Mensal Samuel';
