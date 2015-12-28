package com.example.alanb.cs492week1;

/**
 * Created by shinjaemin on 2015. 12. 23..
 */
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class TabFragment2 extends Fragment {

    private MyInnerClass mic;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);
        /* adds the appropriate child fragment to this fragment.
         * adds FBLoginFragment if the user has not logged in; otherwise, adds FBShowFragment.
         */
        mic = new MyInnerClass(this.getActivity(), view);
        mic.execute("http://comic.naver.com/webtoon/weekdayList.nhn?week=sat");
        return view;
    }
    private class MyInnerClass extends AsyncTask<String, Void, String> {
        String[] res = new String[31];
        private Context micContext;
        private View micView;

        MyInnerClass (Context context, View view){
            micContext = context;
            micView = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                //Document doc = Jsoup.connect("http://comic.naver.com/webtoon/weekdayList.nhn?week=sat").get();
                Document doc = Jsoup.connect(urls[0]).get();
                Elements satWT = doc.select("img[src$=.jpg]");
                String printWT = satWT.toString();
                String[] resWT = printWT.split(" src=", 33);
                char[] tmp = new char[110];
                String[] tmp2 = new String[2];
                for (int i = 1; i < resWT.length - 1; i++) {
                    System.arraycopy(resWT[i].toCharArray(), 1, tmp, 0, tmp.length);
                    tmp2 = (new String(tmp)).split("\"", 3);
                    res[i - 1] = tmp2[0];
                }
            }catch(IOException ie){
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            GridView gridview = (GridView) micView.findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(micContext, res));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Toast.makeText(micContext, "" + position,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}