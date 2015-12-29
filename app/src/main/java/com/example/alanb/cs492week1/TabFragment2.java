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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        private WebView wv;

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
            final LinearLayout imageV = (LinearLayout) micView.findViewById(R.id.linearlayout1);
            final LinearLayout webV = (LinearLayout) micView.findViewById(R.id.linearlayout2);
            final Button backButton = (Button) micView.findViewById(R.id.buttonBack);
            webV.setVisibility(View.GONE);

            GridView gridview = (GridView) micView.findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(micContext, res));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    imageV.setVisibility(View.GONE);
                    webV.setVisibility(View.VISIBLE);
                    wv = (WebView) micView.findViewById(R.id.webview);
                    wv.setWebViewClient(new WebClient());
                    WebSettings set = wv.getSettings();
                    set.setJavaScriptEnabled(true);
                    wv.loadUrl(res2[position]);
                    set.setCacheMode((WebSettings.LOAD_NO_CACHE));
                    set.setSupportZoom(false);
                    backButton.setVisibility(View.VISIBLE);
                }

            });
            backButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view){
                    if(wv.canGoBack())
                        wv.goBack();
                    else {
                        imageV.setVisibility(View.VISIBLE);
                        webV.setVisibility(View.GONE);
                    }
                }
            });
        }

    }
    class WebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }
    }
}