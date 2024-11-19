import java.util.ArrayList;
import java.util.List;

public class Menjadora {
    private final int capacitat;
    private int cavallsMenjant;

    public Menjadora(int capacitat) {
        this.capacitat = capacitat;
        this.cavallsMenjant = 0;
    }

    public synchronized void entrarMenjar(String nomCavall) throws InterruptedException {
        // Esperar fins que hi hagi espai a la menjadora (màxim 2 cavalls)
        while (cavallsMenjant >= capacitat) {
            wait();
        }

        // Quan hi hagi espai, el cavall entra a menjar
        cavallsMenjant++;

        System.out.println(nomCavall + " està menjant \uD83C\uDF4F. Cavalls menjant ara: " + cavallsMenjant);
    }

    public synchronized void sortirMenjar(String nomCavall) {
        // El cavall deixa la menjadora
        cavallsMenjant--;
        System.out.println(nomCavall + " ha acabat de menjar \uD83C\uDF4F. Cavalls menjant ara: " + cavallsMenjant);

        // Notificar als altres cavalls que hi ha espai a la menjadora
        notifyAll();
    }


}
