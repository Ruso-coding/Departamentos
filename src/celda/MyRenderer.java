package celda;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

// Clase encargada de dibujar los componentes en las celdas del JTable
public class MyRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable tabla, Object value, boolean isSelected, boolean hasFocus, int fila, int columna) {

		/* Observen que todo lo que hacemos en éste método es retornar el objeto que se va a dibujar en la
		 * celda. Esto significa que se dibujará en la celda el objeto que devuelva el TableModel. También
		 * significa que este renderer nos permitiría dibujar cualquier objeto gráfico en la grilla, pues
		 * retorna el objeto tal y como lo recibe. */

		/* Convierte el valor de la celda seleccionada (fila x y columna 5) que es un objeto (Object)
		 * a Component (JPanel, JLabel, JButton, etc.) y luego lo devuelve.
		 * Podría ser el caso de convertirlo solo a JButton en donde aceptaría solo botones, pero no sería lógico, ya que el metodo
		 * devuelve un Component. */
		// return (Component) tabla.getValueAt(fila, 5);

		/* Esta línea es más resumida que la anterior, ya que no hace falta especificar de que fila y de que columna proviene ese valor,
		 * porque esto mismo se especifica con anterioridad en el modelo de la tabla, en donde la columna 5 ("Excel") es la unica
		 * que acepta componentes en sus celdas.
		 * Aunque es recomendable indicar cual es la fila y la columna de la celda que se va a dibujar. */
		return (Component) value; // Retorna el valor de la celda convertido a componente

	}

}
