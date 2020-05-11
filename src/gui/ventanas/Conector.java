package gui.ventanas;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import conector.Conexion;

public class Conector extends JDialog implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;

	private JLabel lblServidor;
	private JTextField txtIP;
	private JButton btnConectar;

	private static Conector instance = null;

	public static Conector getInstance() {
		if (instance == null)
			instance = new Conector();
		return instance;
	}

	private Conector() {
		createGUI();
		setFrameProperties();
	}

	private void createGUI() {

		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		lblServidor = new JLabel("Servidor:");
		getContentPane().add(lblServidor);

		txtIP = new JTextField("192.168.1.22");
		txtIP.setColumns(15);
		txtIP.selectAll();
		txtIP.addKeyListener(this);
		getContentPane().add(txtIP);

		btnConectar = new JButton("Conectar");
		btnConectar.setFocusable(false);
		btnConectar.addActionListener(this);
		getContentPane().add(btnConectar);

		pack();

	}

	private void setFrameProperties() {
		setTitle("Conector");
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("img/Logo1_64x58.png")).getImage());
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == btnConectar)
			conectar();
	}

	private void conectar() {

		// Le paso la IP. En este caso como la conexion es local, se debe usar la IP como localhost, 127.0.0.1, IP Privada (IPv4) o el nombre de la maquina.
		Conexion.servidor = txtIP.getText();

		// Si hay una conexion, entonces...
		if (Conexion.getConnection() != null) {
			GestionarDepartamentos.getInstance().setVisible(true);
			Conector.getInstance().dispose();
		}

	}

	@Override
	public void keyPressed(KeyEvent evt) {

	}

	@Override
	public void keyReleased(KeyEvent evt) {
		if (evt.getSource() == txtIP)
			habilitarBtn(txtIP, btnConectar);
	}

	@Override
	public void keyTyped(KeyEvent evt) {

	}

	private void habilitarBtn(JTextField txtIP, JButton btnConectar) {
		if (txtIP != null) {
			if (!txtIP.getText().isEmpty())
				btnConectar.setEnabled(true);
			else
				btnConectar.setEnabled(false);
		}
	}
}
