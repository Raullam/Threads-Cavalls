import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double distanciaCarrera;
        int numCavalls;

        String colorRojo = "\u001B[31m";
        String resetColor = "\u001B[0m";
        String colorNaranja = "\u001B[38;5;214m";

        while (true) {
            while (true) {
                try {
                    System.out.print(colorNaranja + "Introdueix la distància del recorregut (en metres): " + resetColor);
                    numCavalls = scanner.nextInt();
                    if (numCavalls > 0) {
                        break; // Sortir del bucle si el valor és vàlid
                    } else {
                        System.out.println(colorRojo + "La distància ha de ser un número positiu." + resetColor);
                    }
                } catch (InputMismatchException e) {
                    System.out.println(colorRojo + "Error: Has d'introduir un número decimal." + resetColor);
                    scanner.nextLine(); // Consumir l'entrada incorrecta
                }
            }

            // Capturar l'entrada per a la distància
            while (true) {
                try {
                    System.out.print(colorNaranja + "Introdueix la distància del recorregut (en metres): " + resetColor);
                    distanciaCarrera = scanner.nextDouble();
                    if (distanciaCarrera > 0) {
                        break; // Sortir del bucle si el valor és vàlid
                    } else {
                        System.out.println(colorRojo + "La distància ha de ser un número positiu." + resetColor);
                    }
                } catch (InputMismatchException e) {
                    System.out.println(colorRojo + "Error: Has d'introduir un número decimal." + resetColor);
                    scanner.nextLine(); // Consumir l'entrada incorrecta
                }
            }



            // Crear una nova instància de la carrera
            Carrera carrera = new Carrera(numCavalls, distanciaCarrera);
            carrera.iniciar();

            // Tornar a simular?
            System.out.print(colorNaranja + "\nVols fer una nova simulació? (si/no): " + resetColor);
            if (!scanner.next().equalsIgnoreCase("si")) {
               // System.out.println("Gràcies per utilitzar el simulador de carreres!");
                break;
            }
        }

        scanner.close();
    }
}
