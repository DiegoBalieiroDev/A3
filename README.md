# AnhembiMorumBank – Sistema Bancário Completo em Java (Spring Boot)

Este repositório contém um **projeto bancário completo**, desenvolvido seguindo boas práticas de arquitetura, segurança, padrões de projeto, testes unitários e integração entre múltiplas camadas. É um projeto robusto, didático e ideal para estudos de **Java, Spring Boot, Segurança, Arquitetura e testes automatizados**.

---

## 🚀 Objetivo do Projeto

Criar um **sistema bancário completo**, incluindo:

* Cadastro de clientes
* Cadastro e gerenciamento de contas bancárias
* Sistema de transações com PIX
* Motor de detecção de fraudes integrado
* Autenticação e autorização com Spring Security + JWT
* Segurança com PIN, roles, perfis e prevenção a golpes
* Histórico e registro de transações
* Testes unitários com JUnit + Mockito

Este projeto foi utilizado como **atividade A3 na faculdade**, mas foi estruturado de forma profissional, sendo um excelente exemplo de arquitetura limpa.

---

## 🏗️ Arquitetura do Sistema

O sistema segue a divisão em camadas:

```
Controller → Service → Repository → Model → Database
```

Além disso, conta com:

* Camada **Security** com JWT e filtros
* Camada **DTO** para comunicação segura e padronizada
* Camada **Fraude** para avaliação de comportamento suspeito
* Camada **Migrations** (Flyway/Liquibase) com criação do banco

---

## 📦 Tecnologias Utilizadas

### **Back-end (Java / Spring Boot)**

* **Spring Boot 3+**
* **Spring Web**
* **Spring Security (JWT)**
* **Spring Data JPA**
* **Flyway** (migrations)
* **Lombok**
* **Jakarta Validation**

### **Ferramentas e padrões**

* DTOs para entrada/saída
* Autenticação JWT
* Encoders e PasswordHashing (BCrypt)
* Regras de negócio isoladas em Services
* Testes com **JUnit + Mockito**

### **Banco de Dados**

* MySQL / MariaDB
* Migrations completas contendo:

  * clientes
  * contas
  * transacoes
  * usuarios

---

## 🧱 Estrutura das Principais Funcionalidades

### ✔️ **Clientes**

Inclui dados pessoais, endereço completo, chave PIX, tipo de cliente, PIN seguro e vínculo com usuário.

### ✔️ **Contas**

* Cada cliente possui uma conta única
* Número da conta gerado
* Agência fixa
* Saldo
* Data de criação (`criado_em`)

### ✔️ **Transações (PIX)**

Fluxo completo:

1. Validação do cliente origem
2. Validação de PIN (hash Bcrypt)
3. Verificação de valor e saldo
4. Identificação de destino interno/externo
5. Avaliação antifraude
6. Persistência da transação
7. Atualização de saldo

### ✔️ **Mecanismo de Detecção de Fraudes**

Baseado em:

* Horário
* Frequência
* Valor
* Comportamento atípico
* Histórico
* Relação entre origem e destino

Retorna:

* Score
* Motivos
* Ação: **APPROVE**, **DENY**, **REVIEW**

### ✔️ **Autenticação (Spring Security + JWT)**

A pasta **Security** contém:

* `TokenService` → geração/validação de JWT
* `SecurityFilter` → intercepta requisições e valida token
* `SecurityConfig` → define rotas públicas/privadas, cors, csrf
* `UserDetails` (classe Usuário) → integração com autenticação
* `AuthenticationManager`

---

## 🗂️ Estrutura de Pastas

```
A3.AnhembiMorumBank/
 ├── Controller/
 ├── DTO/
 │    ├── Cliente/
 │    ├── Conta/
 │    └── Transacao/
 ├── model/
 ├── Repository/
 ├── Security/
 │    ├── TokenService.java
 │    ├── SecurityFilter.java
 │    └── SecurityConfig.java
 ├── Service/
 │    ├── ClienteService
 │    ├── ContaService
 │    ├── TransacaoService
 │    ├── FraudeService
 │    └── AuthService
 └── migrations/
```

---

## 🧪 Testes Unitários

O projeto possui testes completos para:

* Fluxo de transações internas
* Falhas de PIN
* Falhas de saldo
* Avaliação de fraud score
* Regras de validação de valor
* Regras de cliente inexistente

Testes construídos com:

* JUnit 5
* Mockito

Estratégias utilizadas:

* Mock de repositórios
* Mock de PasswordEncoder
* Mock do serviço de fraude
* Testes de exceções
* Testes de saldo final e persistência

---

## 🛢️ Banco de Dados (Migrations)

As migrations criam as tabelas:

* `clientes`
* `contas`
* `transacoes`
* `usuarios`

Com campos adicionais: `pin`, `fraud_score`, `fraud_reasons`, etc.

Inclui constraints essenciais:

* UNIQUE em CPF, email, chave PIX
* FK de relação Cliente ↔ Conta
* FK Cliente ↔ Usuário
* FK Transação ↔ Cliente origem

---

## ⚙️ Como Rodar o Projeto

### **1. Clonar o repositório**

```bash
git clone https://github.com/SEU_USUARIO/AnhembiMorumBank.git
```

### **2. Criar um banco MySQL**

```sql
CREATE DATABASE banco_a3;
```

### **3. Configurar application.properties**

```properties
spring.datasource.url=jdbc:mysql://localhost/banco_a3
spring.datasource.username=root
spring.datasource.password=senha
```

### **4. Rodar**

```bash
mvn spring-boot:run
```

---

## 🔐 Segurança Implementada

* JWT com expiração
* Pin numérico hashado com BCrypt
* Perfis: `CLIENTE`, `ADMIN`
* Filtros para rotas
* Proteção contra acesso indevido a contas

---

## 📌 Endpoints Principais

### **Autenticação**

```
POST /login
```

### **Clientes**

```
POST /clientes
GET /clientes/{id}
PUT /clientes/{id}
```

### **Contas**

```
GET /contas/{clienteId}
```

### **Transações PIX**

```
POST /transacoes
GET /transacoes/extrato/{idCliente}
```

---

## 📊 Fluxo geral da transação

```
Cliente envia DTO → Validação → PIN → Conta → Saldo → Fraude → Persistência → Resposta
```

---

## 🏆 Destaques do Projeto

* Arquitetura limpa
* Alta segurança
* Fluxo completo e realista de um banco
* Testes automatizados robustos
* Mecanismo inteligente de fraude
* Pronto para apresentação acadêmica ou portfólio

---

## 📄 Licença

Uso livre para fins de estudo e evolução profissional.

---

## ✨ Autor

**Diego Balieiro** – Auditor, estudante de ADS e desenvolvedor em evolução.

---
