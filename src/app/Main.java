package app;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class Main {

    public static void main(String[] args) {

        Firma firma = new Firma();
        
        firma.nactiZSql(); 

        Scanner scanner = new Scanner(System.in);
        boolean bezi = true;

        System.out.println("   SYSTÉM ZAMĚSTNANCŮ (SQL MODE) ");

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
            System.out.println("11 - exportovat konkretniho zamestnance do TXT");
            System.out.println("12 - importovat konkretniho zamestnance z TXT");
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
                    firma.ulozDoSql();
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
                        firma.ulozDoSql(); 
                    } else {
                        System.out.println("ERROR: ID neexistuje nebo jsou zadane ID stejna");
                    }
                    break;

                case 4:
                    System.out.print("ID ke smazani: ");
                    if (firma.smazZamestnance(scanner.nextInt())) {
                        System.out.println("zamestnanec byl smazan");
                        firma.ulozDoSql(); 
                    } else System.out.println("ID neexistuje");
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
                    } else System.out.println("nenalezen.");
                    break;

                case 8:
                    List<Zamestnanec> analytici = new ArrayList<>();
                    List<Zamestnanec> specialiste = new ArrayList<>();
                    for (Zamestnanec zTemp : firma.getZamestnanci()) {
                        if (zTemp instanceof DatovyAnalytik) analytici.add(zTemp);
                        else specialiste.add(zTemp);
                    }
                    analytici.sort(Comparator.comparing(Zamestnanec::getPrijmeni));
                    specialiste.sort(Comparator.comparing(Zamestnanec::getPrijmeni));

                    System.out.println("--- Analytici ---");
                    for (Zamestnanec a : analytici) System.out.println(a.getPrijmeni() + " " + a.getJmeno());
                    System.out.println("--- Specialiste ---");
                    for (Zamestnanec s : specialiste) System.out.println(s.getPrijmeni() + " " + s.getJmeno());
                    break;

                case 9:
                    int suma = 0, pocet = 0;
                    for (Zamestnanec zam : firma.getZamestnanci()) {
                        for (Integer urovenVazby : zam.getSpolupracovnici().values()) {
                            suma += urovenVazby;
                            pocet++;
                        }
                    }
                    if (pocet == 0) System.out.println("Zadne spoluprace neexistuji.");
                    else System.out.println("Prumerna kvalita spoluprace: " + ((double) suma / pocet));
                    break;

                case 10:
                    int an = 0, sp = 0;
                    for (Zamestnanec z3 : firma.getZamestnanci()) {
                        if (z3 instanceof DatovyAnalytik) an++;
                        else sp++;
                    }
                    System.out.println("Analytici: " + an + ", Specialiste: " + sp);
                    break;

                case 11:
                    System.out.print("ID k exportu: ");
                    int idExp = scanner.nextInt(); scanner.nextLine();
                    Zamestnanec zExp = firma.najdiZamestnance(idExp);
                    if (zExp != null) {
                        System.out.print("Název souboru: ");
                        String nazev = scanner.nextLine();
                        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(nazev))) {
                            String t = (zExp instanceof DatovyAnalytik) ? "A" : "S";
                            pw.println(t + ";" + zExp.getId() + ";" + zExp.getJmeno() + ";" + zExp.getPrijmeni() + ";" + zExp.getRokarozeni());
                            System.out.println("Exportováno do TXT.");
                        } catch (Exception e) { System.out.println("Chyba exportu."); }
                    } else System.out.println("ID neexistuje.");
                    break;

                case 12:
                    System.out.print("Soubor k importu: ");
                    String impNazev = scanner.nextLine();
                    try (Scanner sSoubor = new Scanner(new java.io.File(impNazev))) {
                        if (sSoubor.hasNextLine()) {
                            String[] c = sSoubor.nextLine().split(";");
                            int nId = Integer.parseInt(c[1]);
                            if (firma.najdiZamestnance(nId) == null) {
                                if (c[0].equals("A")) firma.pridejZamestnance(new DatovyAnalytik(nId, c[2], c[3], Integer.parseInt(c[4])));
                                else firma.pridejZamestnance(new BezpecnostniSpecialista(nId, c[2], c[3], Integer.parseInt(c[4])));
                                firma.ulozDoSql();
                                System.out.println("Importován a uložen do SQL.");
                            } else System.out.println("ID již existuje.");
                        }
                    } catch (Exception e) { System.out.println("Chyba importu."); }
                    break;

                case 0:
                    firma.ulozDoSql();
                    bezi = false;
                    System.out.println("Program ukončen.");
                    break;

                default:
                    System.out.println("Neplatná volba.");
            }
        }
        scanner.close();
    }
}