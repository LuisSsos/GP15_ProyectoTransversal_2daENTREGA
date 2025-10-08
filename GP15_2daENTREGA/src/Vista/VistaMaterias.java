package Vista;

import javax.swing.JOptionPane;

public class VistaMaterias extends javax.swing.JInternalFrame {

    public VistaMaterias() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txCodigo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtMateria = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txAnio = new javax.swing.JTextField();
        bttGuardar = new javax.swing.JButton();
        bttBorrar = new javax.swing.JButton();
        bttSalir = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setTitle("Vista Materias");

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel1.setText("Formulario de materias");

        jLabel2.setText("Codigo de materia");

        txCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txCodigoActionPerformed(evt);
            }
        });

        jLabel3.setText("Nombre de la materia");

        jLabel4.setText("Año al que pertenece");

        bttGuardar.setText("Guardar");
        bttGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttGuardarActionPerformed(evt);
            }
        });

        bttBorrar.setText("Nuevo");
        bttBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttBorrarActionPerformed(evt);
            }
        });

        bttSalir.setText("Salir");
        bttSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel2)
                                            .addGap(46, 46, 46))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addGap(29, 29, 29)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(32, 32, 32)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txCodigo)
                                    .addComponent(txtMateria)
                                    .addComponent(txAnio, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(163, Short.MAX_VALUE)
                        .addComponent(bttGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bttBorrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bttSalir)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtMateria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bttGuardar)
                    .addComponent(bttBorrar)
                    .addComponent(bttSalir)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bttSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_bttSalirActionPerformed

    private void bttGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttGuardarActionPerformed
        try {
            int codigo = Integer.parseInt(txCodigo.getText());

            String nombre = txtMateria.getText();
            int anio = Integer.parseInt(txAnio.getText());

            Materia sc = new Materia(codigo, nombre, anio);
            MenuPrincipal.materias.add(sc);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(rootPane, "Por favor.Ingrese un numero");
        }
    }//GEN-LAST:event_bttGuardarActionPerformed


    private void bttBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttBorrarActionPerformed
        txCodigo.setText("");
        txtMateria.setText("");
        txAnio.setText("");
    }//GEN-LAST:event_bttBorrarActionPerformed

    private void txCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txCodigoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bttBorrar;
    private javax.swing.JButton bttGuardar;
    private javax.swing.JButton bttSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField txAnio;
    private javax.swing.JTextField txCodigo;
    private javax.swing.JTextField txtMateria;
    // End of variables declaration//GEN-END:variables
}
