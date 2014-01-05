package com.instasong;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
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
							 // userInfoTextView.setText(a);
							//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(a));
							//startActivity(browserIntent);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}}

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


		// url to make request
		String rawUrl = "http://instasong-python.herokuapp.com/api/users/suggest?api_secret=i9UDy2WpmN90PoW28Eu1a4Rf&access_token=%s";
		String url = String.format(rawUrl, token);

		// JSON Node names
	    final String TYPE = "type";
		final String MESSAGE = "message";
		final String DATA = "data";
		final String TITLE = "title";
		final String PERMALINK_URL = "permalink_url";
		final String SEARCHED_FOR = "searched_for";


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
					returns = data.getString("permalink_url");
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
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(a));
				startActivity(browserIntent);

	            break;
	        }
}

}
