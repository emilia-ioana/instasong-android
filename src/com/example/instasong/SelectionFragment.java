package com.example.instasong;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.facebook.UiLifecycleHelper;

public class SelectionFragment extends Fragment implements OnClickListener, OnTouchListener, OnCompletionListener, OnBufferingUpdateListener{
	
	private static final String TAG = "SelectionFragment";
	//private ProfilePictureView profilePictureView;
	//private TextView userNameView;
	private UiLifecycleHelper uiHelper;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	/*
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    
	    View view = inflater.inflate(R.layout.selection, container, false);

	    // Find the user's profile picture custom view
	    // profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
	    //profilePictureView.setCropped(true);

	    // Find the user's name view
	    //userNameView = (TextView) view.findViewById(R.id.selection_user_name);
	 
	   // Check for an open session
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	       // makeMeRequest(session);
	    }
	    
	    return view;
	}

	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                    // Set the id for the ProfilePictureView
	                    // view that in turn displays the profile picture.
	                    profilePictureView.setProfileId(user.getId());
	                    // Set the Textview's text to the user's name.
	                    userNameView.setText(user.getName());
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	} 
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);
	    }
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}*/
	
	private ImageButton buttonPlayPause;
	private SeekBar seekBarProgress;
	public EditText editTextSongURL;
	
	private MediaPlayer mediaPlayer;
	private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class
	
	private final Handler handler = new Handler();
	
	private boolean intialStage = true;
	private boolean playPause;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    
	    View view = inflater.inflate(R.layout.selection, container, false);
	    //initView();
		buttonPlayPause = (ImageButton) view.findViewById(R.id.ButtonTestPlayPause);
		buttonPlayPause.setOnClickListener(this);
	   
		seekBarProgress = (SeekBar) view.findViewById(R.id.SeekBarTestPlay);	
		seekBarProgress.setMax(99); // It means 100% .0-99
		seekBarProgress.setOnTouchListener(this);
		
		//editTextSongURL = (EditText) view.findViewById(R.id.EditTextSongURL);
		//editTextSongURL.setText(R.string.testsong_20_sec);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		
		return view;
	}
    
    /** This method initialise all the views in project*/
    private void initView() {
		//buttonPlayPause = (ImageButton)getView().findViewById(R.id.ButtonTestPlayPause);
		//buttonPlayPause.setOnClickListener(this);
		
		//seekBarProgress = (SeekBar)getView().findViewById(R.id.SeekBarTestPlay);	
		//seekBarProgress.setMax(99); // It means 100% .0-99
		//seekBarProgress.setOnTouchListener(this);
		//editTextSongURL = (EditText)findViewById(R.id.EditTextSongURL);
		//editTextSongURL.setText(R.string.testsong_20_sec);
		
		//mediaPlayer = new MediaPlayer();
		//mediaPlayer.setOnBufferingUpdateListener(this);
		//mediaPlayer.setOnCompletionListener(this);
	}
 
	/** Method which updates the SeekBar primary progress by current song playing position*/
    private void primarySeekBarProgressUpdater() {
    	seekBarProgress.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100)); // This math construction give a percentage of "was playing"/"song length"
		if (mediaPlayer.isPlaying()) {
			Runnable notification = new Runnable() {
		        public void run() {
		        	primarySeekBarProgressUpdater();
				}
		    };
		    handler.postDelayed(notification,1000);
    	}
    }
 
	@Override
	public void onClick(View v) {
		String url = "http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3";
		editTextSongURL = (EditText)getView().findViewById(R.id.EditTextSongURL);
		editTextSongURL.setText(url);
		
		if(v.getId() == R.id.ButtonTestPlayPause){
			 /** ImageButton onClick event handler. Method which start/pause mediaplayer playing */
			try {
				mediaPlayer.setDataSource(url); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
				mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer. 
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
			
			if(!mediaPlayer.isPlaying()){
				mediaPlayer.start();
				buttonPlayPause.setImageResource(R.drawable.icon_pause);
			}else {
				mediaPlayer.pause();
				buttonPlayPause.setImageResource(R.drawable.icon_play);
			}
			
			primarySeekBarProgressUpdater();
		}
	}
 
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(v.getId() == R.id.SeekBarTestPlay){
			/** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
			if(mediaPlayer.isPlaying()){
		    	SeekBar sb = (SeekBar)v;
				int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
				mediaPlayer.seekTo(playPositionInMillisecconds);
			}
		}
		return false;
	}
 
	@Override
	public void onCompletion(MediaPlayer mp) {
		 /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
		buttonPlayPause.setImageResource(R.drawable.icon_play);
	}
 
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		/** Method which updates the SeekBar secondary progress by current song loading from URL position*/
		seekBarProgress.setSecondaryProgress(percent);
	}
}
