package com.example.caso_dsy1105_002v_tacticaldev.Model

enum class EstadoMaquina(val descripcion: String) {
    OPERATIVO("Equipo funcionando en óptimas condiciones"),
    EN_FALLA("Presenta fallas técnicas o de enfriamiento"),
    REQUIERE_MANTENIMIENTO("Necesita revisión preventiva o cambio de piezas"),
    FUERA_DE_SERVICIO("Inoperativo / Desconectado permanentemente")
}