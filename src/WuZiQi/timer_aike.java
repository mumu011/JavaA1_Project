package WuZiQi;

import javax.swing.*;
import java.util.TimerTask;

public class timer_aike extends TimerTask {
    Board m_board;
    JTextField m_textfield;
    int remain_time;

    public timer_aike(Board board, JTextField textField) {
        m_board = board;
        m_textfield = textField;
        // 每隔一分钟随机落子
        remain_time = 60;
        // 测试
//        remain_time = 10;
        m_textfield.setText(String.valueOf(remain_time) + "s");
    }

    @Override
    public void run() {
        remain_time = remain_time - 1;
        m_textfield.setText(String.valueOf(remain_time) + "s");
        if (remain_time == 0) {
            m_board.Random_luozi();
            remain_time = 60;
//            remain_time = 10;
        }
    }
}
