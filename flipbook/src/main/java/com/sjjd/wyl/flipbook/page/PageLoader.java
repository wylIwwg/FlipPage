package com.sjjd.wyl.flipbook.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import java.util.List;

/**
 * Created by newbiechen on 17-7-1.
 */

public class PageLoader {
    private static final String TAG = "PageLoader";

    // 当前页面的状态
    public static final int STATUS_LOADING = 1;         // 正在加载
    public static final int STATUS_FINISH = 2;          // 加载完成
    public static final int STATUS_ERROR = 3;           // 加载错误 (一般是网络加载情况)
    public static final int STATUS_EMPTY = 4;           // 空数据
    public static final int STATUS_PARING = 5;          // 正在解析 (装载本地数据)
    public static final int STATUS_PARSE_ERROR = 6;     // 本地文件解析错误(暂未被使用)
    public static final int STATUS_CATEGORY_EMPTY = 7;  // 获取到的目录为空
    // 默认的显示参数配置
    private static final int DEFAULT_MARGIN_HEIGHT = 28;
    private static final int DEFAULT_MARGIN_WIDTH = 15;
    private static final int DEFAULT_TIP_SIZE = 25;
    private static final int EXTRA_TITLE_SIZE = 4;

    // 监听器
    protected OnPageChangeListener mPageChangeListener;

    private String RES_DIR = "pages";

    private Context mContext;
    // 页面显示类
    private PageView mPageView;
    // 当前显示的页
    private int mCurPage;

    private int mHiddenPage;


    // 当前章节的页面列表
    private List<Bitmap> mCurPageList;
    // 绘制电池的画笔
    private Paint mBatteryPaint;

    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private Paint mBgPaint;
    // 阅读器的配置选项
    //private ReadSettingManager mSettingManager;
    // 被遮盖的页，或者认为被取消显示的页
    // 存储阅读记录类


    /*****************params**************************/
    // 当前的状态
    protected int mStatus = STATUS_LOADING;

    // 是否打开过章节
    private boolean isFirstOpen = true;
    private boolean isClose;
    // 页面的翻页效果模式
    private PageMode mPageMode = PageMode.SIMULATION;
    // 加载器的颜色主题
    private PageStyle mPageStyle = PageStyle.BG_0;
    //当前是否是夜间模式
    private boolean isNightMode;
    //书籍绘制区域的宽高
    private int mVisibleWidth;
    private int mVisibleHeight;
    //应用的宽高
    private int mDisplayWidth;
    private int mDisplayHeight;
    //间距
    private int mMarginWidth;
    private int mMarginHeight;
    //字体的颜色
    private int mTextColor;
    //当前页面的背景
    private int mBgColor;

    // 当前章
    protected int mCurChapterPos = 0;
    //上一章的记录
    private int mLastChapterPos = 0;

    /*****************************init params*******************************/
    public PageLoader(PageView pageView, List<Bitmap> list) {
        mPageView = pageView;
        mContext = pageView.getContext();
        mCurPageList = list;

        // 初始化画笔
        initPaint();

        // 初始化数据
        initData();

        // 初始化PageView
        initPageView();
    }


    private void initData() {
        // 初始化参数
        mMarginWidth = 20;
        mMarginHeight = 20;


    }

    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeBgImage(Bitmap bitmap, int w, int h) {
        if (bitmap == null)
            return null;

        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    private void initPaint() {

        // 绘制背景的画笔
        mBgPaint = new Paint();
        mTextColor = ContextCompat.getColor(mContext, mPageStyle.getBgColor());
        mBgPaint.setColor(mBgColor);

        // 绘制电池的画笔
        mBatteryPaint = new Paint();
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setDither(true);

        // 初始化页面样式
        // setNightMode(false);
    }

    private void initPageView() {
        //配置参数
        mPageView.setPageMode(mPageMode);
        mPageView.setBgColor(mBgColor);
    }


    /**
     * 翻到上一页
     *
     * @return
     */
    public boolean skipToPrePage() {
        return mPageView.autoPrevPage();
    }

    /**
     * 翻到下一页
     *
     * @return
     */
    public boolean skipToNextPage() {
        return mPageView.autoNextPage();
    }


    /**
     * 翻页动画
     *
     * @param pageMode:翻页模式
     * @see PageMode
     */
    public void setPageMode(PageMode pageMode) {
        mPageMode = pageMode;

        mPageView.setPageMode(mPageMode);

        // 重新绘制当前页
        mPageView.drawCurPage(false);
    }

    /**
     * 设置内容与屏幕的间距
     *
     * @param marginWidth  :单位为 px
     * @param marginHeight :单位为 px
     */
    public void setMargin(int marginWidth, int marginHeight) {
        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        // 如果是滑动动画，则需要重新创建了
        if (mPageMode == PageMode.SCROLL) {
            mPageView.setPageMode(PageMode.SCROLL);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置页面切换监听
     *
     * @param listener
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;

        // 如果目录加载完之后才设置监听器，那么会默认回调
        mPageChangeListener.onCategoryFinish(mCurPageList);
    }

    /**
     * 获取当前页的状态
     *
     * @return
     */
    public int getPageStatus() {
        return mStatus;
    }


    /**
     * 获取章节目录。
     *
     * @return
     */
    public List<Bitmap> getChapterCategory() {
        return mCurPageList;
    }

    /**
     * 获取当前页的页码
     *
     * @return
     */
    public int getPagePos() {
        return mCurPage;
    }

    /**
     * 获取当前章节的章节位置
     *
     * @return
     */
    public int getChapterPos() {
        return mCurChapterPos;
    }

    /**
     * 获取距离屏幕的高度
     *
     * @return
     */
    public int getMarginHeight() {
        return mMarginHeight;
    }


    public void chapterError() {
        //加载错误
        mStatus = STATUS_ERROR;
        mPageView.drawCurPage(false);
    }

    /**
     * 关闭书本
     */
    public void closePage() {
        isClose = true;


        clearList(mCurPageList);

        mCurPageList = null;
        mCurPageList = null;
        mPageView = null;
        mCurPage = 0;
        mHiddenPage = 0;
    }

    private void clearList(List list) {
        if (list != null) {
            list.clear();
        }
    }

    public boolean isClose() {
        return isClose;
    }


    /***********************************default method***********************************************/

    void drawPage(Bitmap bitmap, boolean isUpdate) {
        drawBackground(mPageView.getBgBitmap(), isUpdate);
        if (!isUpdate) {
            drawContent(bitmap);
        }
        //更新绘制
        mPageView.invalidate();
    }

    private void drawBackground(Bitmap bitmap, boolean isUpdate) {
        Canvas canvas = new Canvas(bitmap);

        if (!isUpdate) {
            /****绘制背景****/
            canvas.drawColor(mBgColor);

        } else {
            //擦除区域
            mBgPaint.setColor(mBgColor);
            canvas.drawRect(mDisplayWidth / 2, mDisplayHeight - mMarginHeight +5, mDisplayWidth, mDisplayHeight, mBgPaint);
        }


    }

    private void drawContent(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);

        if (mPageMode == PageMode.SCROLL) {
            canvas.drawColor(mBgColor);
        }
        /******绘制内容****/

        if (mStatus != STATUS_FINISH) {
            //绘制字体
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "正在拼命加载中...";
                    break;
                case STATUS_ERROR:
                    tip = "加载失败(点击边缘重试)";
                    break;
                case STATUS_EMPTY:
                    tip = "文章内容为空";
                    break;
                case STATUS_PARING:
                    tip = "正在排版请等待...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "文件解析错误";
                    break;
                case STATUS_CATEGORY_EMPTY:
                    tip = "目录列表为空";
                    break;
            }


        } else {
            canvas.drawBitmap(mCurPageList.get(mCurPage), 0, 0, null);


        }
    }

    void prepareDisplay(int w, int h) {
        // 获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;

        // 获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2;

        // 重置 PageMode
        mPageView.setPageMode(mPageMode);

        // 如果章节已显示，那么就重新计算页面
        dealLoadPageList();
        // 重新设置文章指针的位置
        mCurPage = 0;
        mHiddenPage = 0;

        mPageView.drawCurPage(false);
    }

    /**
     * 翻阅上一页
     *
     * @return
     */
    boolean prev() {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在上一页
            int index = getPrevPage();
            if (mCurPage == index) {
                return false;
            }
            mHiddenPage = mCurPage;
            mCurPage = index;
            mPageView.drawNextPage();
            return true;
        }

        mPageView.drawNextPage();
        return true;
    }


    /**
     * 翻到下一页
     *
     * @return:是否允许翻页
     */
    boolean next() {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在下一页
            int index = getNextPage();
            if (mCurPage == index) {
                return false;
            }
            mHiddenPage = mCurPage;
            mCurPage = index;
            mPageView.drawNextPage();
            return true;
        }
        mHiddenPage = mCurPage;
        mPageView.drawNextPage();
        return true;
    }


    private void dealLoadPageList() {
        try {
            if (mCurPageList.size() > 0) {
                mStatus = STATUS_FINISH;
            } else {
                mStatus = STATUS_LOADING;
            }
        } catch (Exception e) {
            e.printStackTrace();

            mCurPageList = null;
            mStatus = STATUS_ERROR;
        }

        // 回调
        chapterChangeCallback();
    }

    private void chapterChangeCallback() {
        if (mPageChangeListener != null) {
            mPageChangeListener.onChapterChange(mCurChapterPos);
            mPageChangeListener.onPageCountChange(mCurPageList != null ? mCurPageList.size() : 0);
        }
    }


    // 取消翻页
    void pageCancel() {
        // 假设加载到下一页，又取消了。那么需要重新装载。
        mCurPage = mHiddenPage;
    }


    /**
     * @return:获取初始显示的页面
     */
    private Bitmap getCurPage(int pos) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取上一个页面
     */
    private int getPrevPage() {
        int pos = mCurPage - 1;
        if (pos < 0) {
            return mCurPage;
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        return pos;
    }

    /**
     * @return:获取下一的页面
     */
    private int getNextPage() {
        int pos = mCurPage + 1;
        if (pos >= mCurPageList.size()) {
            return mCurPage;
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        return pos;
    }

    /**
     * @return:获取上一个章节的最后一页
     */
    private int getPrevLastPage() {
        int pos = mCurPageList.size() - 1;

        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }

        return pos;
    }

    /**
     * 根据当前状态，决定是否能够翻页
     *
     * @return
     */
    private boolean canTurnPage() {


        if (mStatus == STATUS_PARSE_ERROR
                || mStatus == STATUS_PARING) {
            return false;
        } else if (mStatus == STATUS_ERROR) {
            mStatus = STATUS_LOADING;
        }
        return true;
    }

    /*****************************************interface*****************************************/

    public interface OnPageChangeListener {
        /**
         * 作用：章节切换的时候进行回调
         *
         * @param pos:切换章节的序号
         */
        void onChapterChange(int pos);

        /**
         * 作用：请求加载章节内容
         *
         * @param requestChapters:需要下载的章节列表
         */
        void requestChapters(List<Bitmap> requestChapters);

        /**
         * 作用：章节目录加载完成时候回调
         *
         * @param chapters：返回章节目录
         */
        void onCategoryFinish(List<Bitmap> chapters);

        /**
         * 作用：章节页码数量改变之后的回调。==> 字体大小的调整，或者是否关闭虚拟按钮功能都会改变页面的数量。
         *
         * @param count:页面的数量
         */
        void onPageCountChange(int count);

        /**
         * 作用：当页面改变的时候回调
         *
         * @param pos:当前的页面的序号
         */
        void onPageChange(int pos);
    }
}
