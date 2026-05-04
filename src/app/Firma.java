package app;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;

public class Firma {
    private List<Zamestnanec> zamestnanci;

    public Firma() {
        this.zamestnanci = new ArrayList<>();
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
    
    public void ulozDoSouboru(String soubor) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(soubor))) {
            for (Zamestnanec z : zamestnanci) {
                String typ = (z instanceof DatovyAnalytik) ? "A" : "S";
                pw.println(typ + ";" + z.getId() + ";" + z.getJmeno() + ";" + z.getPrijmeni() + ";" + z.getRokNarozeni());
            }
        } catch (IOException e) {
            System.out.println("Chyba při zápisu do souboru: " + e.getMessage());
        }
    }

    
    public void nactiZeSouboru(String soubor) {
        try (Scanner s = new Scanner(new File(soubor))) {
            zamestnanci.clear();
            while (s.hasNextLine()) {
                String[] casti = s.nextLine().split(";");
                int id = Integer.parseInt(casti[1]);
                String jm = casti[2];
                String pr = casti[3];
                int rok = Integer.parseInt(casti[4]);
                
                if (casti[0].equals("A")) pridejZamestnance(new DatovyAnalytik(id, jm, pr, rok));
                else pridejZamestnance(new BezpecnostniSpecialista(id, jm, pr, rok));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Soubor nenalezen, začínáme s prázdnou databází.");
        }
    }
}