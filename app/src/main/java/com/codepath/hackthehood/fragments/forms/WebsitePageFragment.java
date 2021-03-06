package com.codepath.hackthehood.fragments.forms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.codepath.hackthehood.R;
import com.codepath.hackthehood.fragments.VoiceRecordingDialog;
import com.codepath.hackthehood.models.ImageResource;
import com.codepath.hackthehood.models.PageResource;
import com.codepath.hackthehood.models.User;
import com.codepath.hackthehood.models.Website;
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


public class WebsitePageFragment extends ImageResourceFragment {

    private WebpageFormListener mListener;
    private VoiceRecordingDialog voiceRecordingDialog;
    private EditText etPageText, etDesignerNotes;
    private ImageView ivMic1, ivMic2;
    private List<ImageView> imageViews;
    private List<ImageResource> imageResources;
    private com.codepath.hackthehood.models.WebsitePage page;

    private final static String TITLE = "title";
    private final static String PAGE_INDEX = "pageIndex";

    @InjectView(R.id.btnFillInfo) Button btnFillInfo;
    @InjectView(R.id.btnClearInfo) Button btnClearInfo;

    public static WebsitePageFragment newInstance(String title, int pageIndex) {
        WebsitePageFragment fragment = new WebsitePageFragment();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putInt(PAGE_INDEX, pageIndex);
        fragment.setArguments(args);

        return fragment;
    }

    public WebsitePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void doFetch(boolean onlyIfNeeded) {
        super.doFetch(onlyIfNeeded);

        final int pageIndex = getArguments().getInt(PAGE_INDEX);
        final User user = (User) ParseUser.getCurrentUser();
        incrementNetworkActivityCount();
        ParseGroupOperator.fetchObjectGroupsInBackground(true,
                new ParseIterator() {
                    @Override
                    protected void findNextObject() {

                        if(considerNextObject(user)) return;

                        Website website = user.getWebsite();
                        if(considerNextObject(website)) return;

                        page = website.getWebsitePages().get(pageIndex);
                        if(considerNextObject(page)) return;

                        List<PageResource> pageResources = page.getPageResources();
                        if(considerNextObjects(pageResources.toArray(new ParseObject[pageResources.size()]))) return;

                        if(imageResources == null) {
                            imageResources = new ArrayList<ImageResource>();
                            for (PageResource pageResource : pageResources) {
                                ImageResource imageResource = pageResource.getImageResource();
                                if (imageResource != null)
                                    imageResources.add(imageResource);
                            }
                        }
                        considerNextObjects(imageResources.toArray(new ParseObject[imageResources.size()]));
                    }

                }, new GetCallback() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        setFetchIsFinished();
                        decrementNetworkActivityCount();
                        didReceiveNetworkException(e);

                        if (e == null && etPageText != null)
                            populateView();
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            onAttachFragment(getParentFragment());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_webpage_collection,container,false);
        etPageText = (EditText) v.findViewById(R.id.etPageText);
        etDesignerNotes = (EditText) v.findViewById(R.id.etDesignerNotes);
        ivMic1 = (ImageView) v.findViewById(R.id.ivMic1);
        ivMic2 = (ImageView) v.findViewById(R.id.ivMic2);

        imageViews = new ArrayList<ImageView>();
        imageViews.add((ImageView)v.findViewById(R.id.imgFile1));
        imageViews.add((ImageView)v.findViewById(R.id.imgFile2));
        imageViews.add((ImageView)v.findViewById(R.id.imgFile3));

        setupAddSiteListener((Button)v.findViewById(R.id.btnAddSite));
        for(int c = 0; c < 3; c++)
            setupImageUploadListener(imageViews.get(c), c);

        fetch(true);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Add " + getArguments().getString(TITLE) + " Page Info");

        setupMicListener(ivMic1,1);
        setupMicListener(ivMic2,2);


        ButterKnife.inject(this, v);
        btnFillInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPageText.setText("Here at BERGERON we are a community focusing on the re-circulation of books ( Sci - Fi, Horror, Plausible etc). We want to emphasize on our role with the community and by doing this we are trying to have events 3 nights a week which could include Movie Nights, Game Nights and other events soon to be created. We also have art, drawn by local artists, displayed in the store and we even host rap battles. We want BEREGON to become the anchor of the community for anyone who wants to have a good time.");
                etDesignerNotes.setText("Please place the attached images in a grid layout on the page.");
            }
        });
        btnClearInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPageText.setText("");
                etDesignerNotes.setText("");
            }
        });
        return v;
    }

    private void setupMicListener(ImageView micImg, final int fieldNum) {
        micImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                voiceRecordingDialog = VoiceRecordingDialog.newInstance("Voice Recording",fieldNum);
                voiceRecordingDialog.show(fm, "fragment_audio_recording_dialog");
            }
        });
    }

    private void onAttachFragment(Fragment fragment) {
        try {
            mListener = (WebpageFormListener)fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString()
                    + " must implement WebpageFormListener");
        }
    }

    private void populateView() {
        for(int c = 0; c < 3; c++) {
            String imageUrl = imageResources.get(c).getImageUrl();
            if(imageUrl != null)
                Picasso.with(getActivity()).load(imageUrl).fit().into(imageViews.get(c));
        }

        etDesignerNotes.setText(page.getNotes());
        etPageText.setText(page.getText());
    }

    private void setupAddSiteListener(final Button bntAddSite) {

        bntAddSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bntAddSite.setEnabled(false);
                submit(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        bntAddSite.setEnabled(true);

                        if (e == null && mListener != null) {
                            mListener.onWebpageFormSubmit(getArguments().getInt(PAGE_INDEX));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void submit(final SaveCallback saveCallback) {

        page.setNotes(etDesignerNotes.getText().toString());
        page.setTitle(getArguments().getString(TITLE));
        page.setText(etPageText.getText().toString());

        incrementNetworkActivityCount();
        page.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                decrementNetworkActivityCount();
                didReceiveNetworkException(e);
                saveCallback.done(e);
            }
        });
    }

    @Override
    protected ImageView imageViewForIndex(int index) {
        return imageViews.get(index);
    }

    @Override
    protected ImageResource imageResourceForIndex(int index) {
        return imageResources.get(index);
    }

    public interface WebpageFormListener {
        public void onWebpageFormSubmit(int pageIndex);
    }
}
