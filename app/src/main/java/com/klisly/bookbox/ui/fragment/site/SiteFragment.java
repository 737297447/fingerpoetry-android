package com.klisly.bookbox.ui.fragment.site;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.klisly.bookbox.BusProvider;
import com.klisly.bookbox.R;
import com.klisly.bookbox.adapter.PagerFragmentAdapter;
import com.klisly.bookbox.listener.OnDataChangeListener;
import com.klisly.bookbox.logic.AccountLogic;
import com.klisly.bookbox.logic.SiteLogic;
import com.klisly.bookbox.model.Site;
import com.klisly.bookbox.ottoevent.ToLoginEvent;
import com.klisly.bookbox.ui.base.BaseMainFragment;
import com.klisly.bookbox.utils.ToastHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class SiteFragment extends BaseMainFragment implements Toolbar.OnMenuItemClickListener {
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static SiteFragment newInstance() {
        return new SiteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SiteLogic.getInstance().unRegisterListener(this);
        ButterKnife.unbind(this);
    }

    @Override
    protected FragmentAnimator onCreateFragmentAnimation() {
        // 默认不改变
//         return super.onCreateFragmentAnimation();
        // 在进入和离开时 设定无动画
        return new DefaultNoAnimator();
    }

    private void initView() {
        mToolbar.setTitle(R.string.site);
        initToolbarNav(mToolbar);
        mToolbar.setOnMenuItemClickListener(this);
        updateItems();
        PagerFragmentAdapter adapter = new PagerFragmentAdapter(getChildFragmentManager(),
                SiteLogic.getInstance().getOpenFocuses());
        mViewPager.setAdapter(adapter);
        SiteLogic.getInstance().registerListener(this, new OnDataChangeListener() {
            @Override
            public void onDataChange() {
                adapter.notifyDataSetChanged();
                updateItems();
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void updateItems() {
        if(SiteLogic.getInstance().getOpenFocuses() != null){
            mTabLayout.removeAllTabs();
            for(Site site : SiteLogic.getInstance().getOpenFocuses()){
                if(mTabLayout != null) {
                    mTabLayout.addTab(mTabLayout.newTab().setText(site.getName()));
                }
            }
        }
    }

    /**
     * 类似于 Activity的 onNewIntent()
     */
    @Override
    protected void onNewBundle(Bundle args) {
        super.onNewBundle(args);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more:
                final PopupMenu popupMenu = new PopupMenu(_mActivity, mToolbar, GravityCompat.END);
                popupMenu.inflate(R.menu.menu_site_pop);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
//                            case R.id.action_find_site:
//                                ToastHelper.showShortTip(R.string.find_site);
//                                break;
                            case R.id.action_manage_site:
                                if(!AccountLogic.getInstance().isLogin()){
                                    BusProvider.getInstance().post(new ToLoginEvent());
                                } else {
                                    start(ChooseSiteFragment.newInstance(ChooseSiteFragment.ACTION_MANAGE));
                                }
                                break;
                            case R.id.action_sort_method:
                                ToastHelper.showShortTip(R.string.sort_method);
                                break;
                            case R.id.action_notify_setting:
                                ToastHelper.showShortTip(R.string.notify_setting);
                                break;
                            default:
                                break;
                        }
                        popupMenu.dismiss();
                        return true;
                    }
                });
                popupMenu.show();
                break;
            default:
                break;
        }
        return true;
    }
}