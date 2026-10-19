public class FiltroKernel {

    // Tamaño del filtro: 9x9 (radio de 4 píxeles alrededor del centro)
    public static final int TAMANO = 9;
    public static final int RADIO = TAMANO / 2;

    // Matriz para detectar bordes horizontales (9x9)
    public static final int[][] BORDES_X = {
        {-1, -1, -1, -1, 0, 1, 1, 1, 1},
        {-1, -1, -1, -1, 0, 1, 1, 1, 1},
        {-1, -1, -1, -2, 0, 2, 1, 1, 1},
        {-1, -1, -2, -3, 0, 3, 2, 1, 1},
        {-2, -2, -3, -4, 0, 4, 3, 2, 2},
        {-1, -1, -2, -3, 0, 3, 2, 1, 1},
        {-1, -1, -1, -2, 0, 2, 1, 1, 1},
        {-1, -1, -1, -1, 0, 1, 1, 1, 1},
        {-1, -1, -1, -1, 0, 1, 1, 1, 1}
    };

    // Matriz para detectar bordes verticales (9x9)
    public static final int[][] BORDES_Y = {
        {-1, -1, -1, -1, -2, -1, -1, -1, -1},
        {-1, -1, -1, -1, -2, -1, -1, -1, -1},
        {-1, -1, -1, -2, -3, -2, -1, -1, -1},
        {-1, -1, -2, -3, -4, -3, -2, -1, -1},
        { 0,  0,  0,  0,  0,  0,  0,  0,  0},
        { 1,  1,  2,  3,  4,  3,  2,  1,  1},
        { 1,  1,  1,  2,  3,  2,  1,  1,  1},
        { 1,  1,  1,  1,  2,  1,  1,  1,  1},
        { 1,  1,  1,  1,  2,  1,  1,  1,  1}
    };

    /**
     * Aplica la convolución de 9x9 sobre un rango de filas de la imagen.
     * tanto el modo secuencial como cada hilo en paralelo.
     */
    public static void procesarFilas(int[][] matrizOriginal, int[][] matrizSalida, 
                                     int ancho, int alto, int filaInicio, int filaFin) {
        
        for (int y = filaInicio; y < filaFin; y++) {
            int[] filaSalida = matrizSalida[y]; // Referencia directa a la fila de salida en caché

            for (int x = RADIO; x < ancho - RADIO; x++) {
                
                int sumaSuaveR = 0, sumaSuaveG = 0, sumaSuaveB = 0;
                int gxR = 0, gyR = 0;
                int gxG = 0, gyG = 0;
                int gxB = 0, gyB = 0;

                // Recorrer la vecindad de 9x9 usando enteros puros (más rápido en ALU que double)
                for (int ky = -RADIO; ky <= RADIO; ky++) {
                    int[] filaVecina = matrizOriginal[y + ky]; // Evita saltar en RAM en cada columna
                    int[] filaKernelX = BORDES_X[ky + RADIO];
                    int[] filaKernelY = BORDES_Y[ky + RADIO];

                    for (int kx = -RADIO; kx <= RADIO; kx++) {
                        int rgbVecino = filaVecina[x + kx];
                        
                        int r = (rgbVecino >> 16) & 255;
                        int g = (rgbVecino >> 8) & 255;
                        int b = rgbVecino & 255;

                        sumaSuaveR += r;
                        sumaSuaveG += g;
                        sumaSuaveB += b;

                        int pesoX = filaKernelX[kx + RADIO];
                        int pesoY = filaKernelY[kx + RADIO];

                        gxR += r * pesoX;
                        gyR += r * pesoY;

                        gxG += g * pesoX;
                        gyG += g * pesoY;

                        gxB += b * pesoX;
                        gyB += b * pesoY;
                    }
                }

                int baseR = sumaSuaveR / 81;
                int baseG = sumaSuaveG / 81;
                int baseB = sumaSuaveB / 81;

                int bordeR = (int) (Math.sqrt(gxR * gxR + gyR * gyR) / 18.0);
                int bordeG = (int) (Math.sqrt(gxG * gxG + gyG * gyG) / 18.0);
                int bordeB = (int) (Math.sqrt(gxB * gxB + gyB * gyB) / 18.0);

                int finalR = limitarRango((int) (baseR * 0.85 + bordeR * 1.8));
                int finalG = limitarRango((int) (baseG * 0.85 + bordeG * 1.8));
                int finalB = limitarRango((int) (baseB * 0.95 + bordeB * 2.1));

                filaSalida[x] = (finalR << 16) | (finalG << 8) | finalB;
            }
        }
    }

    // Asegura que el valor de color siempre esté entre 0 y 255
    private static int limitarRango(int valor) {
        if (valor < 0) return 0;
        if (valor > 255) return 255;
        return valor;
    }
}