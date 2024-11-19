import java.util.ArrayList;
import java.util.Random;

public class Cavall extends Thread {
    private final int id;
    private double velocitat; // en km/h
    private double posicio; // en metres
    private final double distanciaMeta; // en metres
    private final Menjadora menjadora; // Afegim la menjadora
    private final Carrera carrera; // Referència a la carrera
    private boolean haArribat;
    private static boolean tieneEstrella = false;
    private double temps = 0;
    private double distanciaActual = 0;
    private int pauses = 0;
    private double velocitatsSegons = 0.0;  // Lista de velocitats per segon





    public Cavall(int id, double distanciaMeta, Menjadora menjadora, Carrera carrera) {
        this.id = id;
        this.velocitat = 50; // Velocitat inicial
        this.posicio = 0; // Posició inicial
        this.distanciaMeta = distanciaMeta;
        this.menjadora = menjadora; // Assignar la menjadora compartida
        this.carrera = carrera; // Assignar la carrera compartida
        this.haArribat = false;

    }

    public void setHaArribat(boolean haArribat) {
        this.haArribat = haArribat;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Random random = new Random();
            while (!haArribat) {
                temps++;
                try {
                    // Comprovar si la carrera està pausada
                    synchronized (carrera) {
                        while (carrera.isPausa()) {
                            carrera.wait(); // Esperar fins que es reprengui
                        }
                    }

                    // Simular menjar en moments aleatoris
                    if (random.nextInt(10) < 1) { // Un 10% de probabilitat de parar a menjar
                        synchronized (carrera) {
                            //pauses++;
                            while (carrera.isPausa()) {
                                carrera.wait(); // Esperar si la carrera està pausada
                            }
                        }

                        menjadora.entrarMenjar("Cavall " + id);
                        Thread.sleep(3000); // Menjar 3 SEGONS
                        temps +=3;
                        pauses++;


                        synchronized (carrera) {
                            while (carrera.isPausa()) {
                                carrera.wait(); // Esperar si la carrera està pausada
                            }
                        }

                        menjadora.sortirMenjar("Cavall " + id);
                    }

                    // Variar velocitat entre -5 i +5 i mantenir dins els límits (15-70)
                    synchronized (carrera) {
                        while (carrera.isPausa()) {
                            carrera.wait(); // Esperar si la carrera està pausada
                        }
                    }

                    velocitat += random.nextInt(11) - 5; // entre -5 i +5
                    velocitat = Math.min(70, Math.max(15, velocitat));
                    velocitatsSegons = velocitatsSegons + velocitat;

                    // Convertir velocitat de km/h a m/s i avançar
                    double velocitatMetresSegon = velocitat * 1000 / 3600;
                    posicio += velocitatMetresSegon;

                    // Comprovar si ha arribat a la meta
                    if (posicio >= distanciaMeta) {
                        posicio = distanciaMeta;
                        haArribat = true;
                    }

                    // Pausar un segon (simular temps real)
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public double getVelocitatsSegons() {
        return velocitatsSegons;
    }

    public int getPauses() {
        return pauses;
    }

    public double getDistanciaActual() {
        return distanciaActual;
    }

    public int getIdCavall() {
        return id;
    }

    public double getVelocitat() {
        return velocitat;
    }

    public double getPosicio() {
        return posicio;
    }

    public boolean isHaArribat() {
        return haArribat;
    }
    public void aplicarBonus(int tiempoBonus, double velocidadBonus) {
        this.temps = Math.max(0, this.temps - tiempoBonus); // Evitar tiempos negativos
        this.velocitat = velocidadBonus; // Asignar la velocidad de bonus
        this.tieneEstrella = true; // Indicar que este caballo tiene la estrella
    }


    public double getTemps() {
        return temps;
    }
}
