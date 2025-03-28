```mermaid
classDiagram
    class App {
        +main(String[] args)
    }

    class ClinicaView {
        -FacadeSingletonController facadeController
        +ClinicaView()
        +initComponents()
        +adicionarCliente()
        +adicionarVeterinario()
        +removerCliente()
        +removerVeterinario()
        +listarClientes()
        +listarVeterinarios()
    }

    class ConsultaView {
        -FacadeSingletonController facadeController
        +ConsultaView()
        +iniciarConsulta()
    }

    class FacadeSingletonController {
        -ClienteController clienteController
        -VeterinarioController veterinarioController
        -ConsultaController consultaController
        -UsuarioRepository repository

        +FacadeSingletonController()
        +adicionarCliente(String nome, String enderecoLinha1, String enderecoLinha2, String email, String telefone) throws InvalidUserException, PersistenceException
        +removerCliente(int id) throws UsuarioNotFoundException, PersistenceException
        +adicionarVeterinario(String nome, String especialidade, String cmv, String email, String telefone) throws InvalidUserException, PersistenceException
        +removerVeterinario(int id) throws UsuarioNotFoundException, PersistenceException
        +listarClientes() : List~Cliente~
        +listarVeterinarios() : List~Veterinario~
        +agendarConsulta(String clienteNome, String veterinarioNome, String data) throws PersistenceException
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

    class ConsultaController {
        -ConsultaModel consultaModel
        -UsuarioRepository repository

        +agendarConsulta(String clienteNome, String veterinarioNome, String data) throws PersistenceException
        +listarConsultas() : List~Consulta~
    }

    class Consulta {
        -String clienteNome
        -String veterinarioNome
        -String data
        +ConsultaModel(String clienteNome, String veterinarioNome, String data)
        +String getClienteNome()
        +String getVeterinarioNome()
        +String getData()
        +String toString()
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
        -List~Consulta~ consultas      // opcional, caso queira manter cache em memória
        -ADM adm
        +UsuarioRepository()
        +static UsuarioRepository getInstance()

        +addCliente(Cliente cliente) throws PersistenceException
        +boolean removeCliente(int id) throws PersistenceException
        +List~Cliente~ getClientes() 
        +addVeterinario(Veterinario vet) throws PersistenceException
        +boolean removeVeterinario(int id) throws PersistenceException
        +addConsulta(Consulta consulta) throws PersistenceException
        +List~Consulta~ getConsultas()
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
    App --> ConsultaView : utiliza
    ClinicaView --> FacadeSingletonController : interage
    ConsultaView --> FacadeSingletonController : interage
    FacadeSingletonController --> ClienteController : interage
    FacadeSingletonController --> VeterinarioController : interage
    FacadeSingletonController --> ConsultaController : interage
    ClienteController --> Cliente : gerencia
    ClienteController --> UsuarioRepository : utiliza
    ClienteController --> ClienteJaExisteException : lida com
    ClienteController --> ClienteNaoEncontradoException : lida com
    VeterinarioController --> Veterinario : gerencia
    VeterinarioController --> UsuarioRepository : utiliza
    VeterinarioController --> VeterinarioJaExisteException : lida com
    VeterinarioController --> VeterinarioNaoEncontradoException : lida com
    ConsultaController --> ConsultaModel : gerencia
    ConsultaController --> UsuarioRepository : utiliza
    Usuario <|-- Cliente : estende
    Usuario <|-- Veterinario : estende
    Usuario <|-- ADM : estende
    UsuarioRepository --> Cliente : gerencia
    UsuarioRepository --> Veterinario : gerencia
    UsuarioRepository --> ADM : gerencia
    UsuarioRepository --> Consulta : gerencia

    UsuarioRepository --> UsuarioDAO : utiliza
    UsuarioNotFoundException <|-- Exception
    InvalidUserException <|-- Exception
    PersistenceException <|-- Exception
