/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Tech Guy
 */
class marquee extends JPanel implements ActionListener {

    private static final int RATE = 12;
    private final Timer timer = new Timer(1000 / RATE, this);
    public final JLabel label = new JLabel();
    private final int n;
    private final String s;
    private int index;

    public marquee(String s, int n) {
        if (s == null || n < 1) {
            throw new IllegalArgumentException("Null String or n<");
        }
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }

        this.s = sb + s + sb;
        this.n = n;

// label.setFont(new ("Serif", Font.ITALIC, 36));
        label.setText(sb.toString());
        label.setForeground(Color.BLUE);
        label.setFont(new Font("Tahoma", Font.BOLD, 10));
        this.add(label);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        index++;
        if (index > s.length() - n) {
            index = 0;
        }
        label.setText(s.substring(index, index + n));
    }


}
