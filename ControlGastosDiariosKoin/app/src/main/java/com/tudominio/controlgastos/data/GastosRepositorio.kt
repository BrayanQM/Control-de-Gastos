package com.tudominio.controlgastos.data

interface GastosRepositorio {
    fun obtenerGastos(): List<Gasto>
    fun guardarGasto(monto: Double, descripcion: String)
    fun obtenerTotalGastos(): Double
    fun eliminarGasto(id: Int)
}

class GastosRepositorioSimulado : GastosRepositorio {
    // Lista mutable que simula una base de datos en memoria
    private val gastos = mutableListOf<Gasto>()
    private var contadorId = 1

    init {
        // Datos de ejemplo al iniciar la app
        gastos.add(Gasto(contadorId++, 25.50, "Café y empanadas", Gasto.obtenerFechaActual()))
        gastos.add(Gasto(contadorId++, 45.00, "Almuerzo ejecutivo", Gasto.obtenerFechaActual()))
        gastos.add(Gasto(contadorId++, 120.00, "Supermercado", Gasto.obtenerFechaActual()))
        gastos.add(Gasto(contadorId++, 15.00, "Taxi", Gasto.obtenerFechaActual()))
    }

    override fun obtenerGastos(): List<Gasto> {
        // Simula una pequeña demora de red (útil para ver loading states)
        Thread.sleep(1000)
        return gastos.toList() // Retorna una copia para evitar modificaciones externas
    }

    override fun guardarGasto(monto: Double, descripcion: String) {
        val nuevoGasto = Gasto(
            id = contadorId++,
            monto = monto,
            descripcion = descripcion,
            fecha = Gasto.obtenerFechaActual()
        )
        gastos.add(0, nuevoGasto) // Lo agrega al principio (más reciente primero)
    }

    override fun obtenerTotalGastos(): Double {
        return gastos.sumOf { it.monto }
    }

    override fun eliminarGasto(id: Int) {
        gastos.removeAll { it.id == id }
    }
}
