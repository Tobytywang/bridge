package com.happylich.bridge.game.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.happylich.bridge.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentContentThr.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentContentThr#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentContentThr extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentContentThr() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentContentThr.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentContentThr newInstance(String param1, String param2) {
        FragmentContentThr fragment = new FragmentContentThr();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        TextView textView = new TextView(getActivity());
//        textView.setText(R.string.hello_blank_fragment);
//        return textView;
        return inflater.inflate(R.layout.fragment_main_thr, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Button button = (Button)getActivity().findViewById(R.id.login);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Handler handler = new Handler();
//                new HttpThread(handler).start();
//            }
//        });
    }

    public class HttpThread extends Thread {
        private Handler handler;
        private String  message = "";
        public HttpThread(Handler handler) {
            this.handler = handler;
        }
        @Override
        public void run() {
            HttpURLConnection urlConn = null;
            InputStream is = null;
            try {
                URL url = new URL("http://192.168.1.105:8080/login");
                String param = "password=" + URLEncoder.encode("password", "UTF-8")
                        + "&username=" + URLEncoder.encode("admin", "UTF-8");
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setUseCaches(false);
                urlConn.setReadTimeout(5000);
                urlConn.setConnectTimeout(5000);
                urlConn.setRequestProperty("Accept-Charset", "utf-8");
                urlConn.setRequestProperty("contentType", "utf-8");
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
                out.write(param.getBytes());
                out.close();
                out.flush();
                if(urlConn.getResponseCode() == HttpURLConnection.HTTP_OK ||
                        urlConn.getResponseCode() == HttpURLConnection.HTTP_CREATED ||
                        urlConn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                    is = urlConn.getInputStream();
                } else {
                    is = urlConn.getErrorStream();
                }
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String temp = "";
                while ((temp=br.readLine())!=null) {
                    message += temp+"\n";
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (urlConn != null) {
                    urlConn.disconnect();
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
