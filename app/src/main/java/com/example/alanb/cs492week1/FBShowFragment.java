package com.example.alanb.cs492week1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alanb on 12/26/2015.
 */
public class FBShowFragment extends Fragment
{
    public final static String TAG = "FBShowFragment";
    private ListView m_fbItemView;
    private SimpleAdapter m_fbItemAdapter;
    private ArrayList<String> m_keyStrings;
    private List<Map<String, String>> m_values;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fb_show, container, false);

        m_keyStrings = new ArrayList<>();
        m_keyStrings.add("name");
        m_keyStrings.add("birthday");
        m_keyStrings.add("gender");
        m_keyStrings.add("email");

        m_values = new ArrayList<>();
        m_fbItemAdapter = new SimpleAdapter(getActivity().getApplicationContext(),
                m_values,
                R.layout.fb_list_item,
                new String[] {"Field", "Value"},
                new int[] {R.id.text1, R.id.text2});
        m_fbItemView = (ListView) view.findViewById(R.id.FBItemView);
        m_fbItemView.setAdapter(m_fbItemAdapter);

        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                for (String field: m_keyStrings) {
                    String value = new String();

                    try {
                        value = object.getString(field);
                        Log.d(TAG, "field=" + field + " value=" + value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Map<String, String> datum = new HashMap<>(2);
                    datum.put("Field", field);
                    datum.put("Value", value);
                    m_values.add(datum);
                }
                m_fbItemAdapter.notifyDataSetChanged();
            }
        });

        Bundle params = new Bundle();
        StringBuilder stringBuilder = new StringBuilder();
        for (String field: m_keyStrings)
        {
            stringBuilder.append(field);
            stringBuilder.append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        params.putString(GraphRequest.FIELDS_PARAM, stringBuilder.toString());

        Log.d(TAG, "request param=" + stringBuilder.toString());

        request.setParameters(params);
        request.executeAsync();

        return view;
    }
}
