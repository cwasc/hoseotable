package devflow.hoseotable;

/**
 * ScrollView의 스크롤이벤트를 위한 인터페이스입니다.
 * @author 안계성
 *
 */
public interface ScrollViewListener {

    void onScrollChanged(TwoDScrollView scrollView, int x, int y, int oldx, int oldy);
    //여기선 TwoDScrollView를 사용합니다.

}