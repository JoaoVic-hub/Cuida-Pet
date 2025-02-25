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
        +adicionarCliente(String nome, String enderecoLinha1, String enderecoLinha2, String email, String telefone)
        +removerCliente(int id)
    }

    class VeterinarioController {
        -UsuarioRepository repository
        +adicionarVeterinario(String nome, String especialidade, String cmv, String email, String telefone)
        +removerVeterinario(int id)
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
        +addCliente(Cliente cliente)
        +boolean removeCliente(int id)
        +List~Cliente~ getClientes()
        +addVeterinario(Veterinario vet)
        +boolean removeVeterinario(int id)
        +List~Veterinario~ getVeterinarios()
        +ADM getAdm()
        +List~Object~ getAllUsuarios()
    }

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
