package WuZiQi;

public class Loc {
    int m_x;
    int m_y;

    public Loc (int x, int y) {
        m_x = x;
        m_y = y;
    }

    @Override
    public boolean equals(Object obj) {
        //判断内存地址
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        //判断是否是同一类型的对象
        if (obj instanceof Loc) {
            //强制转换成Person类型
            Loc per = (Loc) obj;
            //判断他们的属性值
            if (this.m_x == per.m_x && this.m_y == per.m_y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(m_x) + "---" + String.valueOf(m_y);
    }
}
