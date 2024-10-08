package repository

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import model.Empleado

class EmpleadoRepository {

    // Función que lee el archivo CSV y retorna una lista de Empleados
    fun leerEmpleadosDesdeCSV(ruta: Path): List<Empleado> {
        val empleados = mutableListOf<Empleado>()

        try {
            // Crear el BufferedReader para leer el archivo .csv
            val br: BufferedReader = Files.newBufferedReader(ruta)

            // Usar el BufferedReader con .use para leer cada línea del archivo .csv
            br.use { reader ->
                reader.lines().skip(1).forEach { linea ->
                    val campos = linea.split(",")

                    try {
                        val id = campos[0].toInt()
                        val apellido = campos[1]
                        val departamento = campos[2]
                        val salario = campos[3].toDouble()

                        // Añadir el empleado a la lista
                        empleados.add(Empleado(id, apellido, departamento, salario))
                    } catch (e: NumberFormatException) {
                        println("Error: Formato numérico incorrecto en el CSV. Línea: $linea")
                    }
                }
            }
        }
        // Excepción si no se encuentra la ruta del archivo .csv
        catch (e: FileNotFoundException) {
            println("Error: El archivo CSV no se encontró en la ruta: $ruta")
        }
        // Excepción si ocurre cualquier problema inesperado al leer el archivo .csv
        catch (e: IOException) {
            println("Error: Ocurrió un problema al leer el archivo CSV: ${e.message}")
        }
        return empleados
    }

    // Función que genera un archivo XML con los datos de empleados
    fun generarXMLDesdeEmpleados(empleados: List<Empleado>, rutaXML: Path) {
        try {
            // 1. Instanciar el DocumentBuilderFactory y el DocumentBuilder
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()

            // 2. Crear un nuevo documento vacío
            val document: Document = db.newDocument()

            // Se crea el elemento raíz <empleados>
            val rootElement = document.createElement("empleados")
            document.appendChild(rootElement)

            // 3. Añadir empleados como hijos del nodo root
            empleados.forEach { empleado ->
                // Crear elemento <empleado>
                val empleadoElement = document.createElement("empleado")
                empleadoElement.setAttribute("id", empleado.id.toString())

                // Crear y añadir elementos hijo: <apellido>, <departamento>, <salario>
                val apellidoElement = document.createElement("apellido")
                apellidoElement.textContent = empleado.apellido
                empleadoElement.appendChild(apellidoElement)

                val departamentoElement = document.createElement("departamento")
                departamentoElement.textContent = empleado.departamento
                empleadoElement.appendChild(departamentoElement)

                val salarioElement = document.createElement("salario")
                salarioElement.textContent = empleado.salario.toString()
                empleadoElement.appendChild(salarioElement)

                // Añadir <empleado> al root <empleados>
                rootElement.appendChild(empleadoElement)
            }

            // 4. Transformar el Document a un archivo XML
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()

            // Indentar el XML para que sea legible
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")

            val source = DOMSource(document)
            val result = StreamResult(rutaXML.toFile())
            transformer.transform(source, result)

        }
        // Excepción si da problemas confiurar el parser xml
        catch (e: ParserConfigurationException) {
            println("Error al configurar el parser XML: ${e.message}")
        }
        // Excepción si ocurre cualquier problema inesperado al excribir el archivo .xml
        catch (e: IOException) {
            println("Error al escribir el archivo XML: ${e.message}")
        }
    }

    // Función que modifica el salario de un empleado en el XML
    fun modificarSalarioEnXML(rutaXML: Path, idEmpleado: Int, nuevoSalario: Double) {
        try {
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val document = db.parse(rutaXML.toFile())

            // Obtener la lista de nodos <empleado>
            val empleados = document.getElementsByTagName("empleado")

            for (i in 0 until empleados.length) {
                val empleado = empleados.item(i) as Element
                val id = empleado.getAttribute("id").toInt()

                // Si el ID coincide, modificar el salario
                if (id == idEmpleado) {
                    val salarioElement = empleado.getElementsByTagName("salario").item(0)
                    salarioElement.textContent = nuevoSalario.toString()
                    break
                }
            }

            // Guardar el XML modificado
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            val source = DOMSource(document)
            val result = StreamResult(rutaXML.toFile())
            transformer.transform(source, result)

        }
        // Excepción si no se encuentra el archivo .xml
        catch (e: FileNotFoundException) {
            println("Error: No se encontró el archivo XML: ${e.message}")
        }
        // Excepción si da problemas parsear el archivo .xml
        catch (e: SAXException) {
            println("Error al parsear el archivo XML: ${e.message}")
        }
        // Excepción si cualquier problema de entrada y salida inesperado
        catch (e: IOException) {
            println("Error de entrada/salida: ${e.message}")
        }
    }

    // Función que lee y muestra los empleados desde el XML
    fun leerEmpleadosDesdeXML(rutaXML: Path) {
        try {
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val document = db.parse(rutaXML.toFile())

            val empleados = document.getElementsByTagName("empleado")

            for (i in 0 until empleados.length) {
                val empleado = empleados.item(i) as Element

                val id = empleado.getAttribute("id")
                val apellido = empleado.getElementsByTagName("apellido").item(0).textContent
                val departamento = empleado.getElementsByTagName("departamento").item(0).textContent
                val salario = empleado.getElementsByTagName("salario").item(0).textContent

                println("ID: $id, Apellido: $apellido, Departamento: $departamento, Salario: $salario")
            }
        }
        // Excepción si no se encuentra el archivo .xml
        catch (e: FileNotFoundException) {
            println("Error: No se encontró el archivo XML: ${e.message}")
        }
        // Excepción si da problemas parsear el archivo .xml
        catch (e: SAXException) {
            println("Error al parsear el archivo XML: ${e.message}")
        }
        // Excepción si cualquier problema de entrada y salida inesperado
        catch (e: IOException) {
            println("Error de entrada/salida: ${e.message}")
        }
    }
}