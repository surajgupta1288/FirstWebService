package com.ntch.firstwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ntch.firstwebservice.ServiceStatusCheck.NetworkConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtUerName;
    private EditText mEtUserPassword;
    private Button mBtnLetsGo;
    private TextView mtvNewUser;
    String android_id;
    String host;
    NetworkConnection nwc;
    String mEtUerNameStr="",mEtUserPasswordStr="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
            host=getString(R.string.host);// Call the host
        nwc= new NetworkConnection(LoginActivity.this);//Declare the network check
        mEtUerName = findViewById(R.id.et_user_name);
        mEtUserPassword = findViewById(R.id.et_user_password);
        mBtnLetsGo = findViewById(R.id.btn_go_to_home_page);
        mtvNewUser = findViewById(R.id.tv_new_user);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        mBtnLetsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mEtUerNameStr=mEtUerName.getText().toString().trim();
                mEtUserPasswordStr=mEtUserPassword.getText().toString().trim();
                if (nwc.isConnectingToInternet()){
                    new LoginActivity.UserLoginWithSOAP().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {
                    Toast.makeText(LoginActivity.this,"No internet",Toast.LENGTH_LONG).show();
                }

            }
        });

        mtvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    private class UserLoginWithSOAP extends AsyncTask<Void, Void, String> {
        private ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Logging In!");
            pd.show();
        }
        @Override
        protected String doInBackground(Void... voids) {
            String text = null;
            try {
                SoapObject request = new SoapObject("http://tempuri.org/", "user_Login_mobile");
                request.addProperty("usermobile",mEtUerNameStr );
                request.addProperty("Password", mEtUserPasswordStr);
                request.addProperty("deviceid", android_id);
                System.out.println("id........."+mEtUerNameStr);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE httpTransportSE = new HttpTransportSE(host);
                httpTransportSE.debug = true;
                httpTransportSE.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                httpTransportSE.call("http://tempuri.org/user_Login_mobile", envelope);
                text = httpTransportSE.responseDump;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }
        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);
            pd.setMessage(results);
            pd.show();
            pd.hide();
            pd.dismiss();
            if (results != null) {
                Document doc= getDomElement(results);
                if (doc != null){
                    String res0 = "", res1 = "",userId = "",Name = "";
                    NodeList nodeList = doc.getElementsByTagName("Logintbl");
                    for (int a = 0; a < nodeList.getLength(); a++){
                        Element element = (Element)nodeList.item(a);
                        res0 = getValue(element, "Errorno");
                        res1 = getValue(element, "Msg");
                        userId = getValue(element,"id");
                        Name = getValue(element,"Name");

                    }
                    //System.out.println("res1>>>>>>>>"+res0);
                    if (res0.equals("1")){
                        System.out.println("userId............."+userId);
                       Intent intent= new Intent(LoginActivity.this,HomePageActivity.class);
                       intent.putExtra("userId1111111",userId);
                       intent.putExtra("Name1111111111",Name);
                        startActivity(intent);
                       finish();
                    }else{
                       Toast.makeText(LoginActivity.this,"Invalid id and password", Toast.LENGTH_LONG).show();
                    }
                }
            }else{
               // server not response
                Toast.makeText(LoginActivity.this,"Out Of Service Not Responding...",Toast.LENGTH_LONG).show();
            }
        }
    }
    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return doc;
    }
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

}