package nb.scode.bukumigas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nb.scode.bukumigas.adapter.ItemsListAdapter;
import nb.scode.bukumigas.app.App;
import nb.scode.bukumigas.constants.Constants;
import nb.scode.bukumigas.model.Item;
import nb.scode.bukumigas.util.CustomRequest;
import nb.scode.bukumigas.util.Helper;

public class FavoritesFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    RecyclerView mRecyclerView;
    TextView mMessage;

    SwipeRefreshLayout mItemsContainer;

    private ArrayList<Item> itemsList;
    private ItemsListAdapter itemsAdapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    int pastVisiblesItems = 0, visibleItemCount = 0, totalItemCount = 0;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new ItemsListAdapter(getActivity(), itemsList);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");

            viewMore = savedInstanceState.getBoolean("viewMore");

        } else {

            itemsList = new ArrayList<>();
            itemsAdapter = new ItemsListAdapter(getActivity(), itemsList);

            restore = false;
            itemId = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        mMessage = (TextView) rootView.findViewById(R.id.message);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Helper.getGridSpanCount(getActivity()));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(Helper.getGridSpanCount(getActivity()), dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (!loadingMore)
                    {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && (viewMore) && !(mItemsContainer.isRefreshing()))
                        {
                            loadingMore = true;
                            Log.e("...", "Last Item Wow !");

                            getItems();
                        }
                    }
                }
            }
        });

        itemsAdapter.setLikesInterface(new ItemsListAdapter.likes() {
            @Override
            public void onClick(View view, int position) {
                setLike(itemsList.get(position), position);
            }
        });

        itemsAdapter.setCardAct(new ItemsListAdapter.cardact() {
            @Override
            public void onClick(View view, int position) {
                Item i = itemsList.get(position);
                Intent intent = new Intent(getActivity(),ViewBookActivity.class);
                intent.putExtra("pathurl", i.getContent());
                startActivity(intent);
            }
        });

        if (itemsAdapter.getItemCount() == 0) {

            showMessage(getText(R.string.label_empty_list).toString());

        } else {

            hideMessage();
        }

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }

        return rootView;
    }

    private void setLike(final Item item, final int pos){

        if (App.getInstance().isConnected()) {

            if (App.getInstance().getId() != 0) {

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_LIKE, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (!response.getBoolean("error")) {

                                        item.setLikesCount(response.getInt("likesCount"));
                                        item.setMyLike(response.getBoolean("myLike"));
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                } finally {
                                    itemsAdapter.notifyItemChanged(pos);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity(), getString(R.string.error_data_loading), Toast.LENGTH_LONG).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accountId", Long.toString(App.getInstance().getId()));
                        params.put("accessToken", App.getInstance().getAccessToken());
                        params.put("itemId", Long.toString(item.getId()));
                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);

            }

        } else {

            Toast.makeText(getActivity(), getText(R.string.msg_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("viewMore", viewMore);
        outState.putBoolean("restore", true);
        outState.putInt("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;
            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ITEM_EDIT && resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("position", 0);

            Item item = itemsList.get(position);

            item.setContent(data.getStringExtra("post"));
            item.setImgUrl(data.getStringExtra("imgUrl"));

            itemsAdapter.notifyDataSetChanged();

        }
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_FAVORITES_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!loadingMore) {

                            itemsList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            Item item = new Item(itemObj);

                                            itemsList.add(item);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingComplete();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accountId", Long.toString(App.getInstance().getId()));
                params.put("accessToken", App.getInstance().getAccessToken());
                params.put("clientId", CLIENT_ID);

                params.put("itemId", Integer.toString(itemId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength == LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        if (mRecyclerView.getAdapter().getItemCount() == 0) {

            if (isAdded()) {

                showMessage(getText(R.string.label_empty_list).toString());
            }

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {

            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {

                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge

                    outRect.top = spacing;
                }

                outRect.bottom = spacing; // item bottom

            } else {

                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)

                if (position >= spanCount) {

                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {

        Resources r = getResources();

        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}