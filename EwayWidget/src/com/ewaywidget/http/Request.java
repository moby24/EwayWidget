package com.ewaywidget.http;
import java.io.*;
import java.net.URLDecoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Patterns;
import com.ewaywidget.WidgetFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Request {
    public static final String SERVER_URL = "https://app.eway.in.ua";
    public static final String LOGIN = "app-android-w-21014";
    public static final String PASSWORD = "hNeb5L9vep";
    public static final String VERSION = "1.2";
    public static final String CHARSET = "UTF-8";
    Context context ; 
    @SuppressLint("UseSparseArrays")
    static HashMap<Integer, Integer> KEYS = new HashMap<Integer, Integer>();

    public Request() {
    	
    }

    public Response execute(String stopId) {
        KEYS.put(0, 2);
        KEYS.put(1, 1);
        KEYS.put(2, -4);
        KEYS.put(3, 4);
        KEYS.put(4, 1);
        KEYS.put(5, -3);
        
        HttpClient httpclient = WebClientDevWrapper.getNewHttpClient();
        HttpPost post = new HttpPost(SERVER_URL);
        post.addHeader("Authorization", "Basic " + Base64Coder.encodeString(LOGIN + ":" + PASSWORD));
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("v", VERSION));
        params.add(new BasicNameValuePair("query", encode(formXml(stopId))));
        try {
            post.setEntity(new UrlEncodedFormEntity(params, CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = null;
        if (response != null) {
            entity = response.getEntity();
        }
        Response mResponse = null;
        if (entity != null) {

            InputStream inStream = null;
            try {
                inStream = entity.getContent();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String decodedXml = decode(inStream);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document doc = null;
            try {
                if (inStream != null) {
                    InputStream isToParse = new ByteArrayInputStream(decodedXml.getBytes("UTF-8"));
                    doc = db.parse(isToParse);
                }
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mResponse = new Response();
            mResponse.setData(doc);
        }
        return mResponse;
    }

    private String decode(InputStream xml) {
        String isString = isToString(xml);
        try {
            isString = URLDecoder.decode(isString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String source = Base64Coder.decodeString(isString);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            int c = source.charAt(i);
            c -= KEYS.get(i % 6);
            builder.append((char) c);
        }
        return Base64Coder.decodeString(builder.toString());
    }

    private String isToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private String encode(String s) {
        String source = Base64Coder.encodeString(s);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            int c = source.charAt(i);
            c += KEYS.get(i % 6);
            builder.append((char) c);
        }
        return Base64Coder.encodeString(builder.toString());
    }

    private String formXml(String stopId) {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc;
        Element root;
        try {
            builder = dbfac.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        doc = builder.newDocument();
        root = doc.createElement("getStopInfo");
        doc.appendChild(root);

        Element accIdElement = doc.createElement("accountID");
        accIdElement.appendChild(doc.createTextNode(getAccId()));
        root.appendChild(accIdElement);

        Element deviceIdElement = doc.createElement("phoneID");
        deviceIdElement.appendChild(doc.createTextNode(getDeviceId()));
        root.appendChild(deviceIdElement);

        Element zipElement = doc.createElement("zip");
        zipElement.appendChild(doc.createTextNode("0"));
        root.appendChild(zipElement);

        Element langElement = doc.createElement("lang");
        langElement.appendChild(doc.createTextNode("ua"));
        root.appendChild(langElement);

        Element cityElement = doc.createElement("city");
        cityElement.appendChild(doc.createTextNode("kyiv"));
        root.appendChild(cityElement);

        Element stopIdElement = doc.createElement("stopID");
        stopIdElement.appendChild(doc.createTextNode(stopId));
        root.appendChild(stopIdElement);

        Element formatElement = doc.createElement("format");
        formatElement.appendChild(doc.createTextNode("xml"));
        root.appendChild(formatElement);

        Element showRoutesElement = doc.createElement("showRoutes");
        showRoutesElement.appendChild(doc.createTextNode("true"));
        root.appendChild(showRoutesElement);

        Element mergeRoutesElement = doc.createElement("mergeSameRoutes");
        mergeRoutesElement.appendChild(doc.createTextNode("true"));
        root.appendChild(mergeRoutesElement);

        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        try {
            transformer.transform(domSource, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private String getDeviceId() {
        return Settings.Secure.getString(WidgetFactory.context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getAccId() {
        String email = "";
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(WidgetFactory.context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                email = account.name;
            }
        }
        return email;
    }
}