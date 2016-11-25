package nb.scode.bukumigas;

import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import nb.scode.bukumigas.adapter.NavDrawerAdapter;

public class FragmentDrawer extends Fragment{

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    //private RelativeLayout mNavHeaderContainer;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavDrawerAdapter adapter;
    private View containerView;
    //private static String[] titles = null;
    private FragmentDrawerListener drawerListener;

    String navTitles[];
    public TypedArray navIcons;
    // FOR NAVIGATION VIEW ITEM TEXT COLOR
    int[][] state = new int[][] {
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed}  // pressed

    };

    int[] color = new int[] {
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE
    };

    // FOR NAVIGATION VIEW ITEM ICON COLOR
    int[][] states = new int[][] {
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed}  // pressed

    };

    int[] colors = new int[] {
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE
    };
    public FragmentDrawer() {

    }


    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // drawer labels

        navTitles = getActivity().getResources().getStringArray(R.array.navDrawerItems);
        navIcons = getActivity().getResources().obtainTypedArray(R.array.navDrawerIcons);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
//        mNavHeaderContainer = (RelativeLayout) layout.findViewById(R.id.nav_header_container);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        adapter = new NavDrawerAdapter(navTitles, navIcons, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                drawerListener.onDrawerItemSelected(view, position);
                if (position != 0) {
                    mDrawerLayout.closeDrawer(containerView);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {

        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);

                adapter.notifyDataSetChanged();

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                super.onDrawerSlide(drawerView, slideOffset);

                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public void openDrawer() {

        mDrawerLayout.openDrawer(containerView);
    }

    public void closeDrawer() {

        mDrawerLayout.closeDrawer(containerView);
    }

    public boolean isDrawerOpen() {

        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(containerView);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }
}