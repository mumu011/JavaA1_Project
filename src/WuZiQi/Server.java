package WuZiQi;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    // 端口号
    int m_port = 8000;
    // socket读端口
    InputStream is;
    // socket写端口
    BufferedWriter bw;
    // 棋盘
    Board m_board;
    // 网络情况显示
    JTextField m_textfield;

    public Server(int port, Board board, JTextField textField) {
        m_port = port;
        m_board = board;
        m_textfield = textField;
    }

    @Override
    public void run() {
        super.run();

        try {
            ServerSocket serverSocket = new ServerSocket(m_port);
//            System.out.println("server running");
//            JOptionPane.showMessageDialog(null, "Server running");
            m_textfield.setText("server running");

            Socket socket = serverSocket.accept();
//            System.out.println("client "+socket.getInetAddress().getLocalHost()+"connected");
//            JOptionPane.showMessageDialog(null, "client "+socket.getInetAddress().getLocalHost()+"connected");
            m_textfield.setText("client \"+socket.getInetAddress().getLocalHost()+\"connected");

            is = socket.getInputStream();
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            ReadMsg readMsgfromclient = new ReadMsg(is, bw, m_board);
            readMsgfromclient.start();
//            readMsgfromclient.join();
        }
        catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
    }

    public String getport() {
        return String.valueOf(m_port);
    }
}
