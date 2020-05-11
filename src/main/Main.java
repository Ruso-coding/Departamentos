package main;

import java.io.File;

import javax.swing.UIManager;

import gui.ventanas.Conector;
import gui.ventanas.GestionarDepartamentos;

public class Main {

	public static void main(String[] args) {
		try {
			crearCarpetaPlanillas();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// Conector.getInstance().setVisible(true);
			GestionarDepartamentos.getInstance().setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void crearCarpetaPlanillas() {
		// Indica la ruta en donde se va a crear la carpeta "Planillas"
		String rutaAbsoluta = System.getProperty("user.home") + "/Desktop/Planillas";
		File file = new File(rutaAbsoluta);

		// Si la carpeta no existe, entonces...
		if (!file.exists()) {
			// Crea la carpeta
			// file.mkdir();
		}

	}

}
