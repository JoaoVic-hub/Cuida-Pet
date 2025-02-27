
```mermaid
classDiagram
    class App {
        +main(String[] args)
    }

    class ClinicaView {
        -ClienteController clienteController
        -VeterinarioController veterinarioController
        -UsuarioRepository repository
        +ClinicaView()
        +initComponents()
        +adicionarCliente()
        +adicionarVeterinario()
        +removerCliente()
        +removerVeterinario()
        +listarClientes()
        +listarVeterinarios()
    }

    class ClienteController {
        -UsuarioRepository repository
        +adicionarCliente(String nome, String enderecoLinha1, String enderecoLinha2, String email, String telefone) throws ClienteJaExisteException
        +removerCliente(int id) throws ClienteNaoEncontradoException
    }

    class VeterinarioController {
        -UsuarioRepository repository
        +adicionarVeterinario(String nome, String especialidade, String cmv, String email, String telefone) throws VeterinarioJaExisteException
        +removerVeterinario(int id) throws VeterinarioNaoEncontradoException
    }

    class Usuario {
        <<abstract>>
        -static int contador
        -int id
        -String nome
        -String email
        -String telefone
        +Usuario(String nome, String email, String telefone)
        +int getId()
        +String getNome()
        +String getEmail()
        +String getTelefone()
        +abstract String toString()
    }

    class Cliente {
        +String enderecoLinha1
        +String enderecoLinha2
        +Cliente(String nome, String enderecoLinha1, String enderecoLinha2, String email, String telefone)
        +getEnderecoLinha1() String
        +getEnderecoLinha2() String
        +toString() String
    }

    class Veterinario {
        +String especialidade
        +String cmv
        +Veterinario(String nome, String especialidade, String cmv, String email, String telefone)
        +getEspecialidade() String
        +getCmv() String
        +toString() String
    }

    class ADM {
        +ADM(String nome, String email, String telefone)
        +toString() String
    }

    class UsuarioRepository {
        -static UsuarioRepository instance
        -List~Cliente~ clientes
        -List~Veterinario~ veterinarios
        -ADM adm
        +UsuarioRepository()
        +static UsuarioRepository getInstance()
        +addCliente(Cliente cliente) throws ClienteJaExisteException
        +boolean removeCliente(int id) throws ClienteNaoEncontradoException
        +List~Cliente~ getClientes()
        +addVeterinario(Veterinario vet) throws VeterinarioJaExisteException
        +boolean removeVeterinario(int id) throws VeterinarioNaoEncontradoException
        +List~Veterinario~ getVeterinarios()
        +ADM getAdm()
        +List~Object~ getAllUsuarios()
    }

    class UsuarioDAO {
        +static void salvarDados(UsuarioRepository repository)
        +static UsuarioRepository carregarDados()
    }

    class ClienteJaExisteException {
        +ClienteJaExisteException(String mensagem)
    }

    class ClienteNaoEncontradoException {
        +ClienteNaoEncontradoException(String mensagem)
    }

    class VeterinarioJaExisteException {
        +VeterinarioJaExisteException(String mensagem)
    }

    class VeterinarioNaoEncontradoException {
        +VeterinarioNaoEncontradoException(String mensagem)
    }

    App --> ClinicaView : utiliza
    ClinicaView --> ClienteController : interage
    ClinicaView --> VeterinarioController : interage
    ClienteController --> Cliente : gerencia
    ClienteController --> UsuarioRepository : utiliza
    ClienteController --> ClienteJaExisteException : lida com
    ClienteController --> ClienteNaoEncontradoException : lida com
    VeterinarioController --> Veterinario : gerencia
    VeterinarioController --> UsuarioRepository : utiliza
    VeterinarioController --> VeterinarioJaExisteException : lida com
    VeterinarioController --> VeterinarioNaoEncontradoException : lida com
    Usuario <|-- Cliente : estende
    Usuario <|-- Veterinario : estende
    Usuario <|-- ADM : estende
    UsuarioRepository --> Cliente : gerencia
    UsuarioRepository --> Veterinario : gerencia
    UsuarioRepository --> ADM : gerencia
    UsuarioRepository --> ClienteJaExisteException : lida com
    UsuarioRepository --> ClienteNaoEncontradoException : lida com
    UsuarioRepository --> VeterinarioJaExisteException : lida com
    UsuarioRepository --> VeterinarioNaoEncontradoException : lida com
    UsuarioRepository --> UsuarioDAO : utiliza
    UsuarioDAO --> UsuarioRepository : gerencia persistência
