
/**
 * Hilo que realiza la busqueda de una direccion IP en un rango de servidores de listas negras.
 * Permite detener la busqueda de forma global si se alcanza un umbral de coincidencias.
 * Utiliza una bandera atomica para sincronizar la detencion entre multiples hilos.
 */
package co.eci.blacklist.labs.part2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import co.eci.blacklist.infrastructure.HostBlackListsDataSourceFacade;

/**
 * Implementacion de un hilo para busqueda paralela en listas negras.
 */
public class ParallelBlacklistSearchThread extends Thread {
    private final String ip;
    private final int startServer;
    private final int endServer;
    private final HostBlackListsDataSourceFacade facade;
    private final List<Integer> blackListOccurrences;
    private AtomicInteger globalCounter;
    private final AtomicBoolean stop;
    private int totalChecked = 0;
    private int threshold;
    

    /**
     * Constructor del hilo de busqueda paralela.
     * @param ip Direccion IP a buscar
     * @param startServer Indice inicial del rango de servidores
     * @param endServer Indice final del rango de servidores
     * @param facade Fachada para consultar las listas negras
     * @param stop Bandera atomica para detener la busqueda global
     * @param threshold Umbral de coincidencias para detener la busqueda
     */
    public ParallelBlacklistSearchThread(String ip, int startServer, int endServer, HostBlackListsDataSourceFacade facade, AtomicInteger globalCounter, AtomicBoolean stop, int threshold) {
        this.ip = ip;
        this.startServer = startServer;
        this.endServer = endServer;
        this.facade = facade;
        this.blackListOccurrences = new ArrayList<>();
        this.globalCounter = globalCounter;
        this.stop = stop; 
        this.threshold=threshold;  
    }

    /**
     * Obtiene la lista de indices de servidores donde se encontro la IP.
     * @return Lista de indices de coincidencias
     */
    public List<Integer> getBlackListOccurrences() {
        return blackListOccurrences;
    }

    /**
     * Retorna el numero de coincidencias encontradas.
     * @return Cantidad de coincidencias
     */
    public int getMatchCount() {
        return blackListOccurrences.size();
    }

    /**
     * Retorna el total de servidores revisados por este hilo.
     * @return Cantidad de servidores revisados
     */
    public int getTotalChecked() {
        return totalChecked;
    }
    /**
     * Ejecuta la busqueda de la IP en el rango asignado.
     * Si se alcanza el umbral de coincidencias, detiene la busqueda global.
     */
    @Override
    public void run() {
        for (int i = startServer; i < endServer; i++) {
            if (stop.get()) break;
            if (facade.isInBlackListServer(i, ip)) {
                blackListOccurrences.add(i);
                int total = globalCounter.incrementAndGet();
                if (total >= threshold) {
                    stop.set(true);
                }
            }
            totalChecked++;
        }
    }
}
