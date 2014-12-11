package cn.com.pplo.sicauhelper.ui.fragment;


import android.app.Activity;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.ui.FeedbackActivity;
import cn.com.pplo.sicauhelper.ui.HelpActivity;
import cn.com.pplo.sicauhelper.ui.SettingActivity;
import cn.com.pplo.sicauhelper.ui.UserActivity;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends BaseFragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ListView mDrawerListView;
    private TextView nameTv;
    private TextView sidTv;
    private CircleImageView profileIv;
    private View settingBtn;
    private View helpBtn;
    private View feedbackBtn;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    public static NavigationDrawerFragment newInstance(int position) {
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_SELECTED_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        //取得当前选择项
        mCurrentSelectedPosition = getArguments().getInt(STATE_SELECTED_POSITION);
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate( R.layout.fragment_navigation_drawer, container, false);
        initListView(view);
        return view;
    }

    /**
     * 初始化listView
     *
     */
    private void initListView(View view) {

        mDrawerListView = (ListView) view.findViewById(R.id.navigation_drawer_listView);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem((int) id);
            }
        });
        final int[] icons = {
                R.drawable.ic_event_grey600_24dp,
                R.drawable.ic_desktop_windows_grey600_24dp,
                R.drawable.ic_school_grey600_24dp,
                R.drawable.ic_whatshot_grey600_24dp,
                R.drawable.ic_location_city_grey600_24dp,
                R.drawable.ic_shopping_cart_grey600_24dp,
                R.drawable.ic_group_grey600_24dp
        };
        final String[] titles = {
                getString(R.string.title_cource),
                getString(R.string.title_cource_lab),
                getString(R.string.title_score),
                getString(R.string.title_news),
                getString(R.string.title_classroom),
                getString(R.string.title_market),
                getString(R.string.title_status)
        };

        //增加header
        initHeaderView();
        mDrawerListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return icons.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(getActivity(), R.layout.item_navigation, null);
                TextView tv = (TextView) convertView.findViewById(R.id.navigation_item_tv);
                ImageView iv = (ImageView) convertView.findViewById(R.id.navigation_item_iv);
                tv.setText(titles[position]);
                iv.setImageResource(icons[position]);
                if (position == mCurrentSelectedPosition) {
                    convertView.setBackgroundColor(Color.parseColor("#eeeeee"));
                    tv.setTextColor(Color.parseColor("#212121"));
                } else {
                    tv.setTextColor(Color.parseColor("#757575"));
                    convertView.setBackgroundResource(R.drawable.btn_navigation_item);
                }
                return convertView;
            }
        });
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        //底部按钮
        settingBtn = view.findViewById(R.id.navigation_setting);
        helpBtn = view.findViewById(R.id.navigation_help);
        feedbackBtn = view.findViewById(R.id.navigation_feedback);

        //反馈页面
        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackActivity.startFeedbackActivity(getActivity());
            }
        });

        //设置页面
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.startSettingActivity(getActivity());
            }
        });

        //帮助页面
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpActivity.startHelpActivity(getActivity());
            }
        });
    }

    /**
     * 初始化headerView
     */
    private void initHeaderView() {
        View headerView = getActivity().getLayoutInflater().inflate(R.layout.header_navigation, null, false);
        nameTv = (TextView) headerView.findViewById(R.id.navigation_name_tv);
        sidTv = (TextView) headerView.findViewById(R.id.navigation_sid_tv);
        profileIv = (CircleImageView) headerView.findViewById(R.id.navigation_head_iv);

        final AVUser avUser = SicauHelperApplication.getStudent();
        if (avUser != null) {
            nameTv.setText(avUser.getString(TableContract.TableUser._NAME));
            sidTv.setText(avUser.getString(TableContract.TableUser._SID));
            ImageLoader.getInstance().displayImage(avUser.getAVFile(TableContract.TableUser._PROFILE_URL).getUrl(), profileIv, ImageUtil.getDisplayProfileOption(getActivity()));
        }

        //设置人的背景颜色
//        headerView.findViewById(R.id.navigation_user).setBackgroundResource(ColorUtil.getColorBySchool(getActivity(), avUser.getInt(TableContract.TableUser._SCHOOL))
//        );
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserActivity.startUserActivity(getActivity(), SicauHelperApplication.getStudent().getObjectId());
            }
        });

        mDrawerListView.addHeaderView(headerView, null, false);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
//        if (mDrawerLayout != null) {
//            mDrawerLayout.closeDrawer(mFragmentContainerView);
//        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
//            inflater.inflate(R.menu.global, menu);
//            showGlobalContextActionBar();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getSupportActionBar(getActivity());
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getSupportActionBar(getActivity());
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
