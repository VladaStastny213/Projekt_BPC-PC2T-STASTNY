package app;

public class DatovyAnalytik extends Zamestnanec {

    public DatovyAnalytik(int id, String jmeno, String prijmeni, int roknarozeni) {
        super(id, jmeno, prijmeni, roknarozeni);
    }

    
    @Override
    public void provedDovednost() {
    	System.out.println("analyza: analytik ma vazby na " + spolupracovnici.size() + " kolegu");
    }
}