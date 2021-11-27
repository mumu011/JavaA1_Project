package WuZiQi;

import java.io.*;
import java.net.Socket;

public class Client extends Thread{
    public String m_host;
    public int m_port;
    // socket读端口
    InputStream is;
    // socket写端口
    BufferedWriter bw;
    // 棋盘
    Board m_board;

    public Client(String host, int port, Board board) {
        m_host = host;
        m_port = port;
        m_board = board;
    }

    @Override
    public void run() {
        super.run();

        try {
            Socket socketClient = new Socket(m_host, m_port);

            is = socketClient.getInputStream();
            bw = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));

            ReadMsg readMsgfromserver = new ReadMsg(is, bw, m_board);
            readMsgfromserver.start();
//            readMsgfromserver.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
