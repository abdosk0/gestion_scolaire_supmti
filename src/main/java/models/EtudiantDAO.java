package models;

import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {

    public boolean create(Etudiant e) {
        String sql = "INSERT INTO etudiants (nom, prenom, email, telephone, date_naissance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getTelephone());
            ps.setDate(5, e.getDateNaissance() != null ? Date.valueOf(e.getDateNaissance()) : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EtudiantDAO.create] " + ex.getMessage());
            return false;
        }
    }

    public List<Etudiant> findAll() {
        List<Etudiant> liste = new ArrayList<>();
        String sql = "SELECT * FROM etudiants ORDER BY nom, prenom";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.err.println("[EtudiantDAO.findAll] " + ex.getMessage());
        }
        return liste;
    }

    public List<Etudiant> search(String motCle) {
        List<Etudiant> liste = new ArrayList<>();
        String sql = "SELECT * FROM etudiants WHERE LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR LOWER(email) LIKE ? ORDER BY nom, prenom";
        String pattern = "%" + motCle.toLowerCase() + "%";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.err.println("[EtudiantDAO.search] " + ex.getMessage());
        }
        return liste;
    }

    public boolean update(Etudiant e) {
        String sql = "UPDATE etudiants SET nom=?, prenom=?, email=?, telephone=?, date_naissance=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getTelephone());
            ps.setDate(5, e.getDateNaissance() != null ? Date.valueOf(e.getDateNaissance()) : null);
            ps.setInt(6, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EtudiantDAO.update] " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM etudiants WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EtudiantDAO.delete] " + ex.getMessage());
            return false;
        }
    }

    private Etudiant mapRow(ResultSet rs) throws SQLException {
        Date d = rs.getDate("date_naissance");
        return new Etudiant(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("telephone"),
                d != null ? d.toLocalDate() : null
        );
    }
}
