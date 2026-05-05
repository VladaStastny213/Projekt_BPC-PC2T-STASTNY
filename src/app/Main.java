package app;

import java.util.Scanner;
import java.sql.*;


public class Main {
	
	
	public static void main(String[] args) {
        Firma firma = new Firma();
        firma.nacteni("data.txt");
        Firma firma1 = new Firma();
    	firma1.nactiZSql(); 
        Scanner scanner = new Scanner(System.in);
        boolean bezi = true;

        System.out.println("   system zamestnancu ");

        while (bezi) {
        	System.out.println("\n1 - pridat zamestnance");
            System.out.println("2 - vyhledat a spustit dovednost");
            System.out.println("3 - pridat spolupraci");
            System.out.println("4 - smazat zamestnance");
            System.out.println("5 - vypsat vse");
            System.out.println("6 - analyza rizikovosti");
            System.out.println("7 - Vyhledat zamestnance dle ID");
            System.out.println("8 - abecedni vypis podle pozic");
            System.out.println("9 - firemni statistika (prumer spoluprace)");
            System.out.println("10 - pocet zamestnancu ve skupinach");
            System.out.println("11 - ulozit konkretniho zamestnance");
            System.out.println("12 - nacist konkretniho zamestnance");
            System.out.println("0 - konec");
            System.out.print("volba: ");
            
            int volba = scanner.nextInt();
            scanner.nextLine();

        switch (volba) {
        case 1:
                  System.out.print("1-Analytik, 2-Specialista: ");
                  int typ = scanner.nextInt(); scanner.nextLine();
                    
                  System.out.print("jmeno: "); String j = scanner.nextLine();
                  System.out.print("prijmeni: "); String p = scanner.nextLine();
                  System.out.print("rok: "); int r = scanner.nextInt();
                    
                  int id = firma.getZamestnanecCount() + 1;
                  if (typ == 1) firma.pridejZamestnance(new DatovyAnalytik(id, j, p, r));

                  else firma.pridejZamestnance(new BezpecnostniSpecialista(id, j, p, r));
                    
                  System.out.println("pridano s ID " + id);
                    
                  firma.ulozeni("data.txt");
                  break;
                  
        case 2:
                  System.out.print("zadejte ID: ");
                  Zamestnanec z = firma.najdiZamestnance(scanner.nextInt());
                  if (z != null) {
                      System.out.println("nalezen: " + z.getJmeno() + " " + z.getPrijmeni());
                      z.provedDovednost();
                      } else System.out.println("nenalezen.");
                  break;

        case 3:
                  System.out.print("ID prvniho: "); int id1 = scanner.nextInt();
                  System.out.print("ID druheho: "); int id2 = scanner.nextInt();
                  System.out.print("zadejte uroven (1-3): ");
                  int u = scanner.nextInt();
                    
                  if (firma.pridejVazbu(id1, id2, u)) {
                      System.out.println("vazba byla ulozena");
                      firma.ulozeni("data.txt");
                  	  } else {
                        System.out.println("ERROR: ID neexistuje nebo jsou zadane ID stejna");
                  }
                  break;

        case 4:
                  System.out.print("ID ke smazani: ");
                  if (firma.smazZamestnance(scanner.nextInt())) System.out.println("zamestnanec byl smazan");
                  else System.out.println("ID neexistuje");
                  firma.ulozeni("data.txt");
                  break;
               
        case 5:
                  System.out.println(" seznam vsech zamestnancu ");
                  for (Zamestnanec zam : firma.getZamestnanci()) {
                  System.out.println(zam.toString());
                  }
                  break;

        case 6:
                  System.out.println(" analyza rizikovosti ");
                  for (Zamestnanec zam : firma.getZamestnanci()) {
                  System.out.println(zam.getJmeno() + " " + zam.getPrijmeni() + ": " + zam.getRizikoveSkore());
                  }
                  break;
        case 7:
            System.out.print("zadejte ID: ");
            Zamestnanec zInfo = firma.najdiZamestnance(scanner.nextInt());
            if (zInfo != null) {
                System.out.println("jmeno: " + zInfo.getJmeno() + " " + zInfo.getPrijmeni());
                System.out.println("rok narozeni: " + zInfo.getRokarozeni());
                System.out.println("rizikovost: " + zInfo.getRizikoveSkore());
            } else {
                System.out.println("nenalezen.");
            }
            break;
            
        case 8:
            java.util.List<Zamestnanec> analytici = new java.util.ArrayList<>();
            java.util.List<Zamestnanec> specialiste = new java.util.ArrayList<>();

            for (Zamestnanec z2 : firma.getZamestnanci()) {
                if (z2 instanceof DatovyAnalytik) analytici.add(z2);
                else specialiste.add(z2);
            }

            analytici.sort(java.util.Comparator.comparing(Zamestnanec::getPrijmeni));
            specialiste.sort(java.util.Comparator.comparing(Zamestnanec::getPrijmeni));

            System.out.println("--- Analytici ---");
            for (Zamestnanec a : analytici) System.out.println(a.getPrijmeni() + " " + a.getJmeno());
            
            System.out.println("--- Specialiste ---");
            for (Zamestnanec s : specialiste) System.out.println(s.getPrijmeni() + " " + s.getJmeno());
            break;
            
        case 9: {
            int suma = 0;
            int pocet = 0;
            for (Zamestnanec zam : firma.getZamestnanci()) {
               
                for (Integer urovenVazby : zam.getSpolupracovnici().values()) {
                    suma += urovenVazby;
                    pocet++;
                }
            }
            if (pocet == 0) {
                System.out.println("Zadne spoluprace ve firme neexistuji.");
            } else {
                System.out.println("Prumerna kvalita spoluprace: " + ((double) suma / pocet));
            }
            break;
        }    
        
        case 10: {
            int Analytici = 0;
            int Specialiste = 0;

            for (Zamestnanec z3 : firma.getZamestnanci()) {
                if (z3 instanceof DatovyAnalytik) Analytici++;
                else if (z3 instanceof BezpecnostniSpecialista) Specialiste++;
            }

            System.out.println("Datovi analytici: " + Analytici);
            System.out.println("Bezpecnostni specialiste: " + Specialiste);
            break;
        }
               
        case 11: {
            System.out.print("zadejte ID zamestnance k ulozeni: ");
            int id10 = scanner.nextInt(); scanner.nextLine();
            Zamestnanec zHledany = firma.najdiZamestnance(id10);
            
            if (zHledany != null) {
                System.out.print("zadejte jmeno souboru (napr. export.txt): ");
                String nazev = scanner.nextLine();
                try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(nazev))) {
                    String t = (zHledany instanceof DatovyAnalytik) ? "A" : "S";
                    pw.println(t + ";" + zHledany.getId() + ";" + zHledany.getJmeno() + ";" + zHledany.getPrijmeni() + ";" + zHledany.getRokarozeni());
                    System.out.println("zamestnanec ulozen");
                } catch (java.io.IOException e) {
                    System.out.println("chyba pri ukladani");
                }
            } else {
                System.out.println("zamestnanec s timto ID neexistuje");
            }
            break;
        }

        case 12: {
            System.out.print("zadejte jmeno souboru k nacteni: ");
            String nazev = scanner.nextLine();
            try (java.util.Scanner sSoubor = new java.util.Scanner(new java.io.File(nazev))) {
                if (sSoubor.hasNextLine()) {
                    String[] casti = sSoubor.nextLine().split(";");
                    int nId = Integer.parseInt(casti[1]);
                    
                    if (firma.najdiZamestnance(nId) != null) {
                        System.out.println("pozor: zamestnanec s timto ID uz ve firme je!");
                    } else {
                        String nJm = casti[2];
                        String nPr = casti[3];
                        int nRok = Integer.parseInt(casti[4]);
                        
                        if (casti[0].equals("A")) firma.pridejZamestnance(new DatovyAnalytik(nId, nJm, nPr, nRok));
                        else firma.pridejZamestnance(new BezpecnostniSpecialista(nId, nJm, nPr, nRok));
                        
                        System.out.println("zamestnanec uspesne nacten");
                    }
                }
            } catch (java.io.FileNotFoundException e) {
                System.out.println("soubor nenalezen");
            } catch (Exception e) {
                System.out.println("chyba formatu v souboru");
            }
            break;
        }
                    
        case 0:
        	
            firma.ulozDoSql(); 
            bezi = false;
            System.out.println("program ukoncen");
            break;
               
        default:
                  System.out.println("neplatna volba");
            }
        }
        scanner.close();
    }
}