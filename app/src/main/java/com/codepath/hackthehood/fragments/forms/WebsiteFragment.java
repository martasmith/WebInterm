package com.codepath.hackthehood.fragments.forms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.codepath.hackthehood.R;
import com.codepath.hackthehood.models.ImageResource;
import com.codepath.hackthehood.models.User;
import com.codepath.hackthehood.models.Website;
import com.codepath.hackthehood.models.WebsitePage;
import com.codepath.hackthehood.util.ParseGroupOperator;
import com.codepath.hackthehood.util.ParseIterator;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WebsiteFragment extends ImageResourceFragment implements View.OnClickListener {

    private WebsiteInfoListener mListener;

    private EditText etFacebookLink, etYelpLink, etTwitterLink, etInstagramLink;
    private Spinner sprBusinessType;
    private ImageView ivHeader, ivLogo;
    private List<ImageView> checkPages;
    private Button btnSubmit;
    private ArrayList<String> mPageTitles;

    @InjectView(R.id.btnFillInfo) Button btnFillInfo;
    @InjectView(R.id.btnClearInfo) Button btnClearInfo;

    public WebsiteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAttachFragment(getParentFragment());
        mPageTitles = new ArrayList<String>();
        mPageTitles.add("Home");
        mPageTitles.add("Content");
        mPageTitles.add("Contact");
    }

    @Override
    public void doFetch(boolean onlyIfNeeded) {
        super.doFetch(onlyIfNeeded);

        final User user = (User) ParseUser.getCurrentUser();
        incrementNetworkActivityCount();
        ParseGroupOperator.fetchObjectGroupsInBackground(true,
                new ParseIterator() {
                    @Override
                    protected void findNextObject() {
                        if (considerNextObject(user)) return;

                        Website website = user.getWebsite();
                        if (considerNextObject(website)) return;

                        ImageResource imageResources[] = {website.getLogo(), website.getHeader()};
                        considerNextObjects(imageResources);
                    }

                },
                new GetCallback() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        setFetchIsFinished();
                        decrementNetworkActivityCount();
                        didReceiveNetworkException(e);

                        if (e == null && etFacebookLink != null)
                            populateView();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_asset_collection, container, false);

        sprBusinessType = (Spinner) v.findViewById(R.id.sprBusinessType);
        etFacebookLink = (EditText) v.findViewById(R.id.etFacebookLink);
        etYelpLink = (EditText) v.findViewById(R.id.etYelpLink);
        etTwitterLink = (EditText) v.findViewById(R.id.etTwitterLink);
        etInstagramLink = (EditText) v.findViewById(R.id.etInstagramLink);
        ivLogo = (ImageView) v.findViewById(R.id.imgLogo);
        ivHeader = (ImageView) v.findViewById(R.id.imgHeader);

        List<Button> btnPages = new ArrayList<Button>();
        btnPages.add((Button) v.findViewById(R.id.btnPage1));
        btnPages.add((Button) v.findViewById(R.id.btnPage2));
        btnPages.add((Button) v.findViewById(R.id.btnPage3));

        if (checkPages == null) {
            checkPages = new ArrayList<ImageView>();
            checkPages.add((ImageView) v.findViewById(R.id.checkPage1));
            checkPages.add((ImageView) v.findViewById(R.id.checkPage2));
            checkPages.add((ImageView) v.findViewById(R.id.checkPage3));
        }

        if (btnSubmit == null) {
            btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
        }

        // TODO - Need to disable this and investigate why the checkmarks are not showing up
        btnSubmit.setEnabled(true);
        btnSubmit.setOnClickListener(this);

        for (int c = 0; c < btnPages.size(); c++) {
            setupPageCreationListener(btnPages.get(c), c);
        }

        setupImageUploadListener(ivHeader, 0);
        setupImageUploadListener(ivLogo, 1);

        fetch(true);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Add Website Info");

        ButterKnife.inject(this, v);
        btnFillInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etFacebookLink.setText("https://www.facebook.com/BergeronsBooks/info");
                etYelpLink.setText("http://www.yelp.com/biz/bergerons-books-oakland");
                etTwitterLink.setText("@bergerons");
            }
        });
        btnClearInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etFacebookLink.setText("");
                etYelpLink.setText("");
                etTwitterLink.setText("");
            }
        });
        return v;
    }

    @Override
    protected ImageView imageViewForIndex(int index) {
        return (index == 1) ? ivLogo : ivHeader;
    }

    @Override
    protected ImageResource imageResourceForIndex(int index) {
        User user = (User) ParseUser.getCurrentUser();
        Website website = user.getWebsite();
        return (index == 1) ? website.getLogo() : website.getHeader();
    }

    private void setupPageCreationListener(final Button btn, final int pageIndex) {
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.collectPageInfo(mPageTitles.get(pageIndex), pageIndex);
                }
            }
        });
    }

    private void populateView() {
        User user = (User) ParseUser.getCurrentUser();
        Website website = user.getWebsite();

        for (int i = 0; i < website.getWebsitePages().size(); i++) {
            WebsitePage page = website.getWebsitePages().get(i);
            if (!TextUtils.isEmpty(page.getTitle())) {
                checkPages.get(i).setAlpha(0.0f);
                checkPages.get(i).setVisibility(View.VISIBLE);
                checkPages.get(i).animate().alpha(1.0f).setInterpolator(new DecelerateInterpolator());
            }
        }

        // TODO : type of business
//        sprBusinessType.setSel
//        website.setTypeOfBusiness(sprBusinessType.getSelectedItem().toString());

        etFacebookLink.setText(website.getFacebookUrl());
        etYelpLink.setText(website.getYelpUrl());
        etTwitterLink.setText(website.getTwitterUrl());
        etInstagramLink.setText(website.getInstagramUrl());

        String headerImageUrl = website.getHeader().getImageUrl();
        if (headerImageUrl != null)
            Picasso.with(getActivity()).load(headerImageUrl).fit().into(ivHeader);

        String logoImageUrl = website.getLogo().getImageUrl();
        if (logoImageUrl != null)
            Picasso.with(getActivity()).load(logoImageUrl).fit().into(ivLogo);
    }

    @Override
    public void submit(final SaveCallback saveCallback) {

        //get current user
        User user = (User) ParseUser.getCurrentUser();
        Website website = user.getWebsite();
        website.setTypeOfBusiness(sprBusinessType.getSelectedItem().toString());
        website.setFacebookUrl(etFacebookLink.getText().toString());
        website.setYelpUrl(etYelpLink.getText().toString());
        website.setTwitterUrl(etTwitterLink.getText().toString());
        website.setInstagramUrl(etInstagramLink.getText().toString());

        ParseObject objectsToSave[] = {user, website};
        incrementNetworkActivityCount();
        ParseGroupOperator.saveObjectsInBackgroundInParallel(objectsToSave,
                new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        decrementNetworkActivityCount();
                        didReceiveNetworkException(e);
                        saveCallback.done(e);
                    }
                });

    }

    public void collectedInfoForPage(int pageIndex) {
        checkPages.get(pageIndex).setVisibility(View.VISIBLE);
        checkAllWebpagesVisited();
    }

    public void checkAllWebpagesVisited() {
        // while none of the input fields are required, check to make sure the user visited all website page views
        // before we allow them to submit
        btnSubmit.setEnabled(true);
        for (ImageView checkPage : checkPages) {
            if (checkPage.getVisibility() != View.VISIBLE)
                btnSubmit.setEnabled(false);
        }
    }

    private void onAttachFragment(Fragment fragment) {
        try {
            mListener = (WebsiteInfoListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString()
                    + " must implement AssetFormListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

        User user = (User) ParseUser.getCurrentUser();

        incrementNetworkActivityCount();
        user.setApplicationStatus(User.APPSTATUS_ASSETS_SUBMITTED);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                decrementNetworkActivityCount();
                didReceiveNetworkException(e);

                if (e == null && mListener != null)
                    mListener.onWebsiteInfoFormSubmit();
            }
        });
    }

    public interface WebsiteInfoListener {
        public void collectPageInfo(String title, int pageIndex);
        public void onWebsiteInfoFormSubmit();
    }
}
