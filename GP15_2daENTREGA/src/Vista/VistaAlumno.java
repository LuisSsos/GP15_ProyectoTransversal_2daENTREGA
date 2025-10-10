package Vista;

import Modelo.Alumno;
import Persistencia.alumnoData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * @author Grupo 15 Luis Ezequiel Sosa Lucas Saidman Luca Rodrigaño Ignacio
 * Rodriguez
*
 */
public class VistaAlumno extends javax.swing.JInternalFrame {


    private final alumnoData dao = new alumnoData(); 
    private final DefaultTableModel modelo;
    private Alumno seleccionadoOriginal = null; 


    public VistaAlumno() {
        initComponents(); 


        modelo = new DefaultTableModel(
                new Object[]{"DNI", "Apellido", "Nombre", "Fecha de Nacimiento", "Regular"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla_alumnos.setModel(modelo);


        cb_regular.setModel(new DefaultComboBoxModel<>(new String[]{"Sí", "No"}));
        txt_fc.setEditable(true); 
        txt_fc.setToolTipText("Formato: yyyy-MM-dd");


        recargarTabla();
        
        actualizarHabilitacionBotones();
    }

    private void recargarTabla() {
        try {
            List<Alumno> datos = dao.listarTodos();
            cargarTabla(datos);
        } catch (SQLException ex) {
            error(ex);
        }
    }

    private void cargarTabla(List<Alumno> datos) {
        limpiarTabla();
        for (Alumno a : datos) {
            modelo.addRow(new Object[]{
                String.valueOf(a.getDni()),
                a.getApellido(),
                (a.getNombre() != null ? a.getNombre() : ""),
                (a.getFechaNacimiento() != null ? a.getFechaNacimiento().toString() : ""),
                (a.isRegular() ? "Sí" : "No")
            });
        }
    }

    private void limpiarTabla() {
        modelo.setRowCount(0);
    }


    private void msg(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    private void error(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }


    private void limpiarFormulario() {
        txt_dni.setText("");
        txt_apellido.setText("");
        txt_nombre1.setText("");
        txt_fc.setText("");
        cb_regular.setSelectedIndex(0);
        tabla_alumnos.clearSelection();
        actualizarHabilitacionBotones();
    }



    private void actualizarHabilitacionBotones() {
        boolean dniIngresado = !txt_dni.getText().trim().isEmpty();
        boolean haySeleccion = tabla_alumnos.getSelectedRow() >= 0 && seleccionadoOriginal != null;

        btn_nuevo.setEnabled(true);

        // Buscar
        btn_buscar.setEnabled(dniIngresado);

        // Guardar
        boolean puedeGuardar = !txt_dni.getText().trim().isEmpty()
                && !txt_apellido.getText().trim().isEmpty()
                && !txt_nombre1.getText().trim().isEmpty()
                && !txt_fc.getText().trim().isEmpty()
                && cb_regular.getSelectedItem() != null;
        btn_guardar.setEnabled(puedeGuardar);

        // Actualizar y eliminar 
        btn_actualizar.setEnabled(haySeleccion);
        btn_eliminar.setEnabled(haySeleccion);
    }

    // Parsear ya que trabajamos con String en tabla
    private Integer parseEntero(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseFecha(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(s);
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }

    private Boolean getRegularFromCombo() {
        Object sel = cb_regular.getSelectedItem();
        if (sel == null) {
            return null;
        }
        String v = sel.toString().trim();
        if (v.equalsIgnoreCase("Si")) {
            return true;
        }
        if (v.equalsIgnoreCase("No")) {
            return false;
        }
        return null;
    }


    private void onGuardar() {

        Integer dni = parseEntero(txt_dni.getText());
        if (dni == null) {
            msg("DNI invalido");
            txt_dni.requestFocus();
            return;
        }

        String ape = txt_apellido.getText().trim();
        if (ape.isEmpty()) {
            msg("Complete Apellido");
            txt_apellido.requestFocus();
            return;
        }

        String nom = txt_nombre1.getText().trim();
        if (nom.isEmpty()) {
            msg("Complete Nombre");
            txt_nombre1.requestFocus();
            return;
        }

        LocalDate fn = parseFecha(txt_fc.getText());
        if (fn == null) {
            msg("formato de fechha yyyy-MM-dd");
            txt_fc.requestFocus();
            txt_fc.selectAll();
            return;
        }

        Boolean reg = getRegularFromCombo();
        if (reg == null) {
            msg(" Regular Si o No.");
            cb_regular.requestFocus();
            return;
        }

        try {
            // DNI duplicado
            if (dao.buscarPorDni(dni) != null) {
                msg("Ya existe un alumno con ese DNI");
                return;
            }

            Alumno a = new Alumno(dni, ape, nom, fn, reg);
            dao.guardar(a);

            msg("Alumno guardado con su ID : " + a.getIdAlumno());
            recargarTabla();
            actualizarHabilitacionBotones();
            limpiarFormulario();
            txt_dni.requestFocus();

        } catch (Exception ex) {
            error(ex);
        }
    }

    // Buscar por DNI 
    private void onBuscarPorDni() {
        Integer dni = parseEntero(txt_dni.getText());
        if (dni == null) {
            msg("Ingrese un DNI numérico.");
            txt_dni.requestFocus();
            return;
        }

        try {
            Alumno a = dao.buscarPorDni(dni);
            if (a == null) {
                msg("No existe alumno con ese DNI.");
                return;
            }

            // Mostrar datos del alumno encontrado
            txt_dni.setText(String.valueOf(a.getDni()));
            txt_apellido.setText(a.getApellido());
            txt_nombre1.setText(a.getNombre());
            txt_fc.setText(a.getFechaNacimiento().toString());
            cb_regular.setSelectedItem(a.isRegular() ? "Sí" : "No");

            seleccionadoOriginal = a; // guardamos el alumno seleccionado

            // Buscar y seleccionar en la tabla
            seleccionarFilaPorDni(dni);

        } catch (Exception ex) {
            error(ex);
        }
    }

// Eliminar 
    private void onEliminar() {
        if (seleccionadoOriginal == null) {
            msg("Seleccione un alumno de la tabla para eliminar.");
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Eliminar definitivamente al alumno DNI " + seleccionadoOriginal.getDni() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (conf != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.borrar(seleccionadoOriginal.getIdAlumno());
            msg("Alumno eliminado correctamente.");
            recargarTabla();
            limpiarFormulario();
            seleccionadoOriginal = null;
        } catch (Exception ex) {
            error(ex);
        }
    }

//Actualizar Regularidad 
    private void onActualizarRegular() {
        if (seleccionadoOriginal == null) {
            msg("Seleccione un alumno para actualizar su estado de regularidad.");
            return;
        }

        Boolean regForm = getRegularFromCombo();
        if (regForm == null) {
            msg("Seleccione Regular: Sí o No.");
            cb_regular.requestFocus();
            return;
        }

        try {
            if (regForm) {
                dao.altaLogica(seleccionadoOriginal.getIdAlumno());
            } else {
                dao.bajaLogica(seleccionadoOriginal.getIdAlumno());
            }

            msg("Estado de regularidad actualizado.");
            recargarTabla();
            seleccionarFilaPorDni(seleccionadoOriginal.getDni());
        } catch (Exception ex) {
            error(ex);
        }
    }

    private void seleccionarFilaPorDni(int dni) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            Object val = modelo.getValueAt(i, 0);
            if (val != null && String.valueOf(dni).equals(val.toString())) {
                tabla_alumnos.setRowSelectionInterval(i, i);
                tabla_alumnos.scrollRectToVisible(tabla_alumnos.getCellRect(i, 0, true));
                break;
            }
        }
    }

    //  fecha 
    private String getFechaComboText() {
        return txt_fc.getText();

    }

    private void setFechaComboText(String text) {
        txt_fc.setText(text == null ? "" : text);
    }

    /**
     * Creates new form VistaAlumno
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_gestion_alumnos = new javax.swing.JPanel();
        lb_titulo = new javax.swing.JLabel();
        sp_tabla_alumnos = new javax.swing.JScrollPane();
        tabla_alumnos = new javax.swing.JTable();
        lb_dni = new javax.swing.JLabel();
        lb_nombre = new javax.swing.JLabel();
        lb_regular = new javax.swing.JLabel();
        lb_fc = new javax.swing.JLabel();
        lb_apellido = new javax.swing.JLabel();
        txt_dni = new javax.swing.JTextField();
        txt_apellido = new javax.swing.JTextField();
        txt_fc = new javax.swing.JTextField();
        btn_nuevo = new javax.swing.JButton();
        btn_guardar = new javax.swing.JButton();
        btn_actualizar = new javax.swing.JButton();
        btn_eliminar = new javax.swing.JButton();
        btn_buscar = new javax.swing.JButton();
        cb_regular = new javax.swing.JComboBox<>();
        txt_nombre1 = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(1000, 700));

        lb_titulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lb_titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_titulo.setText("Gestion de Alumnos");

        tabla_alumnos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabla_alumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "DNI", "Apellido", "Nombre", "Fecha de Nacimiento", "Regular"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_alumnos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_alumnosMouseClicked(evt);
            }
        });
        sp_tabla_alumnos.setViewportView(tabla_alumnos);

        lb_dni.setText("DNI:");

        lb_nombre.setText("Nombre:");

        lb_regular.setText("Regular:");

        lb_fc.setText("Fecha de Nacimiento:");

        lb_apellido.setText("Apellido:");

        txt_dni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_dniActionPerformed(evt);
            }
        });

        txt_apellido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_apellidoActionPerformed(evt);
            }
        });

        txt_fc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_fcActionPerformed(evt);
            }
        });

        btn_nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/escoba.png"))); // NOI18N
        btn_nuevo.setText("Nuevo");
        btn_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_nuevoActionPerformed(evt);
            }
        });

        btn_guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/guardar.png"))); // NOI18N
        btn_guardar.setText("Guardar");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });

        btn_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-aprobar-y-actualizar-48.png"))); // NOI18N
        btn_actualizar.setText("Actualizar");
        btn_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actualizarActionPerformed(evt);
            }
        });

        btn_eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/eliminar.png"))); // NOI18N
        btn_eliminar.setText("Eliminar");
        btn_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarActionPerformed(evt);
            }
        });

        btn_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-magnifying-glass-tilted-right-48.png"))); // NOI18N
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        cb_regular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_regularActionPerformed(evt);
            }
        });

        txt_nombre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nombre1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_gestion_alumnosLayout = new javax.swing.GroupLayout(pnl_gestion_alumnos);
        pnl_gestion_alumnos.setLayout(pnl_gestion_alumnosLayout);
        pnl_gestion_alumnosLayout.setHorizontalGroup(
            pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lb_titulo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnl_gestion_alumnosLayout.createSequentialGroup()
                .addGap(196, 196, 196)
                .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_gestion_alumnosLayout.createSequentialGroup()
                        .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lb_regular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_fc, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_fc, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cb_regular, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnl_gestion_alumnosLayout.createSequentialGroup()
                            .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lb_apellido, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                .addComponent(lb_dni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txt_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_dni, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pnl_gestion_alumnosLayout.createSequentialGroup()
                            .addComponent(btn_nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btn_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btn_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btn_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_gestion_alumnosLayout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(sp_tabla_alumnos, javax.swing.GroupLayout.PREFERRED_SIZE, 946, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        pnl_gestion_alumnosLayout.setVerticalGroup(
            pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_gestion_alumnosLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lb_titulo)
                .addGap(30, 30, 30)
                .addComponent(sp_tabla_alumnos, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_gestion_alumnosLayout.createSequentialGroup()
                        .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_dni, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_dni, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btn_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lb_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lb_fc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_fc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lb_regular, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_regular, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addGroup(pnl_gestion_alumnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_gestion_alumnos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_gestion_alumnos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed

        onGuardar();
    }//GEN-LAST:event_btn_guardarActionPerformed

    private void btn_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_nuevoActionPerformed
        limpiarFormulario();
        txt_dni.requestFocus();
    }//GEN-LAST:event_btn_nuevoActionPerformed

    private void btn_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salirActionPerformed
        dispose();
    }//GEN-LAST:event_btn_salirActionPerformed

    private void cb_regularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_regularActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb_regularActionPerformed

    private void txt_dniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_dniActionPerformed
        actualizarHabilitacionBotones();
    }//GEN-LAST:event_txt_dniActionPerformed

    private void txt_apellidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_apellidoActionPerformed
        actualizarHabilitacionBotones();
    }//GEN-LAST:event_txt_apellidoActionPerformed

    private void txt_nombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nombre1ActionPerformed
        actualizarHabilitacionBotones();
    }//GEN-LAST:event_txt_nombre1ActionPerformed

    private void txt_fcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_fcActionPerformed
        actualizarHabilitacionBotones();
    }//GEN-LAST:event_txt_fcActionPerformed

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        onBuscarPorDni();
        actualizarHabilitacionBotones();
    }//GEN-LAST:event_btn_buscarActionPerformed

    private void btn_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarActionPerformed
        onEliminar();
        actualizarHabilitacionBotones();
    }//GEN-LAST:event_btn_eliminarActionPerformed

    private void btn_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizarActionPerformed
        onActualizarRegular();
        actualizarHabilitacionBotones();
    }//GEN-LAST:event_btn_actualizarActionPerformed

    private void tabla_alumnosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_alumnosMouseClicked
        
        int fila = tabla_alumnos.getSelectedRow();
        if (fila < 0) {
            return;
        }

        String sDni = String.valueOf(modelo.getValueAt(fila, 0));
        Integer dni = parseEntero(sDni);
        if (dni == null) {
            return;
        }

        try {
            Alumno a = dao.buscarPorDni(dni);
            if (a != null) {
                txt_dni.setText(String.valueOf(a.getDni()));
                txt_apellido.setText(a.getApellido());
                txt_nombre1.setText(a.getNombre());
                txt_fc.setText(a.getFechaNacimiento().toString());
                cb_regular.setSelectedItem(a.isRegular() ? "Si" : "No");
                seleccionadoOriginal = a;
            }
        } catch (SQLException ex) {
            error(ex);
        }
        
        actualizarHabilitacionBotones();
    }//GEN-LAST:event_tabla_alumnosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_actualizar;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_eliminar;
    private javax.swing.JButton btn_guardar;
    private javax.swing.JButton btn_nuevo;
    private javax.swing.JComboBox<String> cb_regular;
    private javax.swing.JLabel lb_apellido;
    private javax.swing.JLabel lb_dni;
    private javax.swing.JLabel lb_fc;
    private javax.swing.JLabel lb_nombre;
    private javax.swing.JLabel lb_regular;
    private javax.swing.JLabel lb_titulo;
    private javax.swing.JPanel pnl_gestion_alumnos;
    private javax.swing.JScrollPane sp_tabla_alumnos;
    private javax.swing.JTable tabla_alumnos;
    private javax.swing.JTextField txt_apellido;
    private javax.swing.JTextField txt_dni;
    private javax.swing.JTextField txt_fc;
    private javax.swing.JTextField txt_nombre1;
    // End of variables declaration//GEN-END:variables
}
