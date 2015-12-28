package com.example.alanb.cs492week1;

/**
 * Created by shinjaemin on 2015. 12. 23..
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
         */
        mic = new MyInnerClass(this.getActivity(), view);
        mic.execute("http://comic.naver.com/webtoon/weekdayList.nhn?week=sat");
        return view;
    }
    private class MyInnerClass extends AsyncTask<String, Void, String> {
        String[] res = new String[27];
        String[] res2 = new String[27];
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
                Document doc = Jsoup.connect(urls[0]).get();
                //Elements satWT = doc.select("div.thumb img[src]");
                Elements satWT = doc.select("img[src]");
                String printWT = satWT.toString();
                String[] resWT = printWT.split("\"");
                int index = 0;
                for(String s:resWT)
                    if(s.contains("http://thumb.") && index!=27)
                        res[index++] = s;
                Elements sat2WT = doc.select("a[href*=/webtoon/list.nhn?]");
                String print2WT = sat2WT.toString();
                String[] res2WT = print2WT.split("\"");
                int index2 = 0;
                for(String s:res2WT)
                    if(s.contains("/webtoon/list.nhn?titleId") && index2!=27)
                        res2[index2++] = "http://comic.naver.com"+s;

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
                    Uri uri = Uri.parse(res2[position]);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    micContext.startActivity(intent);
                }

            });
        }
    }
}