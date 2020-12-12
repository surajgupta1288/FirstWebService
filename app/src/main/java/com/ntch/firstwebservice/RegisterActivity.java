package com.ntch.firstwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity
{
    private EditText mEtNewUerName;
    private EditText mEtNewUserPassword;
    private EditText mEtNewUserMail;
    private EditText mEtMobile;
    private Button mBtnRegister;
    String android_id;
    String host;
    NetworkConnection nwc;
    String mEtNewUerNameStr="",mEtNewUserPasswordStr="",mEtNewUserMailstr="",mEtNewUsermobilestr="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        host=getString(R.string.host);// Call the host
        nwc= new NetworkConnection(RegisterActivity.this);//Declare the network check
        mEtNewUerName = findViewById(R.id.et_new_user_name);
        mEtNewUserPassword = findViewById(R.id.et_new_user_password);
        mEtNewUserMail = findViewById(R.id.et_new_user_mail);
        mEtMobile = findViewById(R.id.et_new_user_mobile);
        mBtnRegister = findViewById(R.id.btn_register);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);



        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mEtNewUerNameStr=mEtNewUerName.getText().toString().trim();
                mEtNewUserPasswordStr = mEtNewUserPassword.getText().toString().trim();
                mEtNewUserMailstr = mEtNewUserMail.getText().toString().trim();
                mEtNewUsermobilestr = mEtMobile.getText().toString().trim();

                if (nwc.isConnectingToInternet()){
                    new RegisterActivity.UserRegisterWithSOAP().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else {
                    Toast.makeText(RegisterActivity.this,"No internet",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    private class UserRegisterWithSOAP extends AsyncTask<Void, Void, String> {
        private ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Registering Wait...");
            pd.show();
        }
        @Override
        protected String doInBackground(Void... voids) {
            String text = null;
            try {
                SoapObject request = new SoapObject("http://tempuri.org/", "User_registration");
                request.addProperty("name",mEtNewUerNameStr );
                request.addProperty("password", mEtNewUserPasswordStr);
                request.addProperty("mail", mEtNewUserMailstr);
                request.addProperty("mobile", mEtNewUsermobilestr);
                request.addProperty("deviceid", android_id);
                System.out.println("id........."+mEtNewUerNameStr);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE httpTransportSE = new HttpTransportSE(host);
                httpTransportSE.debug = true;
                httpTransportSE.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                httpTransportSE.call("http://tempuri.org/User_registration", envelope);
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
                    NodeList nodeList = doc.getElementsByTagName("Register");//Change Responce method name
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
                        Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);

                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(RegisterActivity.this,"Invalid id and password", Toast.LENGTH_LONG).show();
                    }
                }
            }else{
                // server not response
                Toast.makeText(RegisterActivity.this,"Out Of Service Not Responding...",Toast.LENGTH_LONG).show();
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