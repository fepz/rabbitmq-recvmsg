import javax.swing.*;
import java.awt.event.*;

import com.rabbitmq.client.*;

public class mainWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonRcv;
    private JButton buttonExit;
    private JTextField urlTextField;
    private JTextField msgTextField;
    private JTextField statusTextField;
    private ConnectionFactory factory;
    private final static String QUEUE_NAME = "prueba";

    public mainWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonRcv);

        buttonRcv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRcv();
            }
        });

        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        // call onExit() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onExit() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        factory = new ConnectionFactory();
    }

    private void onRcv() {
        statusTextField.setText("Receiving...");
        try {
            factory.setUri(urlTextField.getText());
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            GetResponse msg = channel.basicGet(QUEUE_NAME, true);

            if (msg != null) {
                msgTextField.setText(new String(msg.getBody()));
                statusTextField.setText("Message received.");
            } else {
                msgTextField.setText("");
                statusTextField.setText("The queue is empty.");
            }

        } catch (Exception e) {
            statusTextField.setText(e.toString());
        }
    }

    private void onExit() {
        dispose();
    }

    public static void main(String[] args) {
        mainWindow dialog = new mainWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
