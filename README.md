# Pet API 🐾

Uma API RESTful moderna para gerenciamento de adoções de pets e formulários, desenvolvida com Spring Boot 3 e Java 21.

## 🚀 Tecnologias

Este projeto utiliza as seguintes tecnologias:
- **Java 21**
- **Spring Boot 3.5.x** (Web, Data JPA, Security, Test)
- **H2 Database** (para desenvolvimento local/testes)
- **PostgreSQL** (para produção, via Docker Compose)
- **MapStruct** (para mapeamento de objetos)
- **Lombok** (para reduzir código boilerplate)
- **Swagger / OpenAPI** (para documentação da API)

## 📦 Funcionalidades

- **Gerenciamento de Pets:** Criar, ler, atualizar e excluir registros de pets.
- **Busca Avançada:** Busca de pets por tipo, raça, idade, peso, cidade, etc., com paginação.
- **Formulários de Adoção:** Recuperar perguntas padronizadas para o processo de adoção.

## 🛠️ Como Executar Localmente

### Pré-requisitos
- JDK 21
- Maven
- Docker e Docker Compose (opcional, para uso do PostgreSQL)

### Executando com H2 (Banco de dados em memória)
Por padrão, a aplicação roda com o banco H2 em memória.
```bash
./mvnw spring-boot:run
```

### Executando com PostgreSQL (via Docker Compose)
Para usar o PostgreSQL, inicie o container utilizando o Docker Compose:
```bash
docker-compose up -d
```
*Certifique-se de atualizar o `application.yaml` para apontar para o datasource do PostgreSQL se desejar utilizá-lo no lugar do H2.*

## 📚 Documentação da API

Quando a aplicação estiver rodando, você poderá acessar o Swagger UI para visualizar e testar os endpoints:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### Principais Endpoints

- `GET /v1/pets` - Lista todos os pets (suporta paginação e filtros)
- `GET /v1/pets/{id}` - Busca um pet por ID
- `POST /v1/pets` - Cadastra um novo pet
- `PUT /v1/pets/{id}` - Atualiza os dados de um pet
- `DELETE /v1/pets/{id}` - Exclui um pet
- `GET /v1/form-questions` - Lista as perguntas do formulário de adoção

## 🧪 Testes

O projeto inclui testes unitários abrangentes usando JUnit 5 e Mockito.
Para rodar os testes:
```bash
./mvnw test
```

## 🏗️ Arquitetura e Estrutura

- `domain/` - Entidades JPA e Enums (`Pet`, `Address`, `FormQuestion`).
- `dto/` - Objetos de Transferência de Dados (Request e Response).
- `controller/` - Controladores REST.
- `service/` - Lógica de negócios e integração com Google Workspace.
- `repository/` - Repositórios do Spring Data JPA e Specifications.
- `mapper/` - Interfaces do MapStruct para mapeamento entre Entidades e DTOs.
- `exception/` - Tratamento global de erros (`@ControllerAdvice`).

## 📄 Licença

Este projeto é open-source e está disponível sob a licença MIT.
