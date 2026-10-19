import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class VentanaComparacion extends JFrame {

    public VentanaComparacion(BufferedImage imgOriginal, BufferedImage imgProcesada, 
                              double tiempoSecMs, double tiempoParMs, 
                              double speedup, double eficiencia, int numHilos) {
        
        setTitle("Comparación de Convolución 9x9: Secuencial vs Paralelo (" + numHilos + " Hilos)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Escalar las imágenes para que quepan bien en la pantalla del monitor
        int anchoVista = 640;
        int altoVista = (int) ((double) imgOriginal.getHeight() / imgOriginal.getWidth() * anchoVista);

        Image originalEscalada = imgOriginal.getScaledInstance(anchoVista, altoVista, Image.SCALE_SMOOTH);
        Image procesadaEscalada = imgProcesada.getScaledInstance(anchoVista, altoVista, Image.SCALE_SMOOTH);

        // Panel central con las 2 fotos lado a lado
        JPanel panelFotos = new JPanel(new GridLayout(1, 2, 15, 0));
        panelFotos.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panelFotos.setBackground(new Color(35, 35, 40));

        JLabel lblOriginal = new JLabel("Original (" + imgOriginal.getWidth() + "x" + imgOriginal.getHeight() + ")",
                                        new ImageIcon(originalEscalada), JLabel.CENTER);
        lblOriginal.setHorizontalTextPosition(JLabel.CENTER);
        lblOriginal.setVerticalTextPosition(JLabel.TOP);
        lblOriginal.setForeground(Color.WHITE);
        lblOriginal.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel lblProcesada = new JLabel("Filtro Convolución 9x9 Realce Nocturno (" + numHilos + " hilos)",
                                         new ImageIcon(procesadaEscalada), JLabel.CENTER);
        lblProcesada.setHorizontalTextPosition(JLabel.CENTER);
        lblProcesada.setVerticalTextPosition(JLabel.TOP);
        lblProcesada.setForeground(new Color(120, 255, 160));
        lblProcesada.setFont(new Font("SansSerif", Font.BOLD, 14));

        panelFotos.add(lblOriginal);
        panelFotos.add(lblProcesada);

        // Barra inferior con las métricas calculadas
        String textoMetricas = String.format(
            "   Tiempo Secuencial (Ts): %.2f seg   |   Tiempo Paralelo (Tp): %.2f seg   |   Speedup (S): %.2fx   |   Eficiencia (E): %.1f%%   ",
            tiempoSecMs / 1000.0, tiempoParMs / 1000.0, speedup, eficiencia * 100
        );

        JLabel lblMetricas = new JLabel(textoMetricas, JLabel.CENTER);
        lblMetricas.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblMetricas.setOpaque(true);
        lblMetricas.setBackground(new Color(20, 20, 25));
        lblMetricas.setForeground(Color.WHITE);
        lblMetricas.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        add(panelFotos, BorderLayout.CENTER);
        add(lblMetricas, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Centrar ventana
    }
}