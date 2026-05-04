package app;

public class DatovyAnalytik extends Zamestnanec {

    public DatovyAnalytik(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    
    @Override
    public void provedDovednost() {
    	System.out.println("Analýza sítě: Tento analytik má vazby na " + spolupracovnici.size() + " kolegů.");
    }
}