package com.thecreator.ClientServer.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by User1 on 05-08-2016.
 */
public class WebserviceOld {
   ///public static final String serviceUrlFirstPart = "http://52.66.108.224/uaawalletservice/";
    public static final String serviceUrlFirstPart = "http://52.66.88.181/uaa-wallet/";

    //http://uaawallet.udmatech.com/
    //http://192.168.2.100/UaaWalletService/  "http://52.66.88.181/UAAWCFTest/"
    //http://52.66.88.181/
    public static final String Soap_action_firstPart = "http://tempuri.org/";
    private boolean isResultVector = false;
    private static final String NAMESPACE = "http://tempuri.org/";
    public final static ArrayList<String> arrStr = new ArrayList<String>();
    Thread networkThread;
    public int noOfAttempts = 3;
    private String checkResponse = "";
    private boolean processComplete = false;
    private ArrayList<Parameters> Send = new ArrayList<>();
    private SQLiteDatabase db;
    Context context;
    String toCheck;


    public String callMethodName(ArrayList<Parameters> Recieved, Context context, String SecondPart, String Method, String Function) {
        String serviceUrlSecondPart = SecondPart;
        String Method_name = Method;
        String Function_Name = Function;
        checkResponse = "";
        Log.d("size is", "" + Recieved.size());

        ArrayList<PropertyInfo> Collection = new ArrayList<PropertyInfo>();
        for (int i = 0; i < Recieved.size(); i++) {
            PropertyInfo pi = new PropertyInfo();
            //  Collection.add(pi);
            if (!Recieved.isEmpty()) {
                Parameters p = Recieved.get(i);
                if (p.valueType.equalsIgnoreCase("int")) {
                    Integer intValue = Integer.parseInt(p.value);
                    pi.setValue(intValue);
                    pi.setName(p.name);
                    pi.setType(Integer.class);
                    Collection.add(pi);
                } else if (p.valueType.equalsIgnoreCase("String")) {
                    pi.setValue(p.value);
                    pi.setName(p.name);
                    pi.setType(String.class);
                    Collection.add(pi);
                } else if (p.valueType.equalsIgnoreCase("Float")) {
                    pi.setValue(p.value);
                    pi.setName(p.name);
                    pi.setType(Float.class);
                    Collection.add(pi);
                }
            }
        }
        try {

            getWCFData(serviceUrlSecondPart, Method_name, Function_Name, Collection, context);

        } catch (Exception e) {
            e.printStackTrace();

        }

        Log.d("execute", checkResponse);
        return checkResponse;
    }

    public synchronized void getWCFData(final String serviceurlsecondPart,
                           final String soap_action_secondPart, final String method_name,
                           final ArrayList<PropertyInfo> properties, Context mContext)
            throws InterruptedException {

        processComplete = false;

        networkThread = new Thread() {

            @Override
            public void run() {


                while (checkResponse.equalsIgnoreCase("")) {

                    try {
                        Object response = null;
                        String URL = serviceUrlFirstPart + serviceurlsecondPart;
                        String SOAP_ACTION = Soap_action_firstPart
                                + soap_action_secondPart;
                        String METHOD_NAME = method_name;
                        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER11);

                        // THIS BLOCK READS THE ARRALIST CONTENTS AND ASSIGNS TO THE
                        // PROPERTY
                        if (properties != null) {
                            for (int i = 0; i < properties.size(); i++) {
                                request.addProperty(properties.get(i));
                                Log.d("exe", request.toString());
                            }
                        }

                        envelope.setOutputSoapObject(request);
                        envelope.dotNet = true;
                        int attempts = 1;
                        response = call(SOAP_ACTION, envelope, URL);
                        processComplete = true;
                        if (response != null) {
                            checkResponse = response.toString();
                            Log.d("exe",checkResponse);
                        }

                    } catch (Exception e) {

                    }
                }
            }
        };
        networkThread.start();
        while (!processComplete) {

        }


    }


    protected Object call(final String soapAction,
                          final SoapSerializationEnvelope envelope, String URL) {
        Object result = null;
        final HttpTransportSE transportSE = new HttpTransportSE(URL);
        Log.d("Tag", "" + transportSE.debug);
        try {

            transportSE.call(soapAction, envelope);
            // Log.d("Check",transportSE.responseDump.toString()) ;
            result = envelope.getResponse();
            String resultData = result.toString();


        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final XmlPullParserException e) {
            // TODO Auto-generated catch block
            System.out.println("Request: " + transportSE.requestDump);
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }


    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }



}






