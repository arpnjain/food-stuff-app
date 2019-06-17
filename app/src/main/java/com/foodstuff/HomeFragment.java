package com.foodstuff;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joooonho.SelectableRoundedImageView;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements HomeItemClickListener {
    private FirebaseAuth.AuthStateListener authListener;

    private FirebaseAuth auth;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Item>  blogsList_item = new ArrayList<>();
    private StoreAdapter mAdapter;
    Boolean isScrolling= false;
    int currentItems, totalItems, scrollItems;
    String token ="";
    SpinKitView progress;
    private GridLayoutManager mLayoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();

        //get current user
        final  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    Intent mainIntent = new Intent(getActivity(), SignupActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    getActivity().finish();

                }
            }
        };
        Toolbar toolbar=(Toolbar)view.findViewById(R.id.toolbar);
        toolbar.setTitle("Blogs");


        toolbar.inflateMenu(R.menu.main_menu);
        Menu menu = toolbar.getMenu();

        setHasOptionsMenu(true);
       toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                   // case R.id.settings:
                     //   Log.i("Menu item Selected", "Settings");
                       // return true;
                    case R.id.help:
                        Intent Email = new Intent(Intent.ACTION_SEND);
                        Email.setType("text/email");
                        Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "sdcetfoodstuff2019@gmail.com" });
                        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        Email.putExtra(Intent.EXTRA_TEXT, "User Email Id: ");
                        startActivity(Intent.createChooser(Email, "Send Feedback:"));
                        Log.i("Menu item Selected", "Help");
                        return true;
                    case R.id.logout:
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Warning!")
                                .setMessage("Are you sure you want to Logout ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.i("Menu item Selected", "Log Out");
                                        signOut();

                                    }
                                })
                                .setNegativeButton("No",null)
                                .show();
                        return true;
                }
                return false;
            }
        });

        DrawerLayout drawer = (DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        progress =(SpinKitView) view.findViewById(R.id.spin_kit);
        recyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(getActivity(),1);
        mAdapter = new StoreAdapter(getActivity(), blogsList_item);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setHomeItemClickListener(this);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling= true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems= mLayoutManager.getChildCount();
                totalItems = mLayoutManager.getItemCount();
                scrollItems=  mLayoutManager.findFirstCompletelyVisibleItemPosition();

                if(isScrolling && ( currentItems + scrollItems == totalItems ))
                {
                   isScrolling= false;
                   fetchBlogsItems();
                }
            }
        });
        fetchBlogsItems();
        return view;
    }

    private void signOut() {
        auth.signOut();
        Intent mainIntent = new Intent(getActivity(), SignupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        getActivity().finish();

    }
    private void fetchBlogsItems()
    {

        String url= BloggerAPI.url + "?key=" + BloggerAPI.key;
        if(token != "")
        {
            url= url+ "&pageToken=" + token;
        }
        if(token == null)
        {
            return;
        }
        progress.setVisibility(View.VISIBLE);
        Call<PostList> postList = BloggerAPI.getService().getPostList(url);
        postList.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {

                PostList list = response.body();
                token = list.getNextPageToken();
                blogsList_item.addAll(list.getItems());
                // refreshing recycler view
                mAdapter.notifyDataSetChanged();
                Log.i("HomeFragment","Success");
               // Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                Toast.makeText(getContext(),"Error Occured",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        final Item blogs = blogsList_item.get(position);
        Intent intent = new Intent(getActivity(), PostWebView.class);
        intent.putExtra("url", blogs.getUrl());
        startActivity(intent);
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {


        private int spacing;


        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {

            this.spacing = spacing;

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int itemCount = state.getItemCount();
            outRect.right = spacing;
            outRect.left = spacing;
            outRect.top = spacing;
            outRect.bottom = position == itemCount - 1 ? spacing : 0;
        }
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
