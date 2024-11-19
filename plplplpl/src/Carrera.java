import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Carrera {
    private final List<Cavall> cavalls = new ArrayList<>();
    private final double distanciaCarrera;
    private final int numCavalls;
    private final Menjadora menjadora;
    private volatile boolean pausa = false; // Control de pausa global

    // Definición de los colores utilizando códigos ANSI
    String colorNaranja = "\u001B[38;5;214m";
    String colorVerde = "\u001B[32m";
    String colorRojo = "\u001B[31m";
    String resetColor = "\u001B[0m";


    ///////////////////////

    private final int posicioEstrella;
    private boolean estrellaReclamada;
    private Cavall cavallAmbEstrella;


    public Carrera(int numCavalls, double distanciaCarrera) {
        this.numCavalls = numCavalls;
        this.distanciaCarrera = distanciaCarrera;
        this.estrellaReclamada = false;
// Cream la posició aleatoria de l'estrella
        Random random = new Random();
        int totalPuntosBarra = 50; // La longitud de la barra de progreso
        this.posicioEstrella = random.nextInt(totalPuntosBarra);


        // Inicialitzar la menjadora amb capacitat 2
        this.menjadora = new Menjadora(2);

        // Crear cavalls
        for (int i = 1; i <= numCavalls; i++) {
            cavalls.add(new Cavall(i, distanciaCarrera, menjadora, this));
        }
    }

    public synchronized void esperarContinuar() throws InterruptedException {
        while (pausa) {
            wait();
        }
    }


    public void continuar() {
        synchronized (this) { // Utilitzem 'this' com a monitor de l'objecte Carrera
            pausa = false;
            notifyAll(); // Ara es crida dins d'un bloc sincronitzat
        }
    }
    public void parar() {
        synchronized (this) {
            pausa = true;
        }
    }



    public boolean isPausa() {
        return pausa;
    }

    public void iniciar() {
        System.out.println(colorNaranja + "La carrera comença!" + resetColor);

        // Iniciar els fils dels cavalls
        for (Cavall cavall : cavalls) {
            cavall.start();
        }

        boolean tresPrimersArribats = false;
        Scanner scanner = new Scanner(System.in);

        double distanciaEstrella = (distanciaCarrera * posicioEstrella) / 50; // Distancia real de la estrella

        while (true) {
            List<Cavall> candidatsAEstrella = new ArrayList<>();

            LocalTime horaActual = LocalTime.now();
            String horaFormatejada = horaActual.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.println(colorNaranja + horaFormatejada + resetColor);

            for (Cavall cavall : cavalls) {
                int barraProgres = (int) ((cavall.getPosicio() / distanciaCarrera) * 50);
                StringBuilder barra = new StringBuilder("-".repeat(barraProgres) + "🐌" + "-".repeat(50 - barraProgres));

                // Si la estrella aún no ha sido reclamada, agrégala a la posición
                if (!estrellaReclamada && posicioEstrella >= 0 && posicioEstrella < barra.length()) {
                    barra.setCharAt(posicioEstrella, '⭐');
                }

                System.out.printf("Cavall %2d %s %.2f km/h %.2f metres\n",
                        cavall.getIdCavall(), barra, cavall.getVelocitat(), cavall.getPosicio());

                // Comprobar si el caballo ha alcanzado la estrella
                if (!estrellaReclamada && cavall.getPosicio() >= distanciaEstrella) {
                    candidatsAEstrella.add(cavall);
                }
            }

            // Reclamar estrella si hay candidatos
            if (!candidatsAEstrella.isEmpty()) {
                reclamarEstrella(candidatsAEstrella);
            }

            // Comprobar si tres caballos han llegado a la meta
            long arribats = cavalls.stream().filter(Cavall::isHaArribat).count();
            if (arribats >= 3 && !tresPrimersArribats) {
                tresPrimersArribats = true;
                System.out.println("\nEls tres primers cavalls han arribat a la meta!");

                // Pausar la carrera
                parar();

                System.out.print("\nVols continuar amb la carrera? (si/no): ");
                String resposta = scanner.next();
                if (!resposta.equalsIgnoreCase("si")) {
                    for (Cavall cavall : cavalls) {
                        cavall.stop();
                    }
                    break;
                } else {
                    continuar(); // Reprendre la carrera
                }
            }

            // Comprobar si todos han llegado
            if (arribats == numCavalls) {
                break;
            }

            // Pausa antes de la siguiente iteración
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restaura el estado de interrupción
                e.printStackTrace();
            }
        }

        // Mostrar clasificación final
        mostrarClassificacio();
    }

    private void reclamarEstrella(List<Cavall> caballosCandidatos) {
        if (!estrellaReclamada && !caballosCandidatos.isEmpty()) {

            Random random = new Random();
            Cavall caballoElegido = caballosCandidatos.get(random.nextInt(caballosCandidatos.size()));

            // Marcar l'estrella com reclamada i asignar al cavall elegit
            estrellaReclamada = true;
            cavallAmbEstrella = caballoElegido;

            caballoElegido.aplicarBonus(3, 70);  // Aplicar bonus al cavall elegit pel random
            System.out.println(colorNaranja + "Cavall " + caballoElegido.getIdCavall() + " ⭐ ha capturat l'estrella i rep un bonus!" + resetColor);
        }
    }

    private void mostrarClassificacio() {
        System.out.println(colorNaranja+ "\nClassificació final:" + resetColor);
        cavalls.sort(Comparator.comparingDouble(Cavall::getTemps));
        for (int i = 0; i < cavalls.size(); i++) {
            Cavall cavall = cavalls.get(i);
            double distancia = cavall.getPosicio(); // en metres
            double velocitat = calcularVelocitatMitjja(cavall); // en km/h

            double temps = distancia / ( velocitat / 3.6);
            double temps2 = (cavall.getPosicio()/calcularVelocitatMitjja( cavall )+ cavall.getPauses()*3);

            System.out.printf("%2d. Cavall %2d - Temps: %.3f Distància: %.2f metres Velocitat mitja: %.2f km/h", i + 1, cavall.getIdCavall(),temps2,cavall.getPosicio(), calcularVelocitatMitjja(cavall));
            System.out.print(colorRojo + (cavall.getPauses() != 1 ? " "+ cavall.getPauses() + " pauses" : " " + cavall.getPauses() + " pausa") + resetColor);
            System.out.print((cavall == cavallAmbEstrella ? "⭐\n":"\n"));
        }
    }

    double calcularVelocitatMitjja(Cavall cavall) {
        // Calcular la velocidad media en km/h
        double velocitat = (cavall.getPosicio() / (cavall.getTemps() - (cavall.getPauses() *3)) * 3.6); // Convertimos m/s a km/h
        return velocitat;
    }

}
