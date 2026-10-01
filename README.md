# EMPRESA X - Gerenciamento de Compra e Venda

Trabalho 3 de PPOO (UNIP) - sistema em Java Swing com **MVC + DAO + JDBC** (SQLite, como no projeto modelo do professor).

## Como executar
- **VS Code:** abra esta pasta, instale o *Extension Pack for Java* e rode `principal/Main.java` (Run).
- **Terminal:** `./compilar.sh` e depois `./executar.sh` (Windows: `compilar.bat` e `executar.bat`). Requer JDK 8+.

**Login inicial:** `admin` / `admin`. O banco `erp.db` é criado sozinho na primeira execução (tabelas + dados iniciais).

## Estrutura
| Pacote | Conteúdo |
|---|---|
| `conexao` | `Conexao` (JDBC, criação das 11 tabelas do DER) |
| `model` | Classes do DER: Pessoa, Cliente, Fornecedor, Usuario, Produto, FormaPagto, Venda, VendaProduto, VendaPagto, Compra, CompraProduto |
| `dao` | Acesso ao banco (um DAO por entidade) |
| `controller` | Regras de negócio e validações |
| `view` | `LoginView`, `Menu` e as telas (Cliente, Fornecedor, Produto, Forma de Pagamento, Usuário, Venda, Compra) |
| `view.ui` | Componentes visuais próprios (tema, botões, campos, tabelas) |
| `principal` | `Main` |

## Regras principais
- Venda **baixa** o estoque; Compra **acrescenta**. Editar ou excluir reverte o efeito. Estoque negativo é bloqueado.
- Venda exige cliente, data, ao menos 1 produto e 1 pagamento, com soma dos pagamentos = total.
- Senhas guardadas com hash SHA-256.

## Trocar para PostgreSQL
Em `conexao/Conexao.java` há as linhas de `DRIVER`/`BANCO` do PostgreSQL comentadas. Adicione o `.jar` do driver em `driverJDBC/`.
