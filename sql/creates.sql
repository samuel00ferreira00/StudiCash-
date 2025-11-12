
-- --------------------------------------------------------

--
-- Estrutura para tabela `contas`
--

CREATE TABLE `contas` (
  `id` int(11) NOT NULL,
  `nome_conta` varchar(100) NOT NULL,
  `saldo` decimal(10,2) DEFAULT 0.00,
  `data_criacao` date ,
  `id_utilizador` int(11) NOT NULL,
  `id_tipo_conta` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `metas`
--

CREATE TABLE `metas` (
  `id` int(11) NOT NULL,
  `titulo` varchar(100) NOT NULL,
  `descricao` text DEFAULT NULL,
  `categoria` varchar(50) NOT NULL,
  `valor_objetivo` decimal(10,2) NOT NULL,
  `valor_atual` decimal(10,2) DEFAULT 0.00,
  `progresso` decimal(5,2) DEFAULT 0.00,
  `data_criacao` date ,
  `data_limite` date DEFAULT NULL,
  `id_utilizador` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `orcamentos`
--

CREATE TABLE `orcamentos` (
  `id` int(11) NOT NULL,
  `nome_orcamento` varchar(100) NOT NULL,
  `valor_total` decimal(10,2) NOT NULL,
  `progresso` decimal(5,2) DEFAULT 0.00,
  `data_criacao` date,
  `id_utilizador` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `orcamento_transacao`
--

CREATE TABLE `orcamento_transacao` (
  `id` int(11) NOT NULL,
  `id_orcamento` int(11) NOT NULL,
  `id_transacao` int(11) NOT NULL,
  `data_associacao` date 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `tipo_conta`
--

CREATE TABLE `tipo_conta` (
  `id` int(11) NOT NULL,
  `nome_tipo` varchar(50) NOT NULL,
  `data_criacao` date 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `tipo_transacao`
--

CREATE TABLE `tipo_transacao` (
  `id` int(11) NOT NULL,
  `nome_tipo` varchar(50) NOT NULL,
  `data_criacao` date 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `transacoes`
--

CREATE TABLE `transacoes` (
  `id` int(11) NOT NULL,
  `descricao` varchar(150) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data_transacao` date,
  `id_conta` int(11) NOT NULL,
  `id_tipo_transacao` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `transferencias`
--



CREATE TABLE `transferencias` (
  `id` int(11) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data_transferencia` date,
  `descricao` varchar(150) DEFAULT NULL,
  `id_conta_origem` int(11) NOT NULL,
  `id_conta_destino` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `utilizadores`
--

CREATE TABLE `utilizadores` (
  `id` int(11) NOT NULL,
  `nome_do_utilizador` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `senha` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;