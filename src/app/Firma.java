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

    public Firma() {
        this.zamestnanci = new ArrayList<>();
    }
    public void ulozDoSql() {
        String url = "jdbc:sqlite:databaze.db";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
             
            stmt.execute("CREATE TABLE IF NOT EXISTS lide (id INTEGER PRIMARY KEY, typ TEXT, jmeno TEXT, prijmeni TEXT, rok INTEGER)");
            stmt.execute("DELETE FROM lide"); 
            
            String sql = "INSERT INTO lide(id, typ, jmeno, prijmeni, rok) VALUES(?,?,?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Zamestnanec z : zamestnanci) {
                    pstmt.setInt(1, z.getId());
                    pstmt.setString(2, (z instanceof DatovyAnalytik) ? "A" : "S");
                    pstmt.setString(3, z.getJmeno());
                    pstmt.setString(4, z.getPrijmeni());
                    pstmt.setInt(5, z.getRokarozeni());
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println("Chyba ukladani SQL: " + e.getMessage());
        }
    }

    public void nactiZSql() {
        String url = "jdbc:sqlite:databaze.db";
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
        } catch (Exception e) {
            System.out.println("SQL zaloha neni k dispozici nebo je prazdna.");
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
    
    public int getZamestnanecCount() {
        return zamestnanci.size();
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
            if (z.getId() == id) {
                return z;
            }
        }
        return null;
    }
    
    public List<Zamestnanec> getZamestnanci() {
        return zamestnanci;
    }
    
    public void ulozeni(String soubor) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(soubor))) {
            for (Zamestnanec z : zamestnanci) {
                String typ = (z instanceof DatovyAnalytik) ? "A" : "S";
                pw.println(typ + ";" + z.getId() + ";" + z.getJmeno() + ";" + z.getPrijmeni() + ";" + z.getRokarozeni());
            }
        } catch (IOException e) {
            System.out.println("Chyba při zápisu do souboru: " + e.getMessage());
        }
    }

    
    public void nacteni(String soubor) {
        try (Scanner s = new Scanner(new File(soubor))) {
            zamestnanci.clear();
            while (s.hasNextLine()) {
                String[] data = s.nextLine().split(";");
                int id = Integer.parseInt(data[1]);
                String jm = data[2];
                String pr = data[3];
                int rok = Integer.parseInt(data[4]);
                
                if (data[0].equals("A")) pridejZamestnance(new DatovyAnalytik(id, jm, pr, rok));
                else pridejZamestnance(new BezpecnostniSpecialista(id, jm, pr, rok));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Soubor nenalezen, začínáme s prázdnou databází.");
        }
    }
}