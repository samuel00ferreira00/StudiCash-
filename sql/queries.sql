-- =============================================
-- StudyCash - Principais Consultas (Queries)
-- Consultas úteis para análise de dados
-- =============================================

USE studycash;

-- =============================================
-- 1. CONSULTAS DE UTILIZADORES
-- =============================================

-- 1.1 Listar todos os utilizadores com suas carteiras
SELECT 
    u.id_user,
    u.nome,
    u.email,
    c.saldo AS saldo_carteira,
    u.notificacoes
FROM users u
INNER JOIN carteira c ON u.id_user = c.id_user
ORDER BY u.nome;

-- 1.2 Utilizadores com maior saldo
SELECT 
    u.nome,
    u.email,
    c.saldo
FROM users u
INNER JOIN carteira c ON u.id_user = c.id_user
ORDER BY c.saldo DESC
LIMIT 5;

-- 1.3 Verificar login (autenticação)
SELECT id_user, nome, email 
FROM users 
WHERE email = 'samuel@email.com' AND password = 'senha123';

-- =============================================
-- 2. CONSULTAS DE TRANSAÇÕES
-- =============================================

-- 2.1 Todas as transações de um utilizador
SELECT 
    t.id_transacao,
    t.descricao,
    t.valor,
    t.tipo,
    t.data_transacao,
    t.localizacao,
    cat.nome AS categoria
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
LEFT JOIN categoria cat ON t.id_categoria = cat.id_categoria
WHERE c.id_user = 1
ORDER BY t.data_transacao DESC;

-- 2.2 Total de receitas e despesas por utilizador
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

-- 2.3 Transações do mês atual
SELECT 
    t.descricao,
    t.valor,
    t.tipo,
    t.data_transacao,
    cat.nome AS categoria
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
LEFT JOIN categoria cat ON t.id_categoria = cat.id_categoria
WHERE c.id_user = 1
    AND MONTH(t.data_transacao) = MONTH(CURRENT_DATE())
    AND YEAR(t.data_transacao) = YEAR(CURRENT_DATE())
ORDER BY t.data_transacao DESC;

-- 2.4 Gastos por categoria (para gráficos)
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

-- 2.5 Receitas por categoria
SELECT 
    cat.nome AS categoria,
    cat.icone,
    COUNT(t.id_transacao) AS quantidade,
    SUM(t.valor) AS total_recebido
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
INNER JOIN categoria cat ON t.id_categoria = cat.id_categoria
WHERE c.id_user = 1 AND t.tipo = 'Receita'
GROUP BY cat.id_categoria, cat.nome, cat.icone
ORDER BY total_recebido DESC;

-- 2.6 Transações com localização GPS
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

-- 2.7 Últimas 10 transações
SELECT 
    t.descricao,
    t.valor,
    t.tipo,
    t.data_transacao,
    cat.nome AS categoria
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
LEFT JOIN categoria cat ON t.id_categoria = cat.id_categoria
WHERE c.id_user = 1
ORDER BY t.data_transacao DESC, t.id_transacao DESC
LIMIT 10;

-- =============================================
-- 3. CONSULTAS DE METAS
-- =============================================

-- 3.1 Metas de um utilizador com progresso
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

-- 3.2 Metas próximas de serem concluídas (>80%)
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

-- 3.3 Metas que vencem este mês
SELECT 
    u.nome AS utilizador,
    m.nome AS meta,
    m.valor_objetivo,
    m.valor_atual,
    m.data_fim
FROM meta m
INNER JOIN users u ON m.id_user = u.id_user
WHERE MONTH(m.data_fim) = MONTH(CURRENT_DATE())
    AND YEAR(m.data_fim) = YEAR(CURRENT_DATE())
ORDER BY m.data_fim;

-- =============================================
-- 4. CONSULTAS DE ORÇAMENTOS
-- =============================================

-- 4.1 Orçamentos de um utilizador
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

-- 4.2 Orçamentos ultrapassados (alertas)
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

-- 4.3 Orçamentos próximos do limite (>90%)
SELECT 
    u.nome,
    o.mes,
    o.limite,
    o.gasto_atual,
    ROUND((o.gasto_atual / o.limite) * 100, 2) AS percentagem
FROM orcamento o
INNER JOIN users u ON o.id_user = u.id_user
WHERE (o.gasto_atual / o.limite) >= 0.9
    AND o.gasto_atual <= o.limite
ORDER BY percentagem DESC;

-- =============================================
-- 5. CONSULTAS ESTATÍSTICAS E RELATÓRIOS
-- =============================================

-- 5.1 Resumo financeiro mensal
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

-- 5.2 Média de gastos por mês
SELECT 
    ROUND(AVG(gastos_mes), 2) AS media_gastos_mensais
FROM (
    SELECT 
        DATE_FORMAT(t.data_transacao, '%Y-%m') AS mes,
        SUM(t.valor) AS gastos_mes
    FROM transacao t
    INNER JOIN carteira c ON t.id_carteira = c.id_carteira
    WHERE c.id_user = 1 AND t.tipo = 'Despesa'
    GROUP BY DATE_FORMAT(t.data_transacao, '%Y-%m')
) AS gastos_mensais;

-- 5.3 Dia da semana com mais gastos
SELECT 
    DAYNAME(t.data_transacao) AS dia_semana,
    COUNT(*) AS num_transacoes,
    SUM(t.valor) AS total_gasto
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
WHERE c.id_user = 1 AND t.tipo = 'Despesa'
GROUP BY DAYNAME(t.data_transacao), DAYOFWEEK(t.data_transacao)
ORDER BY DAYOFWEEK(t.data_transacao);

-- 5.4 Top 5 maiores despesas
SELECT 
    t.descricao,
    t.valor,
    t.data_transacao,
    cat.nome AS categoria,
    t.localizacao
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
LEFT JOIN categoria cat ON t.id_categoria = cat.id_categoria
WHERE c.id_user = 1 AND t.tipo = 'Despesa'
ORDER BY t.valor DESC
LIMIT 5;

-- 5.5 Comparativo receitas vs despesas por mês (últimos 6 meses)
SELECT 
    DATE_FORMAT(t.data_transacao, '%Y-%m') AS mes,
    SUM(CASE WHEN t.tipo = 'Receita' THEN t.valor ELSE 0 END) AS receitas,
    SUM(CASE WHEN t.tipo = 'Despesa' THEN t.valor ELSE 0 END) AS despesas
FROM transacao t
INNER JOIN carteira c ON t.id_carteira = c.id_carteira
WHERE c.id_user = 1
    AND t.data_transacao >= DATE_SUB(CURRENT_DATE(), INTERVAL 6 MONTH)
GROUP BY DATE_FORMAT(t.data_transacao, '%Y-%m')
ORDER BY mes;

-- =============================================
-- 6. CONSULTAS ADMINISTRATIVAS
-- =============================================

-- 6.1 Total de utilizadores registados
SELECT COUNT(*) AS total_utilizadores FROM users;

-- 6.2 Total de transações no sistema
SELECT 
    COUNT(*) AS total_transacoes,
    SUM(CASE WHEN tipo = 'Receita' THEN valor ELSE 0 END) AS volume_receitas,
    SUM(CASE WHEN tipo = 'Despesa' THEN valor ELSE 0 END) AS volume_despesas
FROM transacao;

-- 6.3 Utilizadores mais ativos (por número de transações)
SELECT 
    u.nome,
    COUNT(t.id_transacao) AS num_transacoes
FROM users u
INNER JOIN carteira c ON u.id_user = c.id_user
LEFT JOIN transacao t ON c.id_carteira = t.id_carteira
GROUP BY u.id_user, u.nome
ORDER BY num_transacoes DESC
LIMIT 10;

-- 6.4 Categorias mais utilizadas
SELECT 
    cat.nome,
    cat.tipo,
    COUNT(t.id_transacao) AS vezes_utilizada,
    SUM(t.valor) AS valor_total
FROM categoria cat
LEFT JOIN transacao t ON cat.id_categoria = t.id_categoria
GROUP BY cat.id_categoria, cat.nome, cat.tipo
ORDER BY vezes_utilizada DESC;

-- =============================================
-- 7. VIEWS ÚTEIS
-- =============================================

-- View: Resumo do utilizador
CREATE OR REPLACE VIEW vw_resumo_utilizador AS
SELECT 
    u.id_user,
    u.nome,
    u.email,
    c.saldo,
    (SELECT COUNT(*) FROM transacao t WHERE t.id_carteira = c.id_carteira) AS total_transacoes,
    (SELECT COUNT(*) FROM meta m WHERE m.id_user = u.id_user) AS total_metas,
    (SELECT COUNT(*) FROM orcamento o WHERE o.id_user = u.id_user) AS total_orcamentos
FROM users u
INNER JOIN carteira c ON u.id_user = c.id_user;

-- Consultar a view
SELECT * FROM vw_resumo_utilizador;

-- =============================================
-- Fim das Consultas
-- =============================================

