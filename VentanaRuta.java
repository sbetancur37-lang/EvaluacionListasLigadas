import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class VentanaRuta extends JFrame {
    private ListaLigadaParadas listaRuta;
    private DefaultListModel<String> modeloListaUI;
    private JList<String> listParadas;

    private JTextField txtNombre, txtLatitud, txtLongitud, txtTiempo;
    private JTextArea txtDescripcion;

    public VentanaRuta() {
        listaRuta = new ListaLigadaParadas();
        setTitle("Gestión de Rutas de Transporte - IUE");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Detalles de la Parada"));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Latitud:"));
        txtLatitud = new JTextField();
        panelFormulario.add(txtLatitud);

        panelFormulario.add(new JLabel("Longitud:"));
        txtLongitud = new JTextField();
        panelFormulario.add(txtLongitud);

        panelFormulario.add(new JLabel("Tiempo Estimado:"));
        txtTiempo = new JTextField();
        panelFormulario.add(txtTiempo);

        panelFormulario.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextArea(3, 20);
        panelFormulario.add(new JScrollPane(txtDescripcion));

        add(panelFormulario, BorderLayout.WEST);

        modeloListaUI = new DefaultListModel<>();
        listParadas = new JList<>(modeloListaUI);
        listParadas.addListSelectionListener(e -> cargarDatosSeleccionados());
        add(new JScrollPane(listParadas), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("Agregar Parada");
        JButton btnModificar = new JButton("Modificar Parada");
        JButton btnEliminar = new JButton("Eliminar Parada");
        JButton btnDistancia = new JButton("Calcular Distancia");
        JButton btnGuardar = new JButton("Guardar Ruta (JSON)");
        JButton btnCargar = new JButton("Cargar Ruta (JSON)");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnDistancia);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCargar);

        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(this::accionAgregar);
        btnModificar.addActionListener(this::accionModificar);
        btnEliminar.addActionListener(this::accionEliminar);
        btnDistancia.addActionListener(e -> accionCalcularDistancia());
        btnGuardar.addActionListener(e -> accionGuardar());
        btnCargar.addActionListener(e -> accionCargar());
    }

    private void actualizarVistaLista() {
        modeloListaUI.clear();
        String[] nombres = listaRuta.obtenerNombresParadas();
        for (String n : nombres) {
            modeloListaUI.addElement(n);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
        txtTiempo.setText("");
        txtDescripcion.setText("");
    }

    private void accionAgregar(ActionEvent e) {
        try {
            String nombre = txtNombre.getText();
            double lat = Double.parseDouble(txtLatitud.getText());
            double lon = Double.parseDouble(txtLongitud.getText());
            String tiempo = txtTiempo.getText();
            String desc = txtDescripcion.getText();

            listaRuta.agregarParada(nombre, lat, lon, tiempo, desc);
            actualizarVistaLista();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Parada agregada con éxito.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese valores numéricos válidos para latitud y longitud.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosSeleccionados() {
        int index = listParadas.getSelectedIndex();
        if (index >= 0) {
            Parada p = listaRuta.obtenerParada(index);
            if (p != null) {
                txtNombre.setText(p.getNombre());
                txtLatitud.setText(String.valueOf(p.getLatitud()));
                txtLongitud.setText(String.valueOf(p.getLongitud()));
                txtTiempo.setText(p.getTiempoEstimado());
                txtDescripcion.setText(p.getDescripcion());
            }
        }
    }

    private void accionModificar(ActionEvent e) {
        int index = listParadas.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una parada de la lista para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String nombre = txtNombre.getText();
            double lat = Double.parseDouble(txtLatitud.getText());
            double lon = Double.parseDouble(txtLongitud.getText());
            String tiempo = txtTiempo.getText();
            String desc = txtDescripcion.getText();

            if (listaRuta.modificarParada(index, nombre, lat, lon, tiempo, desc)) {
                actualizarVistaLista();
                JOptionPane.showMessageDialog(this, "Parada modificada con éxito.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique los campos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminar(ActionEvent e) {
        int index = listParadas.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una parada para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (listaRuta.eliminarParada(index)) {
            actualizarVistaLista();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Parada eliminada correctamente.");
        }
    }

    private void accionCalcularDistancia() {
        double distancia = listaRuta.calcularDistanciaTotal();
        JOptionPane.showMessageDialog(this, String.format("La distancia total aproximada de la ruta es: %.2f km", distancia), "Cálculo de Distancia", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionGuardar() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            if (!ruta.endsWith(".json")) ruta += ".json";
            try {
                listaRuta.guardarRutaJSON(ruta);
                JOptionPane.showMessageDialog(this, "Ruta guardada exitosamente en formato JSON.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionCargar() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                listaRuta.cargarRutaJSON(archivo.getAbsolutePath());
                actualizarVistaLista();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Ruta cargada exitosamente desde el archivo JSON.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaRuta().setVisible(true);
        });
    }
}