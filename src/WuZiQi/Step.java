package WuZiQi;

import java.util.ArrayList;

public class Step {
    public int m_step;
    public boolean m_IsBlack;
    public ArrayList<Loc> m_listBlack = new ArrayList<>();
    public ArrayList<Loc> m_listWhite = new ArrayList<>();

    public Step(int step, boolean IsBlack, ArrayList<Loc> list_Black, ArrayList<Loc> list_White) {
        m_step = step;
        m_IsBlack = IsBlack;
        for (Loc loc:
                list_Black) {
            m_listBlack.add(loc);
        }
        for (Loc loc:
                list_White) {
            m_listWhite.add(loc);
        }

        // 注意形参和实参的区别！！！
//        m_listBlack = list_Black;
//        m_listWhite = list_White;
    }
}
