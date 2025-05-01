package com.clinica.observer;

/**
 * Interface para os Observers que serão notificados sobre mudanças nos dados.
 */
public interface DataObserver {
    /**
     * Método chamado pelo Subject (ClinicaFacade) quando os dados mudam.
     * @param typeChanged O tipo de dado que foi modificado.
     */
    void update(DataType typeChanged);
}
