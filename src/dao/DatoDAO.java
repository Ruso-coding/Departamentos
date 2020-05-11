package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import conector.Conexion;
import vo.DatoVO;

// CLASE PUENTE
// Data Access Object - Puente entre el sistema y la base de datos
public class DatoDAO {

	// El atributo y los metodos creados en esta clase pertenecen a la misma, no a los objetos creados a partir de ella
	private static ArrayList<DatoVO> datos = new ArrayList<DatoVO>();

	// Consulta para la tabla (ID, Edificio, Departamento, Propietario y Telefono)
	public static ArrayList<DatoVO> consultarTodo(String consulta) {

		/* FIXME: Declararlas como locales o globales? */
		Connection conexion = null; // Metodos para crear instrucciones y para gestionar conexiones y sus propiedades
		Statement sentencia = null; // Permite enviar instrucciones a la BD
		ResultSet rs = null; // Conjunto de resultados que se devuelven de una consulta

		try {

			System.out.println("\nConectando...");

			// Captura la conexion que devuelve el metodo getConnection() de la clase Conexion
			conexion = Conexion.getConnection();

			// Si la conexion es distinta a null, es decir, si se pudo conectar, entonces...
			if (conexion != null) {

				/* El metodo createStatement() se utiliza para crear un objeto que modela a una
				 * sentencia SQL. Es un objeto del tipo de una clase que implementa la interfaz
				 * Statement, y provee la infraestructura para ejecutar sentencias SQL
				 * sobre una conexion con una base de datos. */
				sentencia = conexion.createStatement();

				/* El metodo executeQuery() se utiliza para ejecutar una sentencia SQL y obtener el
				 * resultado correspondiente dentro de un objeto del tipo ResulSet. Este objeto
				 * representa un conjunto de resultados que se obtienen como consecuencia de
				 * ejecutar la sentencia SQL del tipo SELECT a traves de la conexion. */
				rs = sentencia.executeQuery(consulta);

				/* Primero, el programa posiciona el cursor de ResultSet (que apunta a la fila que esta procesando)
				 * en la primera fila en el objeto ResultSet, mediante el metodo next. Este metodo devuelve el valor
				 * boolean true si el cursor puede posicionarse en la siguiente fila; en caso contrario el metodo
				 * devuelve false. */
				while (rs.next()) {

					// Crea un nuevo objeto (fila, propietario o dato)
					DatoVO datoVO = new DatoVO();

					// Setea los valores de la base de datos al objeto
					datoVO.setId(rs.getInt("ID"));
					datoVO.setEdificio(rs.getString("Edificio"));
					datoVO.setDepartamento(rs.getString("Departamento"));
					datoVO.setPropietario(rs.getString("Propietario"));
					datoVO.setTelefono(rs.getString("Telefono"));

					// Agrega el objeto al ArrayList<DatoVO>
					datos.add(datoVO);

					/* FIXME: Lo datos se van a cargar mas lentos si libero el objeto de cada fila. */
					// datoVO = null;
					// liberarMemoria();

				}

				System.out.println("Consulta realizada con exito!");

			}

		} catch (SQLException e) {
			System.err.println("Error al consultar: " + e.getMessage());
		} finally {

			/* Esto es muy importante hacerlo, ya que si se queda abierta, estamos desaprovechando
			 * recursos, creando lo que se llama connection leaks (conexiones perdidas)
			 * con la base de datos. */

			if (sentencia != null) {
				try {
					sentencia.close();
				} catch (SQLException e) {
					System.out.println("Error al liberar la sentencia: " + e.getMessage());
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					System.out.println("Error al liberar los datos: " + e.getMessage());
				}
			}
			if (conexion != null) {
				try {
					conexion.close();
					System.out.println("Se desconecto!");
				} catch (SQLException e) {
					System.out.println("Error al desconectarse: " + e.getMessage());
				}
			}

		}

		return datos;
	}

	// Consulta para el combo (Departamento)
	public static ArrayList<DatoVO> consultarDepartamentos(String consulta) {

		Connection conexion = null;
		Statement sentencia = null;
		ResultSet rs = null;

		try {

			System.out.println("\nConectando...");

			conexion = Conexion.getConnection();

			if (conexion != null) {

				sentencia = conexion.createStatement();

				rs = sentencia.executeQuery(consulta);

				while (rs.next()) {

					DatoVO datoVO = new DatoVO();

					datoVO.setDepartamento(rs.getString("Departamento"));

					datos.add(datoVO);

				}

				System.out.println("Consulta realizada con exito!");

			}

		} catch (SQLException e) {
			System.err.println("Error al consultar: " + e.getMessage());
		} finally {
			if (sentencia != null) {
				try {
					sentencia.close();
				} catch (SQLException e) {
					System.out.println("Error al liberar la sentencia: " + e.getMessage());
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					System.out.println("Error al liberar los datos: " + e.getMessage());
				}
			}
			if (conexion != null) {
				try {
					conexion.close();
					System.out.println("Se desconecto!");
				} catch (SQLException e) {
					System.out.println("Error al desconectarse: " + e.getMessage());
				}
			}

		}

		return datos;
	}

	public static boolean actualizar(String consulta) {

		boolean bandera = false;

		Connection conexion = null;
		PreparedStatement ps = null;

		try {

			System.out.println("\nConectando...");

			conexion = Conexion.getConnection();

			if (conexion != null) {

				ps = conexion.prepareStatement(consulta);

				ps.executeUpdate();

				bandera = true;

			}

			System.out.println("Actualizacion realizado con exito!");

		} catch (SQLException e) {
			System.err.println("Error al actualizar: " + e.getMessage());
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.out.println("Error al liberar la sentencia preparada: " + e.getMessage());
				}
			}
			if (conexion != null) {
				try {
					conexion.close();
					System.out.println("Se desconecto!");
				} catch (SQLException e) {
					System.out.println("Error al desconectarse: " + e.getMessage());
				}
			}

		}

		return bandera;
	}

}
