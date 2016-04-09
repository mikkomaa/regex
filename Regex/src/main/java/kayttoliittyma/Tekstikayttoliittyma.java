package kayttoliittyma;

import domain.Jono;
import domain.Tila;
import java.util.Scanner;
import sovelluslogiikka.*;

/**
 * Luokka toteuttaa tekstikäyttöliittymän ohjelman ajamiseksi komentoriviltä
 */
public class Tekstikayttoliittyma implements Kayttoliittyma {

    String[] args;
    Scanner lukija;

    public Tekstikayttoliittyma(String[] args, Scanner lukija) {
        this.args = args;
        this.lukija = lukija;
    }

    @Override
    public void kaynnista() {
        Jono<Character> lauseke = kasitteleParametrit();
        Jono<Character> postfix = muunnaLauseke(lauseke);
        Automaatinluoja luoja = new Automaatinluoja();
        Tila nfa = luoja.luoAutomaatti(postfix);
        Automaatti automaatti = new Automaatti(nfa);

        while (lukija.hasNextLine()) {
            String rivi = lukija.nextLine();
            if (automaatti.suorita(rivi)) {
                System.out.println(rivi);
            }
        }
    }

    private Jono<Character> kasitteleParametrit() {
        Parametrikasittelija pk = new Parametrikasittelija(args);
        lukija = pk.avaaTiedosto();
        if (lukija == null) {
            tulostaAvausvirhe();
            System.exit(0);
        }

        Jono<Character> lauseke = pk.taulukoiLauseke();
        if (lauseke == null) {
            tulostaPuuttuvaLausekeVirhe();
            System.exit(0);
        }
        return lauseke;
    }

    private Jono<Character> muunnaLauseke(Jono<Character> lauseke) {
        Notaationmuuntaja muuntaja = new Notaationmuuntaja(lauseke);
        char c = muuntaja.onkoLausekeOikein();
        if (c != 'x') {
            tulostaLausekevirhe();
            System.exit(0);
        }
        muuntaja.lisaaPisteet();
        return muuntaja.muutaPostfixiin();
    }

    private void tulostaAvausvirhe() {
        if (args == null || args.length < 1) {
            System.out.println("Virhe: et antanut luettavaa tiedostoa.");
        } else {
            System.out.println("Virhe: tiedostoa " + args[0] + " ei voinut avata.");
        }
        tulostaOhje();
    }

    private void tulostaPuuttuvaLausekeVirhe() {
        System.out.println("Virhe: et antanut säännöllistä lauseketta.");
        tulostaOhje();
    }

    private void tulostaLausekevirhe() {
        System.out.println("Virhe: antamasi säännöllinen lauseke on virheellinen.");
        tulostaOhje();
    }

    private void tulostaOhje() {
        System.out.println("Ohjelman käyttö: ohjelmannimi tiedosto lauseke"); // päivitä tämä lopulliseen ajettavaan versioon
        System.out.println("Tarkemmat ohjeet ovat käyttöohjeessa.");
    }

}
