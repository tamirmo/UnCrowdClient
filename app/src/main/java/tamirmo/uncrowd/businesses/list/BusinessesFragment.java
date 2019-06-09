package tamirmo.uncrowd.businesses.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import tamirmo.uncrowd.location.LocationHandler;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.data.Business;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnBusinessListItemClickedListener}
 * interface.
 */
public class BusinessesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private OnBusinessListItemClickedListener mListener;
    private BusinessRecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BusinessesFragmentViewModel model;
    private RecyclerView recyclerView;
    private TextView emptyListTextView;
    private FrameLayout mainFrame;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BusinessesFragment() {
    }

    @SuppressWarnings("unused")
    public static BusinessesFragment newInstance(int columnCount) {
        return new BusinessesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_list, container, false);

        recyclerView = view.findViewById(R.id.list);

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        this.adapter = new BusinessRecyclerViewAdapter(mListener);
        recyclerView.setAdapter(adapter);

        emptyListTextView = view.findViewById(R.id.empty_businesses_list_text_view);
        mainFrame = view.findViewById(R.id.main_frame_layout);

        // Assuming all is good and the list is not empty
        showFullListViews();

        return view;
    }

    private void showEmptyListViews(){
        mainFrame.bringChildToFront(emptyListTextView);
        emptyListTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void showFullListViews(){
        mainFrame.bringChildToFront(recyclerView);
        emptyListTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() != null){
            // TODO: Leave this ?
            swipeRefreshLayout.setRefreshing(true);

            this.model =
                    ViewModelProviders.of(getActivity()).get(BusinessesFragmentViewModel.class);

            model.getBusinesses().observe(getActivity(), new Observer<List<Business>>() {
                @Override
                public void onChanged(@Nullable List<Business> businesses) {
                    if(adapter != null){
                        adapter.setBusinesses(businesses);
                    }

                    // No more refreshing
                    swipeRefreshLayout.setRefreshing(false);

                    // If the list of businesses is empty
                    if(businesses == null){
                        emptyListTextView.setText(R.string.empty_businesses_list_error_text);
                        showEmptyListViews();
                    }else if(businesses.isEmpty()){
                        emptyListTextView.setText(R.string.empty_businesses_list_text);
                        showEmptyListViews();
                    }else{
                        showFullListViews();
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBusinessListItemClickedListener) {
            mListener = (OnBusinessListItemClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBusinessListItemClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onSearchPhraseEntered(String searchPhrase){
        adapter.onSearchPhraseEntered(searchPhrase);
    }

    @Override
    public void onRefresh() {

        if(this.model != null) {
            model.refresh();
        }
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }, 1000);*/

    }

    public void sortByLocation(LocationHandler locationHandler) {
        model.sortByLocation(locationHandler);
    }

    public void sortByCrowd() {
        model.sortByCrowd();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBusinessListItemClickedListener {
        void onBusinessListItemClicked(Business item);
    }
}
