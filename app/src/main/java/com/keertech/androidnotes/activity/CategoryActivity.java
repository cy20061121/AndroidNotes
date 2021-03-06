package com.keertech.androidnotes.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.keertech.androidnotes.R;
import com.keertech.androidnotes.activity.base.AbstractTopBarActivity;
import com.keertech.androidnotes.adapter.CategoryFragmentPagerAdapter;
import com.keertech.androidnotes.bean.Category;
import com.keertech.androidnotes.bean.ChildCategory;
import com.keertech.androidnotes.util.DbOperationManager;
import com.keertech.androidnotes.view.tabview.SlidingTabLayout;
import com.yftools.LogUtil;
import com.yftools.ViewUtil;
import com.yftools.db.sqlite.Selector;
import com.yftools.exception.DbException;
import com.yftools.view.annotation.ContentView;
import com.yftools.view.annotation.ViewInject;

import java.util.List;

/**
 * *****************************************
 * Description ：
 * Created by cywf on 2015/1/6.
 * *****************************************
 */
@ContentView(R.layout.activity_category)
public class CategoryActivity extends AbstractTopBarActivity {

    @ViewInject(R.id.sliding_tabs)
    private SlidingTabLayout slidingTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtil.inject(this);
        category = (Category) getIntent().getSerializableExtra("category");
        setTitle(category.name);
        initData();
    }

    private void initData() {
        try {
            List<ChildCategory> childCategoryList = DbOperationManager.getInstance().getBeans(Selector.from(ChildCategory.class).where("parentId", "=", category.id));
            viewPager.setAdapter(new CategoryFragmentPagerAdapter(getSupportFragmentManager(), childCategoryList));
           // slidingTabLayout.setCustomTabView(R.layout.tab_view, 0);
            slidingTabLayout.setTabViewTextBackground(R.drawable.bg_tab_click);
            slidingTabLayout.setViewPager(viewPager);
            //slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
            slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return Color.WHITE;
                }

                @Override
                public int getDividerColor(int position) {
                    return 0xffcdcdcd;
                }
            });
        } catch (DbException e) {
            LogUtil.e(e);
        }
    }

}
