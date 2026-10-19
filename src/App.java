import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class App {

    public static void main(String[] args) {
        String rutaEntrada = "resources/paisaje.jpg";
        String rutaSalida = "resources/paisaje_filtrado.jpg";

        try {
            File archivo = new File(rutaEntrada);
            if (!archivo.exists()) {
                System.out.println("Error: No se encontró la imagen en: " + rutaEntrada);
                return;
            }

            System.out.println("Cargando imagen desde " + rutaEntrada + "...");
            BufferedImage imagenOriginal = ImageIO.read(archivo);
            int ancho = imagenOriginal.getWidth();
            int alto = imagenOriginal.getHeight();

            // Obtener el total de hilos lógicos del procesador
            //int numHilos = Runtime.getRuntime().availableProcessors();
            int numHilos = 8;

            System.out.println("Dimensiones       : " + ancho + " x " + alto + " píxeles");
            System.out.println("Hilos de CPU (p)  : " + numHilos + " hilos");
            System.out.println("Filtro aplicado   : Convolución 9x9 (Suavizado + Realce de Bordes)");
            System.out.println("----------------------------------------");

            // Pasar la imagen a una matriz 2D [fila][columna] en memoria compartida
            int[][] matrizEntrada = new int[alto][ancho];
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    matrizEntrada[y][x] = imagenOriginal.getRGB(x, y);
                }
            }

            int[][] salidaSecuencial = new int[alto][ancho];
            int[][] salidaParalela = new int[alto][ancho];

            int radio = FiltroKernel.RADIO; // 4 píxeles de margen en los bordes

            // 1. EJECUCIÓN SECUENCIAL (Un solo hilo procesa toda la imagen)            
            System.out.println("1. Iniciando procesamiento SECUENCIAL...");
            long inicioSec = System.currentTimeMillis();

            FiltroKernel.procesarFilas(matrizEntrada, salidaSecuencial, ancho, alto, radio, alto - radio);

            long finSec = System.currentTimeMillis();
            double tiempoSecuencialMs = finSec - inicioSec;
            System.out.printf("   -> Tiempo Secuencial (Ts): %.2f ms (%.2f segundos)%n", 
                              tiempoSecuencialMs, tiempoSecuencialMs / 1000.0);
            
            // 2. EJECUCIÓN PARALELA (Descomposición en bloques por hilos)            
            System.out.println("\n2. Iniciando procesamiento PARALELO con " + numHilos + " hilos...");
            HiloConvolucion[] hilos = new HiloConvolucion[numHilos];

            // Fase 1 y 3: Descomposición del dominio y Aglomeración de filas por hilo
            int filasTotales = alto - (2 * radio);
            int filasPorHilo = filasTotales / numHilos;

            long inicioPar = System.currentTimeMillis();

            // Fase 2 y 4: Asignación y Mapeo a cada objeto HiloConvolucion
            for (int i = 0; i < numHilos; i++) {
                int filaInicio = radio + (i * filasPorHilo);
                int filaFin;

                // El último hilo toma hasta el final por si la división no es exacta
                if (i == numHilos - 1) {
                    filaFin = alto - radio;
                } else {
                    filaFin = filaInicio + filasPorHilo;
                }

                hilos[i] = new HiloConvolucion(i + 1, matrizEntrada, salidaParalela, ancho, alto, filaInicio, filaFin);
                hilos[i].start();
            }

            // Esperar a que todos los hilos terminen su trabajo (Sincronización)
            for (int i = 0; i < numHilos; i++) {
                hilos[i].join();
            }

            long finPar = System.currentTimeMillis();
            double tiempoParaleloMs = finPar - inicioPar;
            System.out.printf("   -> Tiempo Paralelo (Tp)  : %.2f ms (%.2f segundos)%n", 
                              tiempoParaleloMs, tiempoParaleloMs / 1000.0);
            
            // 3. CÁLCULO DE MÉTRICAS (Speedup y Eficiencia)            
            double speedup = tiempoSecuencialMs / tiempoParaleloMs;
            double eficiencia = speedup / numHilos;

            System.out.println("----------------------------------------");
            System.out.println("RESULTADOS DE RENDIMIENTO:");
            System.out.printf("Aceleramiento (Speedup S = Ts / Tp) : %.2fx%n", speedup);
            System.out.printf("Eficiencia (E = S / p)              : %.2f (%.1f%%)%n", eficiencia, eficiencia * 100);
            System.out.println("----------------------------------------");

            // Crear la imagen final con los datos calculados en paralelo
            BufferedImage imagenProcesada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    imagenProcesada.setRGB(x, y, salidaParalela[y][x]);
                }
            }

            // Guardar copia en la carpeta resources
            ImageIO.write(imagenProcesada, "jpg", new File(rutaSalida));
            System.out.println("Imagen guardada en: " + rutaSalida);

            // Mostrar ventana comparativa
            VentanaComparacion ventana = new VentanaComparacion(
                imagenOriginal, imagenProcesada, 
                tiempoSecuencialMs, tiempoParaleloMs, 
                speedup, eficiencia, numHilos
            );
            ventana.setVisible(true);

        } catch (Exception e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}