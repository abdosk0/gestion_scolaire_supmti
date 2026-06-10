package models;

import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    // requête de base avec jointure pour récupérer les noms
    private static final String SELECT_FULL =
            "SELECT n.id, n.etudiant_id, n.cours_id, n.note, n.date_evaluation, " +
            "e.nom || ' ' || e.prenom AS etudiant_nom, c.code AS cours_code, c.intitule AS cours_intitule " +
            "FROM notes n JOIN etudiants e ON e.id = n.etudiant_id JOIN cours c ON c.id = n.cours_id ";

    // INSERT ou UPDATE si le couple (etudiant, cours) existe déjà
    public boolean upsert(int etudiantId, int coursId, double note) {
        String sql = "INSERT INTO notes (etudiant_id, cours_id, note, date_evaluation) VALUES (?, ?, ?, CURRENT_DATE) " +
                     "ON CONFLICT (etudiant_id, cours_id) DO UPDATE SET note=EXCLUDED.note, date_evaluation=CURRENT_DATE";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ps.setInt(2, coursId);
            ps.setDouble(3, note);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[NoteDAO.upsert] " + ex.getMessage());
            return false;
        }
    }

    public List<Note> findAll() {
        List<Note> liste = new ArrayList<>();
        String sql = SELECT_FULL + "ORDER BY e.nom, c.code";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapRow(rs));
        } catch (SQLException ex) {
            System.err.println("[NoteDAO.findAll] " + ex.getMessage());
        }
        return liste;
    }

    public List<Note> findByEtudiant(int etudiantId) {
        List<Note> liste = new ArrayList<>();
        String sql = SELECT_FULL + "WHERE n.etudiant_id = ? ORDER BY c.code";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapRow(rs));
        } catch (SQLException ex) {
            System.err.println("[NoteDAO.findByEtudiant] " + ex.getMessage());
        }
        return liste;
    }

    // retourne -1 si l'étudiant n'a pas encore de notes
    public double getMoyenneEtudiant(int etudiantId) {
        String sql = "SELECT SUM(n.note * c.coefficient) / SUM(c.coefficient) AS moyenne " +
                     "FROM notes n JOIN cours c ON c.id = n.cours_id WHERE n.etudiant_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getObject("moyenne") != null) {
                return rs.getDouble("moyenne");
            }
        } catch (SQLException ex) {
            System.err.println("[NoteDAO.getMoyenne] " + ex.getMessage());
        }
        return -1;
    }

    public boolean update(int id, double note) {
        String sql = "UPDATE notes SET note=?, date_evaluation=CURRENT_DATE WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setDouble(1, note);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[NoteDAO.update] " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM notes WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[NoteDAO.delete] " + ex.getMessage());
            return false;
        }
    }

    private Note mapRow(ResultSet rs) throws SQLException {
        Date d = rs.getDate("date_evaluation");
        return new Note(
                rs.getInt("id"),
                rs.getInt("etudiant_id"),
                rs.getInt("cours_id"),
                rs.getDouble("note"),
                d != null ? d.toLocalDate() : null,
                rs.getString("etudiant_nom"),
                rs.getString("cours_code"),
                rs.getString("cours_intitule")
        );
    }
}
