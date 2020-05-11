package vo;

// Value Object - Planilla para crear nuevos datos
public class DatoVO {

	private int id;
	private String edificio;
	private String departamento;
	private String propietario;
	private String telefono;

	/* FIXME: Porque no tiene constructor? */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEdificio() {
		return edificio;
	}

	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getPropietario() {
		return propietario;
	}

	public void setPropietario(String propietario) {
		this.propietario = propietario;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

}
