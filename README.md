
# ARSW — (Java 21): **Immortals & Synchronization** — con UI Swing

**Escuela Colombiana de Ingeniería – Arquitecturas de Software**  
Laboratorio de concurrencia: condiciones de carrera, sincronización, suspensión cooperativa y *deadlocks*, con interfaz **Swing** tipo *Highlander Simulator*.


---

## Requisitos

- **JDK 21** (Temurin recomendado)
- **Maven 3.9+**
- SO: Windows, macOS o Linux

---

---

## Parte II — (Antes de terminar la clase) Búsqueda distribuida y condición de parada
Reescribe el **buscador de listas negras** para que la búsqueda **se detenga tan pronto** el conjunto de hilos detecte el número de ocurrencias que definen si el host es confiable o no (`BLACK_LIST_ALARM_COUNT`). Debe:
- **Finalizar anticipadamente** (no recorrer servidores restantes) y **retornar** el resultado.  
- Garantizar **ausencia de condiciones de carrera** sobre el contador compartido.

> Puedes usar `AtomicInteger` o sincronización mínima sobre la región crítica del contador.

---

## Criterios de evaluación (10 pts)

- (3) **Concurrencia correcta**: sin *data races*; sincronización bien localizada; no hay espera activa.  
- (2) **Pausa/Reanudar**: consistencia del estado e invariante bajo **Pause & Check**.  
- (2) **Robustez**: corre con N alto; sin `ConcurrentModificationException`, sin *deadlocks* no gestionados.  
- (1.5) **Calidad**: arquitectura clara, nombres y comentarios; separación UI/lógica.  
- (1.5) **Documentación**: **`RESPUESTAS.txt`** claro con evidencia (dumps/capturas) y justificación técnica.

---

## Cómo correr pruebas

```bash
mvn clean verify
```

Incluye compilación y pruebas JUnit.

---

## Créditos y licencia

Laboratorio basado en el enunciado histórico del curso (Highlander, Productor/Consumidor, Búsqueda distribuida), modernizado a **Java 21**.  
<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software (ECI) y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.
