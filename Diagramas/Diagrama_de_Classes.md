```mermaid
classDiagram
    class Usuario {
        <<abstract>>
        +String nome
        +String email
        +String senha
        +login()
        +logout()
    }

    class Administrador {
        +gerenciarClientes()
        +gerenciarVeterinarios()
        +agendarConsulta()
        +editarConsulta()
        +cancelarConsulta()
    }

    class Veterinario {
        +gerenciarAnimais()
        +elaborarProntuario()
        +visualizarAgenda()
    }

    class Cliente {
        +visualizarConsultas()
        +receberNotificacoes()
    }

    class Animal {
        +String nome
        +String especie
        +String raca
        +int idade
    }

    class Consulta {
        +String data
        +String horario
        +String diagnostico
        +String tratamento
    }

    class Prontuario {
        +String historico
        +String tratamentos
        +String diagnosticos
    }

    class Notificacao {
        +String mensagem
        +Date dataEnvio
        +enviarEmail()
    }

    class UsuarioDAO {
        +salvar(Usuario usuario)
        +buscarPorEmail(String email)
    }

    class AdministradorDAO {
        +salvar(Administrador administrador)
        +buscarPorId(int id)
    }

    class VeterinarioDAO {
        +salvar(Veterinario veterinario)
        +buscarPorId(int id)
    }

    class ClienteDAO {
        +salvar(Cliente cliente)
        +buscarPorId(int id)
    }

    class AnimalDAO {
        +salvar(Animal animal)
        +buscarPorId(int id)
    }

    class ConsultaDAO {
        +salvar(Consulta consulta)
        +buscarPorId(int id)
    }

    class ProntuarioDAO {
        +salvar(Prontuario prontuario)
        +buscarPorId(int id)
    }

    class UsuarioService {
        +cadastrarUsuario(Usuario usuario, String tipo)
        +validarLogin(String email, String senha)
    }

    class AdministradorService {
        +gerenciarClientes()
        +gerenciarVeterinarios()
    }

    class VeterinarioService {
        +gerenciarAnimais()
        +elaborarProntuario()
    }

    class ClienteService {
        +visualizarConsultas()
        +receberNotificacoes()
    }

    class AnimalService {
        +cadastrarAnimal(Animal animal)
        +buscarAnimalPorId(int id)
    }

    class ConsultaService {
        +agendarConsulta(Consulta consulta)
        +cancelarConsulta(int id)
    }

    class ProntuarioService {
        +salvarProntuario(Prontuario prontuario)
        +buscarProntuarioPorId(int id)
    }

    class NotificacaoService {
        +enviarNotificacao(Notificacao notificacao)
    }

    class UsuarioController {
        +cadastrarUsuario(String nome, String email, String senha, String tipo)
        +validarLogin(String email, String senha)
    }

    class AdministradorController {
        +gerenciarClientes()
        +gerenciarVeterinarios()
    }

    class VeterinarioController {
        +gerenciarAnimais()
        +elaborarProntuario()
    }

    class ClienteController {
        +visualizarConsultas()
        +receberNotificacoes()
    }

    class AnimalController {
        +cadastrarAnimal(String nome, String especie, int idade)
        +buscarAnimalPorId(int id)
    }

    class ConsultaController {
        +agendarConsulta(String data, String horario, int idAnimal, int idVeterinario)
        +cancelarConsulta(int id)
    }

    class ProntuarioController {
        +salvarProntuario(String historico, String tratamentos, String diagnosticos)
        +buscarProntuarioPorId(int id)
    }

    class LoginView {
        +exibirTelaLogin()
        +capturarDadosLogin()
    }

    class AdministradorView {
        +exibirTelaAdministrador()
        +capturarDadosGerenciamento()
    }

    class VeterinarioView {
        +exibirTelaVeterinario()
        +capturarDadosAnimais()
    }

    class ClienteView {
        +exibirTelaCliente()
        +capturarDadosConsultas()
    }

    Usuario <|-- Administrador
    Usuario <|-- Veterinario
    Usuario <|-- Cliente

    Cliente "1" *-- "0..*" Animal : possui
    Veterinario "1" *-- "0..*" Prontuario : elabora
    Consulta "1" *-- "1" Animal : trata
    Consulta "1" *-- "1" Cliente : pertence
    Consulta "1" *-- "1" Veterinario : realizadaPor
    Notificacao "1" *-- "1" Cliente : enviaPara

    UsuarioDAO --> Usuario
    AdministradorDAO --> Administrador
    VeterinarioDAO --> Veterinario
    ClienteDAO --> Cliente
    AnimalDAO --> Animal
    ConsultaDAO --> Consulta
    ProntuarioDAO --> Prontuario

    UsuarioService --> UsuarioDAO
    AdministradorService --> AdministradorDAO
    VeterinarioService --> VeterinarioDAO
    ClienteService --> ClienteDAO
    AnimalService --> AnimalDAO
    ConsultaService --> ConsultaDAO
    ProntuarioService --> ProntuarioDAO
    NotificacaoService --> Notificacao

    UsuarioController --> UsuarioService
    AdministradorController --> AdministradorService
    VeterinarioController --> VeterinarioService
    ClienteController --> ClienteService
    AnimalController --> AnimalService
    ConsultaController --> ConsultaService
    ProntuarioController --> ProntuarioService

    LoginView --> UsuarioController
    AdministradorView --> AdministradorController
    VeterinarioView --> VeterinarioController
    ClienteView --> ClienteController