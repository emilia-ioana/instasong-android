package com.instasong;

import java.io.IOException;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class SelectionFragment extends Fragment implements OnClickListener{

	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private TextView userInfoTextView;
	String a = null;


	@Override
	public View onCreateView(LayoutInflater inflater,
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);

	 View view = inflater.inflate(R.layout.selection, container, false);

	 // Find the user's profile picture custom view
	 profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
	 profilePictureView.setCropped(true);

	 // Find the user's name view
	 userNameView = (TextView) view.findViewById(R.id.selection_user_name);

	 // Check for an open session
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        makeMeRequest(session);
	    }

	    userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);

	    Button b = (Button) view.findViewById(R.id.button);
        b.setOnClickListener(this);

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

	@SuppressWarnings("deprecation")
	private void onSessionStateChange(final Session session, final SessionState state, Exception exception) {
		if (state.isOpened()) {
		    userInfoTextView.setVisibility(View.VISIBLE);
		    makeMeRequest(session);

		    // Request user data and show the results
		    Request.newMeRequest(session, new Request.GraphUserCallback() {

		        @Override
		        public void onCompleted(GraphUser user, Response response) {
		            if (user != null) {
		                // Display the info
		                //  userInfoTextView.setText(session.getAccessToken());
		                  try {
		                	  a= getInfo(session);
		                	  playMp3(a);
							  //userInfoTextView.setText(a);
							//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(a));
							//startActivity(browserIntent);
		                	 /* try {
		  						String urlplay = getInfo(session); // your URL here
		  						MediaPlayer mediaPlayer = new MediaPlayer();
		  						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		  						mediaPlayer.setDataSource(urlplay);
		  						mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
		  						mediaPlayer.start();
		  					}catch (IOException e)  {
		  						// TODO Auto-generated catch block
		  						e.printStackTrace();
		  					}*/
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	
						}

		        }
		    });

	    } else if (state.isClosed()) {
	        userInfoTextView.setVisibility(View.INVISIBLE);
	    }
	}

	private UiLifecycleHelper uiHelper;
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

	private static final int REAUTH_ACTIVITY_CODE = 100;

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
	}



	private String getInfo(final Session session) throws JSONException {

		String token=session.getAccessToken();
		TimeZone tz = TimeZone.getDefault();

		// url to make request
		String rawUrl = "http://95.76.94.168:5000/api/users/suggest?api_secret=i9UDy2WpmN90PoW28Eu1a4Rf&access_token=%s&tz=%s";
		String url = String.format(rawUrl, token, tz.getDisplayName(false, TimeZone.SHORT));

		// JSON Node names
	    final String TYPE = "type"; // Response type; can be 'success' or 'error'
		final String MESSAGE = "message"; // If type='error', this contains info about the error that occured
		final String DATA = "data"; // If type='success', this is a dict containing the response data
		final String NAME = "name"; // Key in 'data'; holds artist name
		final String TITLE = "title"; // Key in 'data': holds song title
		final String SOUNDCLOUD_URL = "soundcloud_url"; // Key in 'data': holds song's soundcloud url
		final String SEARCHED_FOR = "searched_for"; // Key in 'data': holds the search term used to search soundcloud


		// Creating JSON Parser instance
		JSONParser jParser = new JSONParser();

		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url);

		String type, returns = null;
		JSONObject data = new JSONObject();

		try {

			type = json.getString("type");

			//if (type == "success") {
				try {
					data = json.getJSONObject("data");
					returns = data.getString("soundcloud_url");
				} catch (JSONException e) {
					e.printStackTrace();
				}


			//} else {
			//	returns = json.getString("message");
			//}


		} catch (JSONException e) {
		    e.printStackTrace();
		}


		return returns;
	}


	/** Called when the user clicks the Send button */
	 @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	        case R.id.button:

	        	Session session = Session.getActiveSession();
	    	    if (session != null && session.isOpened())
				try {
					a= getInfo(session);
					/*try {
						String urlplay = getInfo(session); // your URL here
						MediaPlayer mediaPlayer = new MediaPlayer();
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						mediaPlayer.setDataSource(urlplay);
						mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
						mediaPlayer.start();
					}catch (IOException e)  {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(a));
				//startActivity(browserIntent);
	    	    playMp3(a);
	            break;
	        }
}
	 
	 public void playMp3(String _link) {
	
		 Session session = Session.getActiveSession();
 	     if (session != null && session.isOpened())
 	    	{
 	    	try{
 	    		try{
 	    			String url = getInfo(session); // your URL here
 	    		    MediaPlayer mediaPlayer = new MediaPlayer();
	  			    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				    mediaPlayer.setDataSource(url);
				    mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
				    mediaPlayer.start();
			 }catch (IOException e)  {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }catch(JSONException e) {
				 e.printStackTrace();
			  }
 	    	}
 	    	finally{ }
	 }
	 }
}
