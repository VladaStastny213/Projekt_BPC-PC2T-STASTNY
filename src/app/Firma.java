package app;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Firma {
    private List<Zamestnanec> zamestnanci;
    private final String url = "jdbc:sqlite:databaze.db";

    public Firma() {
        this.zamestnanci = new ArrayList<>();
    }

    public void ulozDoSql() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
          
            stmt.execute("CREATE TABLE IF NOT EXISTS lide (id INTEGER PRIMARY KEY, typ TEXT, jmeno TEXT, prijmeni TEXT, rok INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS vazby (id1 INTEGER, id2 INTEGER, uroven INTEGER)");
            
 
            stmt.execute("DELETE FROM lide");
            stmt.execute("DELETE FROM vazby");
            
 
            String sqlLide = "INSERT INTO lide(id, typ, jmeno, prijmeni, rok) VALUES(?,?,?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlLide)) {
                for (Zamestnanec z : zamestnanci) {
                    pstmt.setInt(1, z.getId());
                    pstmt.setString(2, (z instanceof DatovyAnalytik) ? "A" : "S");
                    pstmt.setString(3, z.getJmeno());
                    pstmt.setString(4, z.getPrijmeni());
                    pstmt.setInt(5, z.getRokarozeni());
                    pstmt.executeUpdate();
                }
            }


            String sqlVazby = "INSERT INTO vazby(id1, id2, uroven) VALUES(?,?,?)";
            try (PreparedStatement pstmtV = conn.prepareStatement(sqlVazby)) {
                for (Zamestnanec z : zamestnanci) {
                    for (var entry : z.getSpolupracovnici().entrySet()) {
                
                        if (z.getId() < entry.getKey()) {
                            pstmtV.setInt(1, z.getId());
                            pstmtV.setInt(2, entry.getKey());
                            pstmtV.setInt(3, entry.getValue());
                            pstmtV.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Chyba ukladani SQL: " + e.getMessage());
        }
    }

    public void nactiZSql() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            

            stmt.execute("CREATE TABLE IF NOT EXISTS lide (id INTEGER PRIMARY KEY, typ TEXT, jmeno TEXT, prijmeni TEXT, rok INTEGER)");
            ResultSet rs = stmt.executeQuery("SELECT * FROM lide");
            
            zamestnanci.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String typ = rs.getString("typ");
                String jm = rs.getString("jmeno");
                String pr = rs.getString("prijmeni");
                int rok = rs.getInt("rok");
                
                if (typ.equals("A")) pridejZamestnance(new DatovyAnalytik(id, jm, pr, rok));
                else pridejZamestnance(new BezpecnostniSpecialista(id, jm, pr, rok));
            }


            stmt.execute("CREATE TABLE IF NOT EXISTS vazby (id1 INTEGER, id2 INTEGER, uroven INTEGER)");
            ResultSet rsV = stmt.executeQuery("SELECT * FROM vazby");
            while (rsV.next()) {
                pridejVazbu(rsV.getInt("id1"), rsV.getInt("id2"), rsV.getInt("uroven"));
            }

        } catch (Exception e) {
            System.out.println("SQL data nejsou k dispozici.");
        }
    }

    public boolean pridejVazbu(int id1, int id2, int uroven) {
        Zamestnanec z1 = najdiZamestnance(id1);
        Zamestnanec z2 = najdiZamestnance(id2);
        
        if (z1 != null && z2 != null && id1 != id2) {
            z1.pridejSpolupracovnika(id2, uroven);
            z2.pridejSpolupracovnika(id1, uroven);
            return true;
        }
        return false;
    }

    public void pridejZamestnance(Zamestnanec z) {
        zamestnanci.add(z);
    }

    public boolean smazZamestnance(int id) {
        for (Zamestnanec z : zamestnanci) {
            z.getSpolupracovnici().remove(id);
        }
        return zamestnanci.removeIf(z -> z.getId() == id);
    }

    public Zamestnanec najdiZamestnance(int id) {
        for (Zamestnanec z : zamestnanci) {
            if (z.getId() == id) return z;
        }
        return null;
    }
    
    public int getZamestnanecCount() {
        return zamestnanci.size();
    }

    public List<Zamestnanec> getZamestnanci() {
        return zamestnanci;
    }
}