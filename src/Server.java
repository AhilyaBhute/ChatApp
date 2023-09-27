import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.io.IOException;
import java.net.*;

public class Server extends JFrame {

    ServerSocket server;
    Socket socket;

    //Reading
    BufferedReader br;
    //Writing
    PrintWriter out;

    // Declaring components
    JLabel heading = new JLabel("Server area");
    JTextArea messageArea = new JTextArea();
    JTextField messageInput = new JTextField();
    JScrollPane scrollPane;
    Font font = new Font("MV Boli",Font.PLAIN,20);

    Server(){
        try {
            server = new ServerSocket(7777);
            System.out.println("Waiting for client...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
//            startWriting();
        } catch (IOException e) {
            System.out.println("Connection closed.");
        }

    }

    private void handleEvents() {

        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == 10){
                    String text = messageInput.getText();
                    messageArea.append("ME: "+ text + "\n");
                    out.println(text);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    private void createGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Server-end");
        this.setSize(400,600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        // coding for component
        heading.setFont(font);
        messageInput.setFont(font);
        messageArea.setFont(font);

        ImageIcon icon = new ImageIcon("image.jpg");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(45,45,Image.SCALE_SMOOTH);
        heading.setIcon(new ImageIcon(scaledImage));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createLineBorder(Color.black));
        messageArea.setEditable(false);
        // layout of frame
        this.setLayout(new BorderLayout());

        //adding component
        this.add(heading,BorderLayout.NORTH);
        scrollPane = new JScrollPane(messageArea);
        this.add(scrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        this.setVisible(true);
    }

    public void startReading(){
        // thread - read the data
        Runnable r1 = ()->{
            System.out.println("Reader started..");
            try{
                while(true){
                    String msg = null;
                    msg = br.readLine();
                    if(msg.equals("exit")){
                        System.out.println("Client terminated the chat.");
                        JOptionPane.showMessageDialog(this,"Client terminated the chat.","Error!",JOptionPane.ERROR_MESSAGE);
                        messageInput.setEnabled(false);
                        break;
                    }
//                    System.out.println("CLIENT: " + msg);
                    messageArea.append("CLIENT: "+msg + "\n");
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }

        };
        new Thread(r1).start();
    }

    public void startWriting(){
        // thread - take data from keyboard and send it to client
        Runnable r2 = ()->{
            System.out.println("Writer started...");
            try{
                while (!socket.isClosed()){
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("Server is ready!!");
        new Server();
    }
}
