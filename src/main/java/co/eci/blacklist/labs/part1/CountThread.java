
/**
 * Hilo que imprime en consola un rango de numeros enteros.
 * Cada instancia cuenta desde un valor inicial hasta uno final, mostrando el nombre del hilo y el valor actual.
 */
package co.eci.blacklist.labs.part1;

/**
 * Implementacion de un hilo que cuenta e imprime un rango de numeros.
 */
public class CountThread extends Thread {
  private final int from;
  private final int to;

    /**
     * Constructor del hilo contador.
     * @param from Numero inicial del rango
     * @param to Numero final del rango
     */
    public CountThread(int from, int to) {
    this.from = from;
    this.to = to;
    setName("CountThread-" + from + "-" + to);
  }

    /**
     * Ejecuta el conteo e imprime cada numero del rango junto con el nombre del hilo.
     */
    @Override
    public void run() {
        for (int i = from; i <= to; i++) {
            System.out.println("[" + getName() + "] " + i);
        }
    }
}