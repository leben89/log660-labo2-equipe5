package ca.ets.log660.labo2.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Employe {
    //private final PreparedStatement stmt;
//
    //public EmployeDAO(Connection conn) throws SQLException {
    //this.stmt = conn.prepareStatement("INSERT INTO Employe (id_employe, matricule, telephone, mot_de_passe, courriel, address_id) VALUES (?, ?, ?, ?, ?, ?)");
    //}
//
    //public void ajouter(int idEmploye, long matricule, String telephone, String motDePasse, String courriel, int addressId) throws SQLException {
    //    this.stmt.setInt(1, idEmploye);
    //    this.stmt.setLong(2, matricule);
    //    this.stmt.setString(3, telephone);
    //    this.stmt.setString(4, motDePasse);
    //    this.stmt.setString(5, courriel);
    //    this.stmt.setInt(6, addressId);
    //    this.stmt.addBatch();
    //}
//
    //public void flush() throws SQLException {
    //    this.stmt.executeBatch();
    //}
//
    //public void close() throws SQLException {
    //    if (this.stmt != null) {
    //    this.stmt.close();
    //    }
    //}
}
