package co.erwan.ineed.ineed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {

    private UiLifecycleHelper uiHelper;
    private RequestQueue mVolleyRequestQueue;
    private static final String TAG = MainFragment.class.getSimpleName();

    private UserActions userActions;
    private User currentUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        userActions = new UserActions(this.getActivity());

        mVolleyRequestQueue = Volley.newRequestQueue(this.getActivity());
        mVolleyRequestQueue.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        final MainFragment _this = this;

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_groups"));
        authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    Log.d("LOGIN", "You are currently logged in as " + user.getName());
                    //_this.routeActivity();
                    _this.getFbUserData();
                } else {
                    Log.d("LOGIN", "You are not logged in.");
                }
            }
        });


        return view;
    }

    private void createUser (JSONObject params) {
        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.create_user);

        JsonObjectRequest createUserRequest = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {

            @Override
           public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                Log.d("createUser", response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("createUser", error.toString());
            }
        });

        mVolleyRequestQueue.add(createUserRequest);

    }

    private void getUser (String userId) {
        String url = this.getResources().getString(R.string.server_path) + this.getResources().getString(R.string.get_user) + userId;

        JsonObjectRequest createUserRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                Log.d("getUser", response.toString());
                if (response.length() == 0) {
                    Log.d("createUser", "EXISTE PAS");

                    /*JSONObject jsonParams = new JSONObject();

                    try {
                        JSONArray jsonUsers = new JSONArray();
                        JSONObject jsonUser = new JSONObject();

                        jsonUser.put("id",currentUser.getId());
                        jsonUser.put("firstname",currentUser.getFirstname());
                        jsonUser.put("picture",currentUser.getPicture());

                        jsonParams.put("user", "Doe");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    createUser(jsonParams);

                    createUser();*/

                    Intent selectGroupsActivity = new Intent(getActivity(), SelectGroupsActivity.class);
                    startActivity(selectGroupsActivity);

                } else {
                    Log.d("createUser", "EXISTE");


               }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("createUser", error.toString());
            }
        });

        mVolleyRequestQueue.add(createUserRequest);

    }

    private void routeActivity() {
        /* TODO : Checker si l'utilisateur est nouveau */
        //Intent intent = new Intent(getActivity(), SelectGroupsActivity.class);
        //startActivity(intent);

        /*JSONObject jsonParams = new JSONObject();

        try {
            JSONArray jsonUsers = new JSONArray();
            JSONObject jsonUser = new JSONObject();

            jsonUser.put("id","");

            jsonParams.put("user", "Doe");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        createUser(jsonParams);*/
    }

    protected void getFbUserData() {
        Session session = Session.getActiveSession();

        Bundle params = new Bundle();
        params.putString("fields", "id,first_name,last_name,name,picture");

        final MainFragment _this = this;

        new com.facebook.Request(
                session,
                "/me",
                params,
                HttpMethod.GET,
                new com.facebook.Request.Callback() {
                    public JSONObject onCompleted(com.facebook.Response response) {
                        GraphObject graphObject = response.getGraphObject();

                        Log.d("graphObjectUSER", graphObject.getProperty("id").toString());

                        FacebookRequestError error = response.getError();

                        if (error == null) {

                            User newUser = new User(Long.parseLong(graphObject.getProperty("id").toString()), graphObject.getProperty("name").toString());
                            newUser.setFirstname(graphObject.getProperty("first_name").toString());

                            JSONObject json_picture = (JSONObject) graphObject.getProperty("picture");
                            try {
                                JSONObject picture_data = json_picture.getJSONObject("data");
                                newUser.setPicture(picture_data.getString("url"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                currentUser = newUser;
                                userActions.setCurrentUser(newUser);

                                getUser(currentUser.getId().toString());
                                //_this.refreshUserData();
                            }

                        }

                        return null;
                    }
                }
        ).executeAsync();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            //MainActivity mainActivity = new MainActivity();
            //mainActivity.onIndex();
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

}
