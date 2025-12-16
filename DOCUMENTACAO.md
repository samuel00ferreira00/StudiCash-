# 📚 Documentação do Projeto StudyCash

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Arquitetura do Sistema](#arquitetura-do-sistema)
3. [Backend](#backend)
4. [Frontend](#frontend)
5. [Base de Dados](#base-de-dados)
6. [API Endpoints](#api-endpoints)
7. [Configuração e Execução](#configuração-e-execução)

---

## 🎯 Visão Geral

O **StudyCash** é uma aplicação de gestão financeira pessoal desenvolvida para estudantes. Permite aos utilizadores:
- Registar receitas e despesas
- Visualizar o saldo da carteira
- Definir metas financeiras
- Criar orçamentos
- Categorizar transações
- Guardar localização das transações (manual e GPS)

### Tecnologias Utilizadas

| Componente | Tecnologia |
|------------|------------|
| Backend | Java 17 + Spring Boot 3.3.4 |
| Frontend | Kotlin + Jetpack Compose |
| Base de Dados | MySQL 8 |
| API | REST |
| Build Backend | Maven |
| Build Frontend | Gradle |

---

## 🏗️ Arquitetura do Sistema

```
┌─────────────────┐      HTTP/REST      ┌─────────────────┐      JDBC      ┌─────────────────┐
│                 │  ◄──────────────►   │                 │  ◄──────────►  │                 │
│  Frontend       │                     │  Backend        │                │  MySQL          │
│  (Android App)  │                     │  (Spring Boot)  │                │  Database       │
│                 │                     │                 │                │                 │
└─────────────────┘                     └─────────────────┘                └─────────────────┘
     Kotlin                                  Java                              studycash
     Jetpack Compose                         REST API                          
     Retrofit                                JPA/Hibernate                     
```

---

## ⚙️ Backend

### Estrutura de Pastas

```
backend/
├── src/main/java/pt/iade/ei/studycash/
│   ├── controller/          # Controladores REST
│   │   ├── CarteiraController.java
│   │   ├── CategoriaController.java
│   │   ├── MetaController.java
│   │   ├── OrcamentoController.java
│   │   ├── TransacaoController.java
│   │   └── UserController.java
│   │
│   ├── model/               # Entidades JPA
│   │   ├── Carteira.java
│   │   ├── Categoria.java
│   │   ├── Meta.java
│   │   ├── Orcamento.java
│   │   ├── Transacao.java
│   │   └── User.java
│   │
│   ├── repository/          # Repositórios Spring Data JPA
│   │   ├── CarteiraRepository.java
│   │   ├── CategoriaRepository.java
│   │   ├── MetaRepository.java
│   │   ├── OrcamentoRepository.java
│   │   ├── TransacaoRepository.java
│   │   └── UserRepository.java
│   │
│   └── StudyCashApplication.java  # Classe principal
│
├── src/main/resources/
│   └── application.properties     # Configurações
│
└── pom.xml                        # Dependências Maven
```

### Modelos de Dados

#### User (Utilizador)
```java
- idUser: Long (PK)
- nome: String
- email: String
- password: String
- dataNascimento: LocalDate
```

#### Carteira (Wallet)
```java
- idCarteira: Long (PK)
- saldo: double
- user: User (FK)
```

#### Transacao (Transaction)
```java
- idTransacao: Long (PK)
- descricao: String
- valor: double
- tipo: String ("Receita" | "Despesa")
- dataTransacao: LocalDate
- carteira: Carteira (FK)
- categoria: Categoria (FK, nullable)
- latitude: Double (nullable)
- longitude: Double (nullable)
- localizacao: String (nullable)
```

#### Meta (Goal)
```java
- idMeta: Long (PK)
- descricao: String
- valorAlvo: double
- valorAtual: double
- dataInicio: LocalDate
- dataFim: LocalDate
- user: User (FK)
```

#### Categoria (Category)
```java
- idCategoria: Long (PK)
- nome: String
- tipo: String
```

#### Orcamento (Budget)
```java
- idOrcamento: Long (PK)
- valorDefinido: double
- valorGasto: double
- dataInicio: LocalDate
- dataFim: LocalDate
- user: User (FK)
- categoria: Categoria (FK)
```

### Controladores REST

Os controladores seguem o padrão REST e utilizam as anotações do Spring:

```java
@RestController
@RequestMapping("/api/endpoint")
@CrossOrigin(origins = "*")
public class ExemploController {
    
    @GetMapping           // GET - Listar todos
    @GetMapping("/{id}")  // GET - Buscar por ID
    @PostMapping          // POST - Criar novo
    @PutMapping("/{id}")  // PUT - Atualizar
    @DeleteMapping("/{id}") // DELETE - Remover
}
```

### Configuração (application.properties)

```properties
# Base de Dados MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/studycash
spring.datasource.username=root
spring.datasource.password=******

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Servidor
server.port=8080
```

---

## 📱 Frontend

### Estrutura de Pastas

```
frontend/app/src/main/java/pt/iade/ei/studycash/
├── data/                    # Gestão de dados local
│   └── SessionManager.kt    # Gestão de sessão do utilizador
│
├── model/                   # Modelos de dados (Data Classes)
│   ├── Carteira.kt
│   ├── Categoria.kt
│   ├── Meta.kt
│   ├── Orcamento.kt
│   ├── Transacao.kt
│   └── User.kt
│
├── network/                 # Serviços de rede (Retrofit)
│   ├── ApiClient.kt         # Cliente Retrofit
│   ├── CarteiraService.kt
│   ├── CategoriaService.kt
│   ├── MetaService.kt
│   ├── OrcamentoService.kt
│   ├── TransacaoService.kt
│   └── UserService.kt
│
├── ui/theme/                # Tema da aplicação
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
└── [Activities]             # Ecrãs da aplicação
    ├── MainActivity.kt           # Ecrã inicial
    ├── LoginActivity.kt          # Login
    ├── RegisterActivity.kt       # Registo
    ├── HomeActivity.kt           # Home (Dashboard)
    ├── TransactionsActivity.kt   # Lista de transações
    ├── NewExpenseActivity.kt     # Nova despesa
    ├── NewRevenueActivity.kt     # Nova receita
    ├── DetailTransactionsActivity.kt  # Detalhes da transação
    ├── GoalsActivity.kt          # Lista de metas
    ├── DetailGoalsActivity.kt    # Detalhes da meta
    ├── BudgetActivity.kt         # Orçamentos
    └── ProfileActivity.kt        # Perfil do utilizador
```

### Componentes Principais

#### ApiClient (Retrofit)
Configura a comunicação com o backend:

```kotlin
object ApiClient {
    // Para EMULADOR: 10.0.2.2
    // Para DISPOSITIVO FÍSICO: IP do computador (ex: 192.168.1.100)
    private const val BASE_URL = "http://10.208.204.4:8080/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)
    val transacaoService: TransacaoService = retrofit.create(TransacaoService::class.java)
    // ... outros serviços
}
```

#### SessionManager
Gere a sessão do utilizador usando SharedPreferences:

```kotlin
object SessionManager {
    fun saveUserId(context: Context, userId: Long)
    fun getUserId(context: Context): Long
    fun clearSession(context: Context)
}
```

#### Services (Interfaces Retrofit)
Definem os endpoints da API:

```kotlin
interface TransacaoService {
    @GET("api/transacoes/user/{userId}")
    suspend fun byUser(@Path("userId") userId: Long): Response<List<Transacao>>

    @POST("api/transacoes/user/{userId}")
    suspend fun createForUser(@Path("userId") userId: Long, @Body t: Transacao): Response<Transacao>

    @DELETE("api/transacoes/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Void>
}
```

### Jetpack Compose

A UI é construída com Jetpack Compose, utilizando:

- **Scaffold**: Estrutura base com BottomBar
- **ElevatedCard**: Cards com elevação
- **Column/Row/Box**: Layout
- **remember/mutableStateOf**: Gestão de estado
- **LaunchedEffect**: Efeitos colaterais
- **rememberCoroutineScope**: Coroutines para chamadas assíncronas

#### Exemplo de Composable:

```kotlin
@Composable
fun ExemploScreen() {
    val dados = remember { mutableStateOf<List<Dados>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        scope.launch {
            val response = ApiClient.service.getData()
            if (response.isSuccessful) {
                dados.value = response.body() ?: emptyList()
            }
        }
    }

    Scaffold(bottomBar = { AppBottomBar() }) { padding ->
        Column(Modifier.padding(padding)) {
            dados.value.forEach { item ->
                Text(item.nome)
            }
        }
    }
}
```

### Permissões (AndroidManifest.xml)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<queries>
    <package android:name="com.google.android.apps.maps" />
    <intent>
        <action android:name="android.intent.action.VIEW" />
        <data android:scheme="https" />
    </intent>
</queries>
```

### Funcionalidade de Localização

A app captura automaticamente a localização GPS ao criar transações:

```kotlin
val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

fusedLocationClient.getCurrentLocation(
    Priority.PRIORITY_HIGH_ACCURACY,
    CancellationTokenSource().token
).addOnSuccessListener { location ->
    location?.let {
        currentLatitude.value = it.latitude
        currentLongitude.value = it.longitude
    }
}
```

---

## 🗄️ Base de Dados

### Diagrama ER Simplificado

```
┌──────────┐       ┌───────────┐       ┌────────────┐
│   User   │──1:1──│  Carteira │──1:N──│  Transacao │
└──────────┘       └───────────┘       └────────────┘
     │                                       │
     │                                       │
     │1:N                               N:1  │
     ▼                                       ▼
┌──────────┐                          ┌────────────┐
│   Meta   │                          │  Categoria │
└──────────┘                          └────────────┘
     │                                       ▲
     │                                       │
     │                                  N:1  │
     ▼                                       │
┌────────────┐                               │
│ Orcamento  │───────────────────────────────┘
└────────────┘
```

### Tabelas

| Tabela | Descrição |
|--------|-----------|
| user | Utilizadores do sistema |
| carteira | Carteira/conta do utilizador |
| transacao | Receitas e despesas |
| meta | Metas financeiras |
| categoria | Categorias de transações |
| orcamento | Orçamentos por categoria |

---

## 🔌 API Endpoints

### Utilizadores
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/users | Listar todos |
| GET | /api/users/{id} | Buscar por ID |
| POST | /api/users | Criar utilizador |
| POST | /api/users/login | Login |
| PUT | /api/users/{id} | Atualizar |
| DELETE | /api/users/{id} | Remover |

### Transações
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/transacoes | Listar todas |
| GET | /api/transacoes/user/{userId} | Por utilizador |
| POST | /api/transacoes/user/{userId} | Criar para utilizador |
| PUT | /api/transacoes/{id} | Atualizar |
| DELETE | /api/transacoes/{id} | Remover |

### Metas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/metas | Listar todas |
| GET | /api/metas/user/{userId} | Por utilizador |
| POST | /api/metas/user/{userId} | Criar para utilizador |
| PUT | /api/metas/{id} | Atualizar |
| DELETE | /api/metas/{id} | Remover |

### Carteiras
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/carteiras | Listar todas |
| GET | /api/carteiras/user/{userId} | Por utilizador |
| POST | /api/carteiras | Criar |
| PUT | /api/carteiras/{id} | Atualizar |

### Categorias
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/categorias | Listar todas |
| POST | /api/categorias | Criar |

### Orçamentos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/orcamentos | Listar todos |
| GET | /api/orcamentos/user/{userId} | Por utilizador |
| POST | /api/orcamentos/user/{userId} | Criar para utilizador |
| PUT | /api/orcamentos/{id} | Atualizar |
| DELETE | /api/orcamentos/{id} | Remover |

---

## 🚀 Configuração e Execução

### Pré-requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+
- Android Studio (Arctic Fox ou superior)
- SDK Android 24+ (target 34)

### Backend

1. **Criar base de dados:**
```sql
CREATE DATABASE studycash CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **Configurar credenciais** em `application.properties`

3. **Executar:**
```bash
cd backend
mvn spring-boot:run
```

O servidor inicia em `http://localhost:8080`

### Frontend

1. **Configurar IP do backend** em `ApiClient.kt`:
   - Emulador: `10.0.2.2`
   - Dispositivo físico: IP do computador (ex: `192.168.1.100`)

2. **Verificar mesma rede Wi-Fi** (para dispositivo físico)

3. **Executar** via Android Studio (Run > Run 'app')

### Troubleshooting

| Problema | Solução |
|----------|---------|
| Erro de conexão no telefone | Verificar IP em ApiClient.kt e mesma rede Wi-Fi |
| Porta 8080 em uso | `lsof -ti:8080 \| xargs kill -9` |
| Mapa não abre | Verificar queries no AndroidManifest.xml |
| Localização não funciona | Verificar permissões e GPS ativado |

---

## 📝 Notas Adicionais

### Fluxo de Criação de Transação

1. Utilizador abre NewExpenseActivity ou NewRevenueActivity
2. App tenta obter localização GPS automaticamente
3. Utilizador preenche dados (valor, descrição, categoria)
4. Utilizador pode inserir localização manual (opcional)
5. Ao guardar:
   - Frontend envia POST para `/api/transacoes/user/{userId}`
   - Backend associa à carteira do utilizador
   - Backend atualiza saldo da carteira
   - Backend guarda transação com localização

### Visualização de Localização

- Nos detalhes da transação, se existir latitude/longitude:
  - Mostra coordenadas GPS
  - Botão "Ver no Mapa" abre Google Maps ou browser

---

**Desenvolvido para:** Projeto Académico IADE  
**Versão:** 1.0  
**Data:** Dezembro 2025




