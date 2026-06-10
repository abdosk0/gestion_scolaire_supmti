package models;

import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursDAO {

    public boolean create(Cours c) {
        String sql = "INSERT INTO cours (code, intitule, description, coefficient) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getIntitule());
            ps.setString(3, c.getDescription());
            ps.setDouble(4, c.getCoefficient());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[CoursDAO.create] " + ex.getMessage());
            return false;
        }
    }

    public List<Cours> findAll() {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT * FROM cours ORDER BY code";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.err.println("[CoursDAO.findAll] " + ex.getMessage());
        }
        return liste;
    }

    public List<Cours> search(String motCle) {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT * FROM cours WHERE LOWER(code) LIKE ? OR LOWER(intitule) LIKE ? ORDER BY code";
        String pattern = "%" + motCle.toLowerCase() + "%";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.err.println("[CoursDAO.search] " + ex.getMessage());
        }
        return liste;
    }

    public boolean update(Cours c) {
        String sql = "UPDATE cours SET code=?, intitule=?, description=?, coefficient=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getIntitule());
            ps.setString(3, c.getDescription());
            ps.setDouble(4, c.getCoefficient());
            ps.setInt(5, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[CoursDAO.update] " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM cours WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[CoursDAO.delete] " + ex.getMessage());
            return false;
        }
    }

    private Cours mapRow(ResultSet rs) throws SQLException {
        return new Cours(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("intitule"),
                rs.getString("description"),
                rs.getDouble("coefficient")
        );
    }
}
