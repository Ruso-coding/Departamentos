package conector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

// https://msdn.microsoft.com/es-es/library/ms345343.aspx <-- Tutorial para abrir puertos desde otra pc

public class Conexion {

	// Datos de la conexion
	private static final String TIPO = "jdbc";
	private static final String MOTOR = "sqlserver";
	public static String servidor; /* LOCAL (lan): localhost, 127.0.0.1, IP Privada (IPv4: 192.168.1.22) o el nombre de la maquina (Juan) - REMOTA (wan): IP Publica? */
	public static final int PUERTO = 1433; // Puerto por defecto, se busca desde el Administrador de configuración de SQL Server
	private static final String DB = "DEPARTAMENTO";
	private static final String USER = "usuario";
	private static final String PASS = "1234";

	public static Connection getConnection() {

		// Driver de SQLServer y URL (Ruta) de la base de datos
		String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // Para obtener este driver necesitamos la libreria de SQLServer (sqljdbc4.jar)

		/* El parámetro url del método getConnection(), es un URL de base de datos que especifica
		 * el subprotocolo (el mecanismo de conectividad de bases de datos), el identificador de la base de datos
		 * o del servidor de bases de datos y una lista de propiedades. */
		String url = TIPO + ":" + MOTOR + "://" + servidor + ":" + PUERTO + ";databaseName=" + DB + ";user=" + USER + ";password=" + PASS;

		Connection conexion = null;

		try {

			/* Lo primero que se debe realizar para poder conectarse a una base de datos es
			 * cargar el driver encargado de ésta función. Para ello es utilizada la llamada Class.forName(). */
			Class.forName(driver); // TIP: JDBC4 soporta el descubrimiento automatico de controladores; ya no tenemos que cargar el controlador de la base de datos por adelantado.

			/* Si uno de los drivers que hemos cargado reconoce la URL suministrada por el método DriverManager.getConnection(url),
			 * dicho driver establecerá una conexión con el controlador de base de datos especificado en la URL del JDBC.
			 * La clase DriverManager, como su nombre indica, maneja todos los detalles del establecimiento
			 * de la conexión detrás de la escena. A menos que estemos escribiendo un driver, posiblemente nunca
			 * utilizaremos ningún método del interface Driver. La conexión devuelta por el método DriverManager.getConnection
			 * es una conexión abierta que se puede utilizar para crear sentencias JDBC que pasen
			 * nuestras sentencias SQL al controlador de la base de datos. */
			conexion = DriverManager.getConnection(url);

			System.out.println("Conectado!");

		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "El driver de la base de datos no existe.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
		}

		/* FIXME: Agregar una excepcion para la IP o puerto incorrectos.
		 * Mensaje de error: "La IP o el puerto no son correctos." */

		return conexion;

	}

}
