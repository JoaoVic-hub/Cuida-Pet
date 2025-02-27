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
        +adicionarCliente(String nome, String enderecoLinha1, String enderecoLinha2, String email, String telefone) throws InvalidUserException, PersistenceException
        +removerCliente(int id) throws UsuarioNotFoundException, PersistenceException
    }

    class VeterinarioController {
        -UsuarioRepository repository
        +adicionarVeterinario(String nome, String especialidade, String cmv, String email, String telefone) throws InvalidUserException, PersistenceException
        +removerVeterinario(int id) throws UsuarioNotFoundException, PersistenceException
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
        +String getEnderecoLinha1()
        +String getEnderecoLinha2()
        +String toString()
    }

    class Veterinario {
        +String especialidade
        +String cmv
        +Veterinario(String nome, String especialidade, String cmv, String email, String telefone)
        +String getEspecialidade()
        +String getCmv()
        +String toString()
    }

    class ADM {
        +ADM(String nome, String email, String telefone)
        +String toString()
    }

    class UsuarioRepository {
        -static UsuarioRepository instance
        -UsuarioDAO usuarioDAO
        -List~Cliente~ clientes        // opcional, caso queira manter cache em memória
        -List~Veterinario~ veterinarios
        -ADM adm
        +UsuarioRepository()
        +static UsuarioRepository getInstance()
        +addCliente(Cliente cliente) throws PersistenceException
        +boolean removeCliente(int id) throws PersistenceException
        +List~Cliente~ getClientes() 
        +addVeterinario(Veterinario vet) throws PersistenceException
        +boolean removeVeterinario(int id) throws PersistenceException
        +List~Veterinario~ getVeterinarios()
        +ADM getAdm()
        +List~Object~ getAllUsuarios() throws PersistenceException
    }

    class UsuarioDAO {
        +UsuarioDAO()
        +void saveUsuario(Usuario usuario) throws PersistenceException
        +void removeUsuario(int id) throws PersistenceException
        +Usuario findUsuarioById(int id) throws PersistenceException
        +List~Usuario~ findAllUsuarios() throws PersistenceException
    }

    class InvalidUserException {
        +InvalidUserException(String message)
    }

    class UsuarioNotFoundException {
        +UsuarioNotFoundException(String message)
    }

    class PersistenceException {
        +PersistenceException(String message)
    }

    %% Relações
    App --> ClinicaView : utiliza
    ClinicaView --> ClienteController : interage
    ClinicaView --> VeterinarioController : interage
    ClienteController --> Cliente : gerencia
    ClienteController --> UsuarioRepository : utiliza
    VeterinarioController --> Veterinario : gerencia
    VeterinarioController --> UsuarioRepository : utiliza
    Usuario <|-- Cliente : estende
    Usuario <|-- Veterinario : estende
    Usuario <|-- ADM : estende
    UsuarioRepository --> Cliente : gerencia
    UsuarioRepository --> Veterinario : gerencia
    UsuarioRepository --> ADM : gerencia
    UsuarioRepository --> UsuarioDAO : utiliza
    UsuarioNotFoundException <|-- Exception
    InvalidUserException <|-- Exception
    PersistenceException <|-- Exception
