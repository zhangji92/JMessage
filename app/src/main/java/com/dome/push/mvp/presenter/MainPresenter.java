package com.dome.push.mvp.presenter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dome.push.R;
import com.dome.push.base.BasePresenter;
import com.dome.push.fragment.HomeFragment;
import com.dome.push.fragment.MyFragment;
import com.dome.push.listener.TabSelectedListener;
import com.dome.push.mvp.view.IMainView;
import com.google.android.material.tabs.TabLayout;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.mvp
 * ClassName:      MainPresenter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-10 下午4:15
 * UpdateUser:     更新者
 * UpdateDate:     19-12-10 下午4:15
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class MainPresenter extends BasePresenter<IMainView> {

    // fragment管理器
    private FragmentManager fragmentManager;
    // 当前
    private Fragment currentFragment;
    // 首页
    private HomeFragment homeFragment;
    // 我的
    private MyFragment myFragment;

    public MainPresenter(IMainView reference) {
        super(reference);
        // 判断当前 Fragment 是否为空
        if (currentFragment == null) {
            // 初始化当前fragment
            currentFragment = new Fragment();
        }
        // 判断 Fragment 管理器是否为空
        if (fragmentManager == null) {
            // fragment管理器
            fragmentManager = reference.getBaseActivity().getSupportFragmentManager();
        }
    }

    /**
     * 初始化
     */
    public void initData() {
        // 获取 tab 控件
        TabLayout tab = getView().getTab();
        // 添加 控件内容
        tab.addTab(tab.newTab().setText("首页"), true);
        tab.addTab(tab.newTab().setText("测试"));
        tab.addTab(tab.newTab().setText("购物车"));
        tab.addTab(tab.newTab().setText("我的"));
        // 显示首页
        displayHome();
        // 选中事件
        tab.addOnTabSelectedListener(new TabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        displayHome();
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        displayMy();
                        break;
                }
            }
        });
    }

    /**
     * 显示首页
     */
    private void displayHome() {
        // 判断 homeFragment 是否为空
        if (this.homeFragment == null) {
            // 初始化 homeFragment
            this.homeFragment = HomeFragment.newInstance();
        }
        // 切换到 homeFragment
        switchFragment(this.homeFragment).commit();
    }
    /**
     * 显示我的
     */
    private void displayMy() {
        // 判断 homeFragment 是否为空
        if (this.myFragment == null) {
            // 初始化 homeFragment
            this.myFragment = MyFragment.newInstance();
        }
        // 切换到 homeFragment
        switchFragment(this.myFragment).commit();
    }


    /**
     * 切换fragment
     *
     * @param fragment 锁片
     * @return 返回 FragmentTransaction
     */
    private FragmentTransaction switchFragment(Fragment fragment) {
        // fragment事物
        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        // 判断fragment是否添加
        if (!fragment.isAdded()) {// fragment未添加
            // 当前fragment不为空
            if (this.currentFragment != null) {
                // 隐藏当前fragment
                transaction.hide(this.currentFragment);
            }
            // 添加fragment
            transaction.add(R.id.main_fragment, fragment, fragment.getClass().getName());
        } else {
            // 隐藏当前fragment显示需要fragment
            transaction.hide(this.currentFragment).show(fragment);
        }
        // 给当前fragment赋值
        this.currentFragment = fragment;
        return transaction;
    }

}
