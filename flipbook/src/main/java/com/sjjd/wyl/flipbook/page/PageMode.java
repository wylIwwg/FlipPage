package com.sjjd.wyl.flipbook.page;

/**
 * Created by newbiechen on 2018/2/5.
 * 作用：翻页动画的模式
 */

/**
 * SIMULATION 模拟真书翻页效果
 * SLIDE 类似ViewPager效果
 * SCROLL 上下拼接 类似Recyclerview
 * COVER 卡片抽屉效果
 * NONE 直接翻页
 */
public enum PageMode {
    SIMULATION, COVER, SLIDE, NONE, SCROLL
}
