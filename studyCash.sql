-- studyCash_fixed.sql
-- Corrigido para MySQL 8 e compatível com Spring Boot
-- Inclui ordem correta de criação de tabelas e permissões adequadas
SET FOREIGN_KEY_CHECKS = 0;
DROP DATABASE IF EXISTS studycash;
CREATE DATABASE studycash CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE studycash;

CREATE TABLE `users` (
  `id_user` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `notificacoes` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `carteira` (
  `id_carteira` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) NOT NULL,
  `saldo` double NOT NULL,
  PRIMARY KEY (`id_carteira`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `carteira_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `categoria` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `tipo` varchar(10) NOT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `meta` (
  `id_meta` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `valor_objetivo` double NOT NULL,
  `valor_atual` double NOT NULL,
  `data_inicio` date DEFAULT NULL,
  `data_fim` date DEFAULT NULL,
  PRIMARY KEY (`id_meta`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `meta_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `orcamento` (
  `id_orcamento` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) NOT NULL,
  `mes` varchar(20) NOT NULL,
  `limite` double NOT NULL,
  `gasto_atual` double NOT NULL,
  PRIMARY KEY (`id_orcamento`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `orcamento_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `transacao` (
  `id_transacao` int(11) NOT NULL AUTO_INCREMENT,
  `id_carteira` int(11) NOT NULL,
  `id_categoria` int(11) NOT NULL,
  `valor` double NOT NULL,
  `data_transacao` date NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `tipo` varchar(10) NOT NULL,
  PRIMARY KEY (`id_transacao`),
  KEY `id_carteira` (`id_carteira`),
  KEY `id_categoria` (`id_categoria`),
  CONSTRAINT `transacao_ibfk_1` FOREIGN KEY (`id_carteira`) REFERENCES `carteira` (`id_carteira`),
  CONSTRAINT `transacao_ibfk_2` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET FOREIGN_KEY_CHECKS = 1;
