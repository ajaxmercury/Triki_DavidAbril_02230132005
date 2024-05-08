import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class TrikiGUI extends JFrame {
    private JButton[][] botones;
    private JuegoTriki juego;
    private JButton reiniciarButton;

    private final Color MORADO_OSCURO = new Color(102, 51, 153);
    private final Color MORADO_CLARO = new Color(153, 102, 204);
    private final Color GRIS_CLARO = new Color(220, 220, 220);
    private final Color NEGRO = new Color(0, 0, 0);

    public TrikiGUI() {
        setTitle("Triki");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(MORADO_OSCURO);

        JPanel panelJuego = new JPanel(new GridLayout(3, 3, 5, 5));
        panelJuego.setBackground(MORADO_CLARO);
        botones = new JButton[3][3];
        juego = new JuegoTriki();
        inicializarInterfaz(panelJuego);

        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.setBackground(MORADO_OSCURO);
        reiniciarButton = new JButton("Reiniciar");
        reiniciarButton.setBackground(MORADO_CLARO);
        reiniciarButton.setForeground(Color.WHITE);
        reiniciarButton.setFocusPainted(false);
        reiniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.reiniciarJuego();
                actualizarInterfaz();
            }
        });
        panelBoton.add(reiniciarButton);

        add(panelJuego, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.EAST);

        setSize(400, 300);
        setVisible(true);
    }

    private void inicializarInterfaz(JPanel panelJuego) {
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                botones[fila][columna] = new JButton();
                botones[fila][columna].setFont(new Font("Arial", Font.BOLD, 36));
                botones[fila][columna].setBackground(GRIS_CLARO);
                botones[fila][columna].setForeground(NEGRO);
                botones[fila][columna].setFocusPainted(false);
                botones[fila][columna].setBorder(BorderFactory.createLineBorder(MORADO_OSCURO, 2));
                botones[fila][columna].addActionListener(new BotonListener(fila, columna));
                panelJuego.add(botones[fila][columna]);
            }
        }
    }

    private void actualizarInterfaz() {
        char[][] tablero = juego.getTablero();
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                botones[fila][columna].setText(String.valueOf(tablero[fila][columna]));
            }
        }
    }

    private class BotonListener implements ActionListener {
        private int fila;
        private int columna;

        public BotonListener(int fila, int columna) {
            this.fila = fila;
            this.columna = columna;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!juego.hayGanador() && !juego.tableroLleno()) {
                if (juego.marcarCasilla(fila, columna)) {
                    actualizarInterfaz();
                    switch (juego.getEstadoJuego()) {
                        case JuegoTriki.GANADOR:
                            mostrarMensaje("¡El jugador " + juego.getJugadorActual() + " ha ganado!");
                            break;
                        case JuegoTriki.EMPATE:
                            mostrarMensaje("¡Empate!");
                            break;
                        case JuegoTriki.CONTINUAR:
                            if (juego.isContraMaquina() && juego.getJugadorActual() == 'O') {
                                juego.jugarMaquina();
                                actualizarInterfaz();
                                switch (juego.getEstadoJuego()) {
                                    case JuegoTriki.GANADOR:
                                        mostrarMensaje("¡La máquina ha ganado!");
                                        break;
                                    case JuegoTriki.EMPATE:
                                        mostrarMensaje("¡Empate!");
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    mostrarMensaje("¡Casilla ocupada! Intente nuevamente.");
                }
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrikiGUI::new);
    }
}

class JuegoTriki {
    private char[][] tablero;
    private char jugadorActual;
    private boolean contraMaquina;

    public static final int GANADOR = 1;
    public static final int EMPATE = 0;
    public static final int CONTINUAR = -1;

    public JuegoTriki() {
        tablero = new char[3][3];
        jugadorActual = 'X';
        contraMaquina = JOptionPane.showOptionDialog(null, "¿Contra quién quieres jugar?", "Triki", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Contra otra persona", "Contra la máquina"}, "Contra otra persona") == JOptionPane.NO_OPTION;
        reiniciarJuego();
    }

    public boolean marcarCasilla(int fila, int columna) {
        if (tablero[fila][columna] == '-') {
            tablero[fila][columna] = jugadorActual;
            if (hayGanador()) {
                return true;
            }
            cambiarTurno();
            return true;
        }
        return false;
    }

    public int getEstadoJuego() {
        if (hayGanador()) {
            return GANADOR;
        }
        if (tableroLleno()) {
            return EMPATE;
        }
        return CONTINUAR;
    }

    public boolean hayGanador() {
        // Comprobación de filas y columnas
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] != '-' && tablero[i][0] == tablero[i][1] && tablero[i][0] == tablero[i][2]) {
                return true; // Fila i
            }
            if (tablero[0][i] != '-' && tablero[0][i] == tablero[1][i] && tablero[0][i] == tablero[2][i]) {
                return true; // Columna i
            }
        }

        // Comprobación de diagonales
        if (tablero[1][1] != '-' && ((tablero[0][0] == tablero[1][1] && tablero[1][1] == tablero[2][2]) ||
                (tablero[0][2] == tablero[1][1] && tablero[1][1] == tablero[2][0]))) {
            return true; // Diagonal principal o secundaria
        }

        return false;
    }

    public boolean tableroLleno() {
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                if (tablero[fila][columna] == '-') {
                    return false; // 
                }
            }
        }
        return true; 
    }

    public void cambiarTurno() {
        jugadorActual = (jugadorActual == 'X') ? 'O' : 'X';
    }

    public void reiniciarJuego() {
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                tablero[fila][columna] = '-';
            }
        }
    }

    public void jugarMaquina() {
        // Lógica para que la máquina juegue
        int fila, columna;
        do {
            fila = (int) (Math.random() * 3);
            columna = (int) (Math.random() * 3);
        } while (tablero[fila][columna] != '-');

        tablero[fila][columna] = jugadorActual;
        cambiarTurno();
    }

    public char[][] getTablero() {
        return tablero;
    }

    public char getJugadorActual() {
        return jugadorActual;
    }

    public boolean isContraMaquina() {
        return contraMaquina;
    }
}
