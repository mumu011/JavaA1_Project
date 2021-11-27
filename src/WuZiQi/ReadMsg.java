package WuZiQi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;

public class ReadMsg extends Thread{
    public InputStream m_br;
    public BufferedWriter m_bw;

    public ReadMsg(InputStream is, BufferedWriter bw) {
        m_br = is;
        m_bw = bw;
    }

    @Override
    public void run() {
        super.run();

        byte[] bytes = new byte[4096];
        String content;

         while (true) {
             try {
                 int x = m_br.read(bytes, 0, bytes.length);
                 content = new String(bytes, 0, x);

                 System.out.println(content);
             }
             catch (Exception e) {
                 e.printStackTrace();
             }
        }
    }
}
