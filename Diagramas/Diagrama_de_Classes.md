```mermaid
classDiagram
    directison LR

    %% ==== Model Inheritance & Composition/Association ====
    Usuario <|-- Cliente
    Usuario <|-- Veterinario
    Cliente "1" *-- "0..*" Animal : possui
    Consulta --> "1" Cliente : referente_a
  ad  Consulta --> "0..1" Animal : referente_a
    Consulta --> "1" Veterinario : realizado_por
    Prontuario --> "1" Consulta : associado_a
    Consulta "1" *-- "1" Consulta_Builder : constrói
dadad
    %% ==== Persistence Layer Dependencies ====
    ClienteDAO ..> JsonPersistenceHelper~Cliente~
    AnimalDAO ..> JsonPersistenceHelper~Animal~
    VeterinarioDAO ..> JsonPersistenceHelper~Veterinario~
    ConsultaDAO ..> JsonPersistenceHelper~Consulta~
    ProntuarioDAO ..> JsonPersistenceHelper~Prontuario~
    EmpresaDAO ..> JsonPersistenceHelper~Empresa~
    AgendaDAO ..> ConsultaDAO : consulta
    ConsultaDAO ..> ClienteDAO : cliente
    ConsultaDAO ..> AnimalDAO : animal
    ConsultaDAO ..> VeterinarioDAO : veterinário
    ProntuarioDAO ..> ConsultaDAO : consulta

    %% ==== Interface Implementation ====
    Usuario ..|> JsonPersistenceHelper_Identifiable
    Cliente ..|> JsonPersistenceHelper_Identifiable
    Veterinario ..|> JsonPersistenceHelper_Identifiable
    Animal ..|> JsonPersistenceHelper_Identifiable
    Consulta ..|> JsonPersistenceHelper_Identifiable
    Prontuario ..|> JsonPersistenceHelper_Identifiable
    Empresa ..|> JsonPersistenceHelper_Identifiable
    Agenda ..|> JsonPersistenceHelper_Identifiable

    %% ==== Facade Dependencies ====
    ClinicaFacade --> ClienteDAO : gerencia
    ClinicaFacade --> AnimalDAO : gerencia
    ClinicaFacade --> VeterinarioDAO : gerencia
    ClinicaFacade --> ConsultaDAO : gerencia
    ClinicaFacade --> ProntuarioDAO : gerencia
    ClinicaFacade --> AgendaDAO : gerencia
    ClinicaFacade --> EmpresaDAO : gerencia
    ClinicaFacade ..> ProntuarioController : coordena

    %% ==== Controller Dependencies ====
    ClienteController ..> ClienteDAO
    AnimalController ..> AnimalDAO
    VeterinarioController ..> VeterinarioDAO
    ConsultaController ..> ConsultaDAO
    ProntuarioController ..> ProntuarioDAO
    ProntuarioController ..> ConsultaDAO
    ClienteController ..> EnderecoAdapter

    %% ==== View Dependencies ====
    App --> TelaEmpresa : inicia
    TelaEmpresa --> PainelGerenciarClientes
    TelaEmpresa --> PainelGerenciarVeterinarios
    TelaEmpresa --> PainelGerenciarConsultas
    TelaEmpresa --> PainelVerAgenda
    TelaEmpresa --> PainelRelatorios
    
    PainelGerenciarClientes ..> ClinicaFacade
    PainelGerenciarVeterinarios ..> ClinicaFacade
    PainelGerenciarConsultas ..> ClinicaFacade
    PainelVerAgenda ..> ClinicaFacade
    PainelRelatorios ..> ClinicaFacade
    
    PainelGerenciarClientes --> ClienteFormDialog
    PainelGerenciarClientes --> AnimalDialog
    PainelGerenciarConsultas --> ConsultaFormDialog
    PainelGerenciarVeterinarios --> VeterinarioFormDialog
    
    AnimalDialog ..> ClinicaFacade
    ConsultaFormDialog ..> ClinicaFacade
    PainelVerAgenda --> VeterinarioComboItem
    ConsultaFormDialog --> AnimalComboItem
    ConsultaFormDialog --> VeterinarioComboItem

    %% ==== Observer Pattern ====
    ClinicaFacade ..> DataObserver : notifica
    PainelGerenciarClientes ..|> DataObserver
    PainelGerenciarVeterinarios ..|> DataObserver
    PainelGerenciarConsultas ..|> DataObserver
    PainelVerAgenda ..|> DataObserver
    ClinicaFacade ..> DataType

    %% ==== Command Pattern ====
    PainelGerenciarVeterinarios --> UndoManager
    UndoManager o-- Command
    AddVeterinarioCommand ..|> Command
    UpdateVeterinarioCommand ..|> Command
    DeleteVeterinarioCommand ..|> Command
    AddVeterinarioCommand ..> ClinicaFacade
    UpdateVeterinarioCommand ..> ClinicaFacade
    DeleteVeterinarioCommand ..> ClinicaFacade

    %% ==== Report Pattern ====
    PDFReportGenerator <|-- ClientReportPDFGenerator
    PainelGerenciarClientes --> ClientReportPDFGenerator

    %% ==== Adapter/DTO ====
    EnderecoAdapter ..> EnderecoViaCepDTO
    ClienteController ..> EnderecoAdapter

    %% ==== Validation ====
    ClienteController ..> ValidadorUtil
    PainelGerenciarClientes ..> ValidadorUtil
    PainelGerenciarVeterinarios ..> ValidadorUtil
    ClienteFormDialog ..> ValidadorUtil
    VeterinarioFormDialog ..> ValidadorUtil

    %% ==== Class Definitions ====
    class JsonPersistenceHelper_Identifiable {
        <<Interface>>
        +getId() int
        +setId(int id) void
    }
    
    class DataType {
        <<enum>>
        CLIENTE ANIMAL VETERINARIO CONSULTA AGENDA PRONTUARIO EMPRESA
    }
    
    class Command {
        <<Interface>>
        +execute() void
        +undo() void
    }