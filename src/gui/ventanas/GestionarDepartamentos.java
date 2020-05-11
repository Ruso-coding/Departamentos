package gui.ventanas;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import celda.MyRenderer;
import dao.DatoDAO;
import vo.DatoVO;

/* Optimizaciones */
/* -Cada vez que se producia un evento sobre un JComboBox, se creaba un nuevo objeto DatoDAO. Esto 
 * ocupaba un espacio en la memoria RAM, lo cual resultaba ineficiente. Se soluciono implementando una instancia
 * de la clase DatoDAO, en la cual comparte todos sus metodos estaticos.*/

/* No tienen nada que ver los items de un JComboBox con los datos de un ArrayList, 
 * lo mismo pasa con las filas del JTable, no tienen nada que ver las filas con los datos del ArrayList. */

public class GestionarDepartamentos extends JFrame implements ActionListener, TableModelListener {
	private static final long serialVersionUID = 1L;

	// Componentes visuales del paquete swing
	private JPanel panelBusqueda;
	private JPanel panelFiltro;
	private JPanel panelTabla;
	private JPanel panelInfo;
	private JLabel lbl1;
	private JLabel lbl2;
	private JLabel lblDiseoJuan;
	private JButton btnVerTodo;
	private JComboBox<String> cbBloques;
	private JComboBox<String> cbDepartamentos;
	private JComboBox<String> cbEdificios;

	// Encabezados de los JComboBox
	private final String[] ENCABEZADO_EDIFICIO = { "-Edificio-", "Casona", "Solano" };
	private final String[] ENCABEZADO_BLOQUE = { "-Bloque-", "Bloque 1", "Bloque 2" };
	private final String[] ENCABEZADO_DEPARTAMENTO = { "-Departamento-" };

	// Tabla, modelo y barra
	private JTable tabla;
	private DefaultTableModel modelo; // Si se usa DefaultTableModel, todas las celdas son Object
	private JScrollPane barra;

	// Variable auxiliar
	private String valorOriginal;

	/* Constantes de objeto */
	/* SENTECIAS */

	// Consulta el ID, los edficios, los departamentos, los propietarios y los telefonos de todos los edificios
	private final String CONSULTA_TODO = "SELECT d.ID_DATO AS 'ID', e.Edificio, d.Departamento, d.Propietario, d.Telefono FROM Edificios e, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO ORDER BY d.ID_EDIFICIO ASC";

	// Consulta el ID, el edficio, los departamentos, los propietarios y los telefonos de Casona
	private final String CONSULTA_CASONA = "SELECT d.ID_DATO AS 'ID', e.Edificio, d.Departamento, d.Propietario, d.Telefono FROM Edificios e, Bloques b, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_BLOQUE = b.ID_BLOQUE";
	// Consulta el ID, el edficio, los departamentos, los propietarios y los telefonos de Casona del bloque 1
	private final String CONSULTA_CASONA_BLOQUE_1 = "SELECT d.ID_DATO AS 'ID', e.Edificio, d.Departamento, d.Propietario, d.Telefono FROM Edificios e, Bloques b, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_BLOQUE = b.ID_BLOQUE AND d.ID_BLOQUE = 1";
	// Consulta el ID, el edficio, los departamentos, los propietarios y los telefonos de Casona del bloque 2
	private final String CONSULTA_CASONA_BLOQUE_2 = "SELECT d.ID_DATO AS 'ID', e.Edificio, d.Departamento, d.Propietario, d.Telefono FROM Edificios e, Bloques b, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_BLOQUE = b.ID_BLOQUE AND d.ID_BLOQUE = 2";
	// Consulta los departamentos de Casona
	private final String CONSULTA_CASONA_DEPARTAMENTOS = "SELECT d.Departamento FROM Edificios e, Datos d WHERE d.ID_EDIFICIO = 1 AND d.ID_EDIFICIO = e.ID_EDIFICIO";
	// Consulta los departamentos de Casona del bloque 1
	private final String CONSULTA_CASONA_DEPARTAMENTOS_BLOQUE_1 = "SELECT d.Departamento FROM Edificios e, Bloques b, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_BLOQUE = b.ID_BLOQUE AND d.ID_BLOQUE = 1";
	// Consulta los departamentos de Casona del bloque 2
	private final String CONSULTA_CASONA_DEPARTAMENTOS_BLOQUE_2 = "SELECT d.Departamento FROM Edificios e, Bloques b, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_BLOQUE = b.ID_BLOQUE AND d.ID_BLOQUE = 2";

	// Consulta el ID, el edficio, los departamentos, los propietarios y los telefonos de Solano
	private final String CONSULTA_SOLANO = "SELECT d.ID_DATO AS 'ID', e.Edificio, d.Departamento, d.Propietario, d.Telefono FROM Edificios e, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_EDIFICIO = 2 ";
	// Consulta los departamentos de Solano
	private final String CONSULTA_SOLANO_DEPARTAMENTOS = "SELECT d.Departamento FROM Edificios e, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_EDIFICIO = 2 ";

	// Declara e inicializa el ArrayList como variable global, ya que es usado por varios metodos de la clase
	private ArrayList<DatoVO> datos = new ArrayList<DatoVO>();

	// Crea un solo objeto de la clase para usar el mismo y no consumir recursos innecesarios con nuevos objetos
	private static GestionarDepartamentos instance = null;

	public static GestionarDepartamentos getInstance() {
		if (instance == null)
			instance = new GestionarDepartamentos();
		return instance;
	}

	// Constructor sin argumentos
	private GestionarDepartamentos() {
		createGUI();
		setFrameProperties();
	}

	// Interfaz de usuario
	private void createGUI() {
		// Contenedor de la ventana que va a administrar los componentes mediante un BorderLayout
		getContentPane().setLayout(new BorderLayout(0, 0));

		/* PANEL BUSQUEDA */
		panelBusqueda = new JPanel();
		panelBusqueda.setLayout(new BorderLayout(0, 0));

		btnVerTodo = new JButton("Ver todo");
		btnVerTodo.setFocusable(false);
		btnVerTodo.addActionListener(this);
		panelBusqueda.add(btnVerTodo, BorderLayout.WEST);

		panelFiltro = new JPanel();
		panelFiltro.setBorder(new TitledBorder(null, "Filtro", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelFiltro.setLayout(new BoxLayout(panelFiltro, BoxLayout.X_AXIS));

		cbEdificios = new JComboBox<String>();
		cbEdificios.setModel(new DefaultComboBoxModel<String>(ENCABEZADO_EDIFICIO));
		cbEdificios.setFocusable(false);
		cbEdificios.addActionListener(this);
		panelFiltro.add(cbEdificios);

		lbl1 = new JLabel("    ");
		panelFiltro.add(lbl1);

		cbBloques = new JComboBox<String>();
		cbBloques.setModel(new DefaultComboBoxModel<String>(ENCABEZADO_BLOQUE));
		cbBloques.setFocusable(false);
		cbBloques.setEnabled(false);
		cbBloques.addActionListener(this);
		panelFiltro.add(cbBloques);

		lbl2 = new JLabel("    ");
		panelFiltro.add(lbl2);

		cbDepartamentos = new JComboBox<String>();
		cbDepartamentos.setModel(new DefaultComboBoxModel<String>(ENCABEZADO_DEPARTAMENTO));
		cbDepartamentos.setFocusable(false);
		cbDepartamentos.setEnabled(false);
		cbDepartamentos.addActionListener(this);
		panelFiltro.add(cbDepartamentos);
		panelBusqueda.add(panelFiltro);

		getContentPane().add(panelBusqueda, BorderLayout.NORTH);

		/* PANEL TABLA */
		panelTabla = new JPanel();
		panelTabla.setLayout(new BorderLayout(0, 0));

		// Crea el modelo
		modelo = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int fila, int columna) {
				// Las columnas "ID", "Edificio", "Departamento" y "Excel", no se pueden editar
				if (columna == 0 || columna == 1 || columna == 2 || columna == 5)
					return false;
				return true;
			}

			// Le indico que la columna "Excel" va a contener solo componentes de tipo JButton
			public Class<?> getColumnClass(int columna) {
				if (columna == 5)
					return JButton.class;
				return Object.class;
			}

		};

		// Agrega las columnas al modelo
		modelo.addColumn("ID"); /* FIXME: Ocultar la columna ID. */
		modelo.addColumn("Edificio");
		modelo.addColumn("Dpto.");
		modelo.addColumn("Propietario");
		modelo.addColumn("Telefono");
		/* FIXME: Agregar columna "Email". */
		modelo.addColumn("Excel");

		// Colocar esta linea despues de crear las columnas
		modelo.addTableModelListener(this);

		// Crea la tabla y le pasa el modelo
		tabla = new JTable(modelo);
		// Encabezado de la tabla
		tabla.getTableHeader().setReorderingAllowed(false);
		tabla.getTableHeader().setResizingAllowed(false);
		tabla.getTableHeader().setOpaque(false);
		tabla.getColumnModel().getColumn(0).setPreferredWidth(5);
		tabla.getColumnModel().getColumn(1).setPreferredWidth(45);
		tabla.getColumnModel().getColumn(2).setPreferredWidth(45); // 25
		tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
		tabla.getColumnModel().getColumn(4).setPreferredWidth(150);
		tabla.getColumnModel().getColumn(5).setPreferredWidth(35);

		// Solo permite seleccionar una sola fila
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Dibuja el componente (JButton) en la tabla
		tabla.getColumnModel().getColumn(5).setCellRenderer(new MyRenderer());

		// Si se produce un evento sobre la tabla, entonces...
		tabla.addMouseListener(new MouseAdapter() {

			// Cuando se hace click sobre una celda
			@Override
			public void mouseClicked(MouseEvent evt) {

				try {

					// Guarda la fila y columna seleccionada
					int fila = tabla.getSelectedRow();
					int columna = tabla.getSelectedColumn();

					// Si se selecciono una fila, entonces...
					if (fila != -1) { /* FIXME: != -1 o > -1? */

						// ID
						int id = (int) modelo.getValueAt(fila, 0);

						// Edificio
						String edificio = (String) modelo.getValueAt(fila, 1);

						// Departamento
						String departamento = (String) modelo.getValueAt(fila, 2);

						// Propietario
						String propietario = (String) modelo.getValueAt(fila, 3);

						// Excel
						Object excel = modelo.getValueAt(fila, 5);

						// Si la columna seleccionada es "Propietario" o "Telefono", entonces...
						if ((columna == 3) || (columna == 4))
							valorOriginal = String.valueOf(modelo.getValueAt(fila, columna));

						// Si la columna seleccionada es igual a un JButton y la celda es distinta a null, entonces...
						if ((modelo.getColumnClass(columna).equals(JButton.class)) && (excel != null))
							cargarArchivo(id, edificio, departamento, propietario);
					}

				}

				/* Se lanza cugtando el c�digo que depende de un teclado, la pantalla, o el rat�n se
				 * llama en un ambiente que no sea compatible con un teclado, la pantalla, o el rat�n. */
				catch (HeadlessException e) {
					JOptionPane
							.showMessageDialog(null, "Error: " + e + "\nInt�ntelo nuevamente.", " .::Error en la operacion::.", JOptionPane.ERROR_MESSAGE);
				}

			}

		});

		// Crea la barra y le pasa la tabla
		barra = new JScrollPane(tabla);
		panelTabla.add(barra);

		getContentPane().add(panelTabla, BorderLayout.CENTER);

		/* PANEL INFORMACION */
		panelInfo = new JPanel();
		panelInfo.setLayout(new BorderLayout(0, 0));

		lblDiseoJuan = new JLabel(" Juan\u00A9");
		lblDiseoJuan.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panelInfo.add(lblDiseoJuan);

		getContentPane().add(panelInfo, BorderLayout.SOUTH);

	}

	// Propiedades de la ventana
	private void setFrameProperties() {
		setTitle("Departamentos");
		// Por defecto la pantalla queda reducida a 600x600 pixeles cuando se minimiza el tama�o del JFrame
		setMinimumSize(new Dimension(600, 600)); // o setSize(600, 600);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("img/Logo1_64x58.png")).getImage());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// Eventos de accion
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == btnVerTodo) {
			actualizarCombos(false, false);
			encabezar_cbEdificios();
			encabezar_cbBloques();
			encabezar_cbDepartamentos();
			consultarTodo();
		}

		// Si se produce un evento sobre cbEdificios, entonces...
		if (evt.getSource() == cbEdificios) {

			/* ENCABEZADO */
			if (cbEdificios.getSelectedIndex() == 0) {
				actualizarCombos(false, false);
				encabezar_cbBloques();
				encabezar_cbDepartamentos();
				limpiarTabla();
			}

			/* CASONA */
			if (cbEdificios.getSelectedIndex() == 1) {
				actualizarCombos(true, true);
				encabezar_cbBloques();
				consultarCasona();
			}

			/* SOLANO */
			if (cbEdificios.getSelectedIndex() == 2) {
				actualizarCombos(false, true);
				encabezar_cbBloques();
				consultarSolano();
			}

		}

		// Si se produce un evento sobre cbBloques, entonces...
		if (evt.getSource() == cbBloques) {
			consultarCasonaPorBloque();
		}

		if (evt.getSource() == cbDepartamentos) {
			consultarDepartamento();
		}

	}

	// Eventos de JTable
	@Override
	public void tableChanged(TableModelEvent evt) {

		// Si el evento sucedido es un cambio (UPDATE), entonces...
		if (evt.getType() == TableModelEvent.UPDATE) {

			// Obtiene la fila y la columna
			int fila = evt.getFirstRow(); // Diferencia tabla.getSelectedRow(); ?
			int columna = evt.getColumn();

			// Toma el ID como identificador para poder hacer una modificacion
			Object id = modelo.getValueAt(fila, 0); /* FIXME: Lo declaro como Object? */

			// Toma el valor de la celda seleccionada, osea el valor nuevo
			Object valorNuevo = modelo.getValueAt(fila, columna);

			/* Propietario */
			if (columna == 3) {

				// Actualiza el nombre del propietario donde el ID es igual al ID de la fila seleccionada
				String consulta = "UPDATE Datos SET Propietario = '" + valorNuevo + "' WHERE ID_DATO = '" + id + "'";

				// Si la celda modificada es disntita al valor original, entonces...
				if (!valorNuevo.equals(valorOriginal)) {
					if (DatoDAO.actualizar(consulta))
						;
					else
						JOptionPane.showMessageDialog(null, "No se pudo actualizar!", "Error", JOptionPane.ERROR_MESSAGE);
				}

			}

			/* Telefono */
			if (columna == 4) {

				String consulta = "UPDATE Datos SET Telefono = '" + valorNuevo + "' WHERE ID_DATO = '" + id + "'";

				if (!valorNuevo.equals(valorOriginal)) {
					if (DatoDAO.actualizar(consulta))
						;
					else
						JOptionPane.showMessageDialog(null, "No se pudo actualizar!", "Error", JOptionPane.ERROR_MESSAGE);

				}

			}

		}

	}

	/* ---------------------------------------------------- */
	/* -------------- Comunicacion con la BD -------------- */
	/* ---------------------------------------------------- */

	private void consultarTodo() {

		// Limpia la tabla antes de cargar los datos
		limpiarTabla(); // Usar este metodo siempre en la cabezera del mismo

		// Le asigna otro ArrayList con la informacion cargada
		datos = DatoDAO.consultarTodo(CONSULTA_TODO);

		// Crea un vector de Objetos de 5 lugares (campos de la BD) para almacenar cada fila de la consulta
		Object fila[] = new Object[6];

		// Itera hasta el limite del ArrayList
		for (int i = 0; i < datos.size(); i++) {

			// Agrega cada dato a cada columna de la fila (6 columnas, 1 fila)
			fila[0] = datos.get(i).getId();
			fila[1] = datos.get(i).getEdificio();
			fila[2] = datos.get(i).getDepartamento();
			fila[3] = datos.get(i).getPropietario();
			fila[4] = datos.get(i).getTelefono();

			// Si el propietario es distinto a null, entonces...
			if (datos.get(i).getPropietario() != null) {
				// Si la columna "Propietario" no esta vacia, entonces...
				if (!datos.get(i).getPropietario().isEmpty())
					fila[5] = new JButton("...");
			}

			// Agrega la fila al modelo
			modelo.addRow(fila);

			// Vuelve a asignar null a esa columna
			fila[5] = null;
		}

		// Hacemos elegible el objeto fila para el GC, asignandole una referencia nula
		fila = null;

		// Libero el objeto despues de su uso
		liberarMemoria();

	}

	private void consultarDepartamento() {

		limpiarTabla();

		int edificio = cbEdificios.getSelectedIndex();
		int bloque = cbBloques.getSelectedIndex();

		// Evita que se produsca un NullPointException, ya que por defecto el cbDepartamentos no contiene items
		String departamento = null;
		if (cbDepartamentos.getItemCount() != 0)
			departamento = cbDepartamentos.getSelectedItem().toString();

		// Consulta el ID, el edificio, el departamento, el propietario y el telefono de cualquier edificio *NO TOMA EN CUENTA EL BLOQUE
		final String CONSULTA_DEPARTAMENTO = "SELECT d.ID_DATO AS 'ID', e.Edificio, d.Departamento, d.Propietario, d.Telefono FROM Edificios e, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_EDIFICIO = '" + edificio + "' AND d.Departamento = '" + departamento + "'";
		// Consulta el ID, el edificio, el departamento, el propietario y el telefono dependendiendo del bloque seleccionado
		final String CONUSLTA_DEPARTAMENTO_POR_BLOQUE = "SELECT d.ID_DATO AS 'ID', e.Edificio, d.Departamento, d.Propietario, d.Telefono FROM Edificios e, Bloques b, Datos d WHERE d.ID_EDIFICIO = e.ID_EDIFICIO AND d.ID_EDIFICIO = '" + edificio + "' AND d.ID_BLOQUE = b.ID_BLOQUE AND d.ID_BLOQUE = '" + bloque + "' AND d.Departamento = '" + departamento + "' ";

		// Si no se selecciono un bloque en especifico, entonces el programa no va a dividir los departamentos por bloques
		if (cbBloques.getSelectedIndex() == 0)
			datos = DatoDAO.consultarTodo(CONSULTA_DEPARTAMENTO);
		// Si se selecciono un bloque, entonces el programa va a dividir los departamentos por bloques
		if (cbBloques.getSelectedIndex() != 0)
			datos = DatoDAO.consultarTodo(CONUSLTA_DEPARTAMENTO_POR_BLOQUE);

		Object fila[] = new Object[6];

		for (int i = 0; i < datos.size(); i++) {

			fila[0] = datos.get(i).getId();
			fila[1] = datos.get(i).getEdificio();
			fila[2] = datos.get(i).getDepartamento();
			fila[3] = datos.get(i).getPropietario();
			fila[4] = datos.get(i).getTelefono();

			// Si el propietario es distinto a null, entonces...
			if (datos.get(i).getPropietario() != null) {
				// Si la columna "Propietario" no esta vacia, entonces...
				if (!datos.get(i).getPropietario().isEmpty())
					fila[5] = new JButton("...");
			}

			modelo.addRow(fila);

			fila[5] = null;

		}

		fila = null;
		liberarMemoria();

	}

	private void consultarCasona() {

		limpiarTabla();

		datos = DatoDAO.consultarTodo(CONSULTA_CASONA);

		Object fila[] = new Object[6];

		for (int i = 0; i < datos.size(); i++) {

			fila[0] = datos.get(i).getId();
			fila[1] = datos.get(i).getEdificio();
			fila[2] = datos.get(i).getDepartamento();
			fila[3] = datos.get(i).getPropietario();
			fila[4] = datos.get(i).getTelefono();

			// Si el propietario es distinto a null, entonces...
			if (datos.get(i).getPropietario() != null) {
				// Si la columna "Propietario" no esta vacia, entonces...
				if (!datos.get(i).getPropietario().isEmpty())
					fila[5] = new JButton("...");
			}

			modelo.addRow(fila);

			fila[5] = null;
		}

		fila = null;
		liberarMemoria();

		cargarComboCasona();

	}

	private void consultarCasonaPorBloque() {

		limpiarTabla();

		// Define la sentencia dependiendo del bloque seleccionado
		if (cbBloques.getSelectedIndex() == 0)
			datos = DatoDAO.consultarTodo(CONSULTA_CASONA);
		if (cbBloques.getSelectedIndex() == 1)
			datos = DatoDAO.consultarTodo(CONSULTA_CASONA_BLOQUE_1);
		if (cbBloques.getSelectedIndex() == 2)
			datos = DatoDAO.consultarTodo(CONSULTA_CASONA_BLOQUE_2);

		Object fila[] = new Object[6];

		for (int i = 0; i < datos.size(); i++) {

			fila[0] = datos.get(i).getId();
			fila[1] = datos.get(i).getEdificio();
			fila[2] = datos.get(i).getDepartamento();
			fila[3] = datos.get(i).getPropietario();
			fila[4] = datos.get(i).getTelefono();

			// Si el propietario es distinto a null, entonces...
			if (datos.get(i).getPropietario() != null) {
				// Si la columna "Propietario" no esta vacia, entonces...
				if (!datos.get(i).getPropietario().isEmpty())
					fila[5] = new JButton("...");
			}

			modelo.addRow(fila);

			fila[5] = null;
		}

		fila = null;
		liberarMemoria();

		cargarComboCasona();

	}

	// Consulta solo los departamentos dependiendo del bloque seleccionado
	private void cargarComboCasona() {

		// Limpia los datos del ArrayList antes de cargarle nuevos datos
		limpiarArrayList();

		// Limpia los items de cbDepartamentos antes de cargarle nuevos items
		encabezar_cbDepartamentos();

		// Define la sentencia dependiendo del bloque seleccionado
		if (cbBloques.getSelectedIndex() == 0)
			datos = DatoDAO.consultarDepartamentos(CONSULTA_CASONA_DEPARTAMENTOS);
		if (cbBloques.getSelectedIndex() == 1)
			datos = DatoDAO.consultarDepartamentos(CONSULTA_CASONA_DEPARTAMENTOS_BLOQUE_1);
		if (cbBloques.getSelectedIndex() == 2)
			datos = DatoDAO.consultarDepartamentos(CONSULTA_CASONA_DEPARTAMENTOS_BLOQUE_2);

		for (int i = 0; i < datos.size(); i++)
			cbDepartamentos.addItem(datos.get(i).getDepartamento());

	}

	private void consultarSolano() {

		limpiarTabla();

		datos = DatoDAO.consultarTodo(CONSULTA_SOLANO);

		Object fila[] = new Object[6];

		for (int i = 0; i < datos.size(); i++) {

			fila[0] = datos.get(i).getId();
			fila[1] = datos.get(i).getEdificio();
			fila[2] = datos.get(i).getDepartamento();
			fila[3] = datos.get(i).getPropietario();
			fila[4] = datos.get(i).getTelefono();

			// Si el propietario es distinto a null, entonces...
			if (datos.get(i).getPropietario() != null) {
				// Si la columna "Propietario" no esta vacia, entonces...
				if (!datos.get(i).getPropietario().isEmpty())
					fila[5] = new JButton("...");
			}

			modelo.addRow(fila);

			fila[5] = null;
		}

		fila = null;
		liberarMemoria();

		cargarComboSolano();

	}

	private void cargarComboSolano() {

		limpiarArrayList();

		encabezar_cbDepartamentos();

		datos = DatoDAO.consultarDepartamentos(CONSULTA_SOLANO_DEPARTAMENTOS);

		for (int i = 0; i < datos.size(); i++)
			cbDepartamentos.addItem(datos.get(i).getDepartamento());

	}

	/* Herramientas */

	private void limpiarTabla() {
		limpiarArrayList();
		limpiarFilas();
	}

	private void limpiarArrayList() {
		// Si hay datos en el ArrayList, entonces...
		if (!datos.isEmpty())
			// Elimina todos los datos del ArrayList
			datos.clear();

	}

	private void limpiarFilas() {
		// Si hay filas en la tabla, entonces...
		if (!isEmpty(tabla)) {
			// Itera hasta que no quede ninguna fila
			for (int i = modelo.getRowCount() - 1; i >= 0; i--)
				// Remueve la fila de la posicion i
				modelo.removeRow(i);
		}

	}

	// FIXME: Hace falta que reciba una tabla?
	private boolean isEmpty(JTable tabla) {
		if (tabla != null && tabla.getModel() != null)
			return tabla.getModel().getRowCount() <= 0 ? true : false;

		return false;
	}

	private void actualizarCombos(boolean estado_cbBloques, boolean estado_cbDepartamentos) {
		cbBloques.setEnabled(estado_cbBloques);
		cbDepartamentos.setEnabled(estado_cbDepartamentos);
	}

	private void encabezar_cbEdificios() {
		cbEdificios.setModel(new DefaultComboBoxModel<String>(ENCABEZADO_EDIFICIO));
	}

	private void encabezar_cbBloques() {
		cbBloques.setModel(new DefaultComboBoxModel<String>(ENCABEZADO_BLOQUE));
	}

	private void encabezar_cbDepartamentos() {
		cbDepartamentos.setModel(new DefaultComboBoxModel<String>(ENCABEZADO_DEPARTAMENTO));
	}

	private void liberarMemoria() {
		Runtime.getRuntime().gc();
	}

	private void cargarArchivo(int id, String edificio, String departamento, String propietario) {
		abrir(id, edificio, departamento, propietario);
	}

	private void abrir(int id, String edificio, String departamento, String propietario) {
		String rutaAbsoluta = System
				.getProperty("user.home") + "/Desktop/Planillas/" + propietario + " - " + edificio + " - " + departamento + ".xlsx";
		File file = new File(rutaAbsoluta);

		// Si la planilla existe, entonces...
		if (file.exists()) {
			try {
				// Abre la planilla
				Desktop.getDesktop().open(file); // o Runtime.getRuntime().exec("cmd /c start " + file);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// Si la planilla no existe, entonces...
		else
			crear(propietario);

	}

	private void crear(String propietario) {
		// Asigna la opcion elegida por el usuario en una variable entera
		int op = JOptionPane
				.showConfirmDialog(null, "La planilla no existe.\n�Desea crear una planilla excel para el propietario " + propietario + "?", "Excel", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		// Si el usuario elige la opcion "Si", entonces...
		if (JOptionPane.YES_OPTION == op) {
			try {
				// Abre el excel
				Runtime.getRuntime().exec("cmd /c start excel");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
