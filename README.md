# E-commerce API

Projeto baseado no roadmap de projetos do [roadmap.sh](https://roadmap.sh/projects/ecommerce-api), com o objetivo de praticar desenvolvimento de APIs RESTful com Java, autenticação de usuários, processamento de pedidos e integração com serviços externos.

## Descrição

O **E-commerce API** é uma aplicação backend que simula uma plataforma de comércio eletrônico. A API permite o gerenciamento de usuários, produtos, carrinho de compras, pedidos e pagamentos.

Os usuários podem:

- Criar uma conta e realizar autenticação
- Consultar produtos disponíveis
- Adicionar e remover produtos do carrinho
- Finalizar compras
- Realizar pagamentos
- Cancelar um pedido
- Consultar seus pedidos

Administradores podem:

- Cadastrar, atualizar e remover produtos
- Gerenciar pedidos

A aplicação integra o **Stripe** para processamento de pagamentos online e utiliza **webhooks** para atualizar automaticamente o status dos pedidos após a confirmação do pagamento.

## Tecnologias utilizadas

- **Java** – Linguagem principal
- **Spring Boot** – Framework para construção da API
- **Spring Security** – Autenticação e autorização
- **JWT (JSON Web Token)** – Autenticação baseada em tokens
- **Spring Data JPA / Hibernate** – Mapeamento objeto-relacional
- **PostgreSQL** – Banco de dados
- **Flyway** – Versionamento do banco de dados
- **Stripe API** – Processamento de pagamentos
- **Stripe Webhooks** – Atualização automática do status dos pagamentos
- **Maven** – Gerenciador de dependências

## Como rodar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/JessicaLorenzon/e-commerce-api.git
```

### 3. Configure o banco de dados

```bash
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
```

### 4. Obtenha a secret key stripe na sua conta do Stripe

```bash
stripe.secret-key=
```

### 5. Gere o stripe webhook secret do Stripe

```bash
1. stripe listen --forward-to localhost:8080/payments/webhook
2. copy secret code
3. stripe.webhook-secret=
```

### 6. Inicie a aplicação

## Endpoints disponíveis

### Auth

- POST /auth/register - registro de usuário
- POST /auth/login - retorna token JWT

### Product

- GET /products - busca todos os produtos, com paginação 
- GET /products/{id} - busca produto por id
- POST /products - insere novo produto (ADMIN)
- PUT /products/{id} - atualiza produto (ADMIN)
- PATCH /products/{id} - desabilita produto (ADMIN)

### Cart

- GET /carts - busca carrinho do usuário
- POST /carts - adiciona item ao carrinho
- PUT /carts - atualiza a quantidade do item no carrinho
- DELETE /carts/{id} - deleta item do carrinho

### Order

- GET /orders - busca todos os pedidos do usuário ou todos os pedidos para Admin
- GET /orders/{id} - busca pedido do usuário por id
- PATCH /orders/{id} - cancela pedido

### Payment

- POST /payments/checkout - finaliza o carrinho do usuario e retorna url para pagamento com o Stripe
- POST /payments/webhook - webhook para pegar status do pagamento
