package app;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Firma firma = new Firma();
        firma.nactiZeSouboru("data.txt");
        Scanner scanner = new Scanner(System.in);
        boolean bezi = true;

        System.out.println("--- Systém zaměstnanců ---");

        while (bezi) {
            System.out.println("\n1 - Přidat zaměstnance");
            System.out.println("2 - Vyhledat a spustit dovednost");
            System.out.println("3 - Přidat spolupráci (vazbu)");
            System.out.println("4 - Smazat zaměstnance");
            System.out.println("5 - Vypsat vše");
            System.out.println("6 - Analýza rizikovosti");
            System.out.println("0 - Konec");
            System.out.print("Volba: ");
            
            int volba = scanner.nextInt();
            scanner.nextLine();

            switch (volba) {
                case 1:
                    System.out.print("1-Analytik, 2-Specialista: ");
                    int typ = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Jméno: "); String j = scanner.nextLine();
                    System.out.print("Příjmení: "); String p = scanner.nextLine();
                    System.out.print("Rok: "); int r = scanner.nextInt();
                    int id = firma.getZamestnanecCount() + 1;
                    if (typ == 1) firma.pridejZamestnance(new DatovyAnalytik(id, j, p, r));
                    else firma.pridejZamestnance(new BezpecnostniSpecialista(id, j, p, r));
                    System.out.println("Přidáno s ID " + id);
                    firma.ulozDoSouboru("data.txt");
                    break;

                case 2:
                    System.out.print("Zadejte ID: ");
                    Zamestnanec z = firma.najdiZamestnance(scanner.nextInt());
                    if (z != null) {
                        System.out.println("Nalezen: " + z.getJmeno() + " " + z.getPrijmeni());
                        z.provedDovednost();
                    } else System.out.println("Nenalezen.");
                    break;

                case 3:
                    System.out.print("ID prvního: "); int id1 = scanner.nextInt();
                    System.out.print("ID druhého: "); int id2 = scanner.nextInt();
                    System.out.print("Úroveň (1-3): ");
                    int u = scanner.nextInt();
                    
                    if (firma.pridejVazbu(id1, id2, u)) {
                        System.out.println("Vazba uložena.");
                        firma.ulozDoSouboru("data.txt");
                    } else {
                        System.out.println("Chyba: ID neexistují nebo jsou stejná.");
                    }
                    break;

                case 4:
                    System.out.print("ID ke smazání: ");
                    if (firma.smazZamestnance(scanner.nextInt())) System.out.println("Zaměstnanec smazán.");
                    else System.out.println("ID neexistuje.");
                    firma.ulozDoSouboru("data.txt");
                    break;
               
                case 5:
                    System.out.println("--- Seznam všech zaměstnanců ---");
                    for (Zamestnanec zam : firma.getZamestnanci()) {
                        System.out.println(zam.toString());
                    }
                    break;
                    
                case 6:
                    System.out.println("--- Analýza rizikovosti ---");
                    for (Zamestnanec zam : firma.getZamestnanci()) {
                        System.out.println(zam.getJmeno() + " " + zam.getPrijmeni() + ": " + zam.getRizikoveSkore());
                    }
                    break;

                    
                case 0:
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