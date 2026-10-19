public class HiloConvolucion extends Thread {

    private int idHilo;
    private int[][] matrizEntrada;
    private int[][] matrizSalida;
    private int ancho;
    private int alto;
    private int filaInicio;
    private int filaFin;

    public HiloConvolucion(int idHilo, int[][] matrizEntrada, int[][] matrizSalida, 
                           int ancho, int alto, int filaInicio, int filaFin) {
        this.idHilo = idHilo;
        this.matrizEntrada = matrizEntrada;
        this.matrizSalida = matrizSalida;
        this.ancho = ancho;
        this.alto = alto;
        this.filaInicio = filaInicio;
        this.filaFin = filaFin;
    }

    @Override
    public void run() {
        // El hilo procesa únicamente su bloque de filas asignado
        FiltroKernel.procesarFilas(matrizEntrada, matrizSalida, ancho, alto, filaInicio, filaFin);
    }

    public int getIdHilo() {
        return idHilo;
    }
}