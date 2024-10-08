import java.nio.file.Path
import repository.EmpleadoRepository

fun main() {
    // Crear una instancia de EmpleadoRepository
    val empleadoRepository = EmpleadoRepository()

    // Ruta del archivo CSV
    val rutaCSV = Path.of("src/main/resources/empleados.csv")
    // Ruta del archivo XML
    val rutaXML = Path.of("src/main/resources/empleados.xml")

    // 1. Leer empleados desde el archivo CSV
    val empleados = empleadoRepository.leerEmpleadosDesdeCSV(rutaCSV)

    // 2. Generar el archivo XML desde los datos del CSV
    empleadoRepository.generarXMLDesdeEmpleados(empleados, rutaXML)

    // 3. Modificar el salario de un empleado
    empleadoRepository.modificarSalarioEnXML(rutaXML, 2, 4500.0)

    // 4. Leer y mostrar los empleados desde el XML modificado
    empleadoRepository.leerEmpleadosDesdeXML(rutaXML)
}

