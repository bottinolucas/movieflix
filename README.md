# Movieflix

API REST para catalogar filmes, suas categorias e os streamings onde estão disponíveis, com autenticação via JWT.

## Stack

- Java 17 + Spring Boot 3.5
- Spring Data JPA + PostgreSQL
- Flyway (migrações de banco)
- Spring Security + JWT (auth0/java-jwt)
- springdoc-openapi (Swagger UI)
- Docker / Docker Compose
- JUnit 5 + Mockito (testes unitários) e Testcontainers (testes de integração)
- GitHub Actions (CI/CD) publicando imagem no GitHub Container Registry

## Arquitetura

```
Controller  ->  Service  ->  Repository  ->  PostgreSQL
    |
    v
 Mapper (Entity <-> Request/Response)
```

Entidades: `Movie`, `Category`, `Streaming` (N:N entre Movie e as outras duas) e `User` (autenticação).

Segurança: `SecurityFilter` valida o JWT em cada requisição (`Authorization: Bearer <token>`) e popula o contexto de segurança; `SecurityConfig` define quais rotas são públicas (`/auth/register`, `/auth/login`, Swagger) e exige autenticação para o resto.

## Rodando localmente

### Opção 1 — Docker Compose (recomendado)

Sobe o Postgres e a aplicação juntos:

```bash
cp .env.example .env
docker compose up -d --build
```

A API fica disponível em `http://localhost:8080` e o Swagger em `http://localhost:8080/swagger-ui/index.html`.

### Opção 2 — Banco via Docker, aplicação local (IDE/Maven)

```bash
cp .env.example .env
docker compose up -d postgres
./mvnw spring-boot:run
```

## Variáveis de ambiente

Definidas em `.env` (veja `.env.example`):

| Variável | Descrição | Default |
|---|---|---|
| `DB_HOST` | Host do Postgres | `localhost` |
| `DB_PORT` | Porta do Postgres | `5432` |
| `DB_NAME` | Nome do banco | `movieflix` |
| `DB_USER` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `postgres` |
| `JWT_SECRET` | Secret usado para assinar o JWT | — |
| `JWT_SALT` | Salt adicional combinado ao secret | — |

## Autenticação

1. `POST /movieflix/auth/register` — cria um usuário.
2. `POST /movieflix/auth/login` — retorna `{ "token": "..." }`.
3. Envie o token nas demais requisições: `Authorization: Bearer <token>`.

No Swagger, use o botão **Authorize** (cadeado) para informar `Bearer <token>` e testar as rotas protegidas direto pela UI.

## Endpoints

### Auth (`/movieflix/auth`)
| Método | Rota | Descrição | Autenticação |
|---|---|---|---|
| POST | `/register` | Cria um usuário | Pública |
| POST | `/login` | Autentica e retorna o JWT | Pública |

### Categorias (`/movieflix/category`)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/` | Lista todas as categorias |
| GET | `/{id}` | Busca categoria por id |
| POST | `/` | Cria categoria |
| DELETE | `/{id}` | Remove categoria |

### Streamings (`/movieflix/streaming`)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/` | Lista todos os streamings |
| GET | `/{id}` | Busca streaming por id |
| POST | `/` | Cria streaming |
| DELETE | `/{id}` | Remove streaming |

### Filmes (`/movieflix/movie`)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/` | Lista todos os filmes |
| GET | `/{id}` | Busca filme por id |
| GET | `/category/{categoryId}` | Lista filmes de uma categoria |
| POST | `/` | Cria filme (com categorias e streamings) |
| PUT | `/{id}` | Atualiza filme |
| DELETE | `/{id}` | Remove filme |

Todas as rotas acima (exceto `/auth/register` e `/auth/login`) exigem o header `Authorization: Bearer <token>`.

## Testes

```bash
./mvnw test      # apenas testes unitários
./mvnw verify     # unitários + integração (sobe um Postgres via Testcontainers, precisa do Docker rodando)
```

## CI/CD

O workflow em `.github/workflows/ci-cd.yml` roda em todo push/PR para `main`:

1. `build-and-test`: builda e executa `./mvnw clean verify` com um Postgres de serviço.
2. `docker-build-push`: builda a imagem Docker e publica em `ghcr.io/<owner>/movieflix` (só em push na `main`).
