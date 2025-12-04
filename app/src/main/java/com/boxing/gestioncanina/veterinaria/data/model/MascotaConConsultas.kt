package com.boxing.gestioncanina.veterinaria.data.model

/**
 * Modelo que combina una mascota con sus consultas veterinarias.
 * Útil para mostrar el historial completo de una mascota.
 */
data class MascotaConConsultas(
    val mascota: Mascota,
    val consultas: List<ConsultaVeterinaria>
) {

    /**
     * Obtiene el total de consultas realizadas.
     */
    fun getTotalConsultas(): Int = consultas.size

    /**
     * Obtiene la última consulta realizada.
     */
    fun getUltimaConsulta(): ConsultaVeterinaria? = consultas.firstOrNull()

    /**
     * Obtiene el costo total de todas las consultas.
     */
    fun getCostoTotal(): Double = consultas.sumOf { it.costo }

    /**
     * Obtiene consultas por rango de fechas.
     */
    fun getConsultasPorRango(fechaInicio: Long, fechaFin: Long): List<ConsultaVeterinaria> {
        return consultas.filter { it.fecha in fechaInicio..fechaFin }
    }

    /**
     * Verifica si la mascota tiene consultas pendientes.
     */
    fun tieneConsultasPendientes(): Boolean {
        return consultas.any { it.tieneProximaCita() }
    }

    /**
     * Obtiene la próxima cita programada.
     */
    fun getProximaCita(): ConsultaVeterinaria? {
        return consultas
            .filter { it.tieneProximaCita() }
            .minByOrNull { it.proximaCita ?: Long.MAX_VALUE }
    }
}