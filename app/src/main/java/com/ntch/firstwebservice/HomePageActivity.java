package com.ntch.firstwebservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ntch.firstwebservice.Model.ProductData;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class HomePageActivity extends AppCompatActivity
{

    RecyclerView offerRecycleView;
    private List<ProductData> ProductDetailsList;
    ProductDetailsDataArrayAdapter productDetailsDataArrayAdapter;

    NetworkConnection nwc;
    String host="";
    private TextView mTvDisplayUserId;
    private TextView mTvDisplayUserName;
    private Button mBtnLogout, mBtnNewProduct;
    String UserIdstr ="", UserName ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
                host=getString(R.string.host);
                nwc= new NetworkConnection(HomePageActivity.this);
        mTvDisplayUserId = findViewById(R.id.tv_display_user_id);
        mTvDisplayUserName = findViewById(R.id.tv_display_user_name);
        mBtnLogout = findViewById(R.id.btn_logout);
        mBtnNewProduct =findViewById(R.id.btn_new_products);

        offerRecycleView=findViewById(R.id.offerRecycleView);
        Intent intent = getIntent();
        if(intent!= null)
        {
            UserIdstr = intent.getStringExtra("userId1111111");
            UserName = intent.getStringExtra("Name1111111111");

        }
        System.out.println("userId1111111////////"+UserIdstr);
        mTvDisplayUserId.setText(UserIdstr);
        mTvDisplayUserName.setText(UserName);
        if (nwc.isConnectingToInternet()){
            new HomePageActivity.GetNewProductWithSOAP().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            Toast.makeText(HomePageActivity.this,"No internet",Toast.LENGTH_LONG).show();
        }
        ProductDetailsList = new ArrayList<>();
        productDetailsDataArrayAdapter = new ProductDetailsDataArrayAdapter(this, ProductDetailsList);
        offerRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        offerRecycleView.setItemAnimator(new DefaultItemAnimator());
        offerRecycleView.setAdapter(productDetailsDataArrayAdapter);

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HomePageActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        mBtnNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this,NewProductActivity.class);
                startActivity(intent);

            }
        });
    }

    private class GetNewProductWithSOAP extends AsyncTask<Void, Void, String> {
        private ProgressDialog pd = new ProgressDialog(HomePageActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Loading...");
            pd.show();
        }
        @Override
        protected String doInBackground(Void... voids) {
            String text = null;
            try {
                SoapObject request = new SoapObject("http://tempuri.org/", "Newproduct_section");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE httpTransportSE = new HttpTransportSE(host);
                httpTransportSE.debug = true;
                httpTransportSE.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                httpTransportSE.call("http://tempuri.org/Newproduct_section", envelope);
                text = httpTransportSE.responseDump;
            } catch (Exception e) {
            }
            return text;
        }
        @Override
        protected void onPostExecute(String results) {
            System.out.println("results..."+results);
            pd.setMessage(results);
            pd.show();
            pd.hide();
            pd.dismiss();
            super.onPostExecute(results);
            if (results != null) {
                Document doc= getDomElement(results);
                if (doc != null){
                    String res0 = "", res1 = "", res2 = "",res3="",res4="",res5="";
                    NodeList nodeList = doc.getElementsByTagName("topoffertbl");
                    for (int a = 0; a < nodeList.getLength(); a++){
                        Element element = (Element)nodeList.item(a);
                          res0 += getValue(element, "name").equals("") ? "-#":getValue(element, "name")+"#";
                          res1 += getValue(element, "mrprice").equals("") ? "-#":getValue(element, "mrprice")+"#";
                          res2 += getValue(element, "prdctprice").equals("") ? "-#":getValue(element, "prdctprice")+"#";
                          res3 += getValue(element, "stockavilbl").equals("") ? "-#":getValue(element, "stockavilbl")+"#";
                          res4 += getValue(element, "Image2").equals("") ? "-#":getValue(element, "Image2")+"#";

                    }

                    String[] productNameArr = res0.split("\\#");
                    String[] mrpPriceArr = res1.split("\\#");
                    String[] productPriceArr = res2.split("\\#");
                    String[] stockavilblArr = res3.split("\\#");
                    String[] imageArr = res4.split("\\#");

                    System.out.println("productNameArr......."+productNameArr[0]);
                    System.out.println("mrpPriceArr......."+mrpPriceArr[0]);
                    System.out.println("productPriceArr......."+productPriceArr[0]);
                    System.out.println("stockavilblArr......."+stockavilblArr[0]);
                    System.out.println("imageArr......."+imageArr[0]);

                    if (productNameArr[0].equals("-") || productNameArr[0].equals("0") || productNameArr[0].equals("")){
                        offerRecycleView.setVisibility(View.GONE);
                    }else {
                        offerRecycleView.setVisibility(View.VISIBLE);
                        for (int i=0;i<productNameArr.length;i++){
                            ProductData d = new ProductData(productNameArr[i],mrpPriceArr[i],productPriceArr[i],
                                    stockavilblArr[i],imageArr[i]);
                            ProductDetailsList.add(d);
                        }

                        productDetailsDataArrayAdapter.notifyDataSetChanged();



                    }

                }
            }else{
                Toast.makeText(HomePageActivity .this, "Connectivity Issue Detected, Please Ensure the network connectivity", Toast.LENGTH_LONG).show();
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

    public class ProductDetailsDataArrayAdapter extends RecyclerView.Adapter<ProductDetailsDataArrayAdapter.MyViewHolder>{

        private Context mContext;
        private List<ProductData> productList;
        List<ProductData> filterList;




        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mItem_name;
            public TextView mItem_mrp;
            public TextView mItem_selling_price;
            public TextView mItem_stock_available;
            public ImageView mItem_image;



            public MyViewHolder(View view) {
                super(view);

                mItem_name = view.findViewById(R.id.item_name);
                mItem_mrp = view.findViewById(R.id.item_mrp);
                mItem_selling_price = view.findViewById(R.id.item_selling_price);
                mItem_stock_available = view.findViewById(R.id.item_stock_available);
                mItem_image = view.findViewById(R.id.item_image);
            }
        }


        public ProductDetailsDataArrayAdapter(Context mContext, List<ProductData> productList) {
            this.mContext = mContext;
            this.productList = productList;
            this.filterList = productList;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_items, parent, false);

            return new MyViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {


            final ProductData model = filterList.get(position);

            holder.mItem_name.setText(model.getProductName());
            holder.mItem_mrp.setText("Rs"+model.getMrpPrice());
            holder.mItem_selling_price.setText("Rs "+model.getProductPrice());
            holder.mItem_stock_available.setText(model.getStockAvl());
            Glide.with((mContext)).load(model.getImageS()).thumbnail(0.5f)
                    .placeholder(R.drawable.note)
                    .error(R.drawable.note)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mItem_image);

        }



        @Override
        public int getItemCount() {
            return filterList.size();
        }



    }



}