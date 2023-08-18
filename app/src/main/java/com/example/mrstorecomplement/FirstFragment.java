package com.example.mrstorecomplement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class FirstFragment extends Fragment {

    private WebView mWebView;
    private HashMap<String, String[]> productsMap = new HashMap<String, String[]>();
    String[] dataProd;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void addProducto(String desc, ArrayAdapter adapter){
        adapter.add(desc);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Charset stringCharset = Charset.forName("Cp866");

        //EditText txtId = view.findViewById(R.id.textBoxProductId);

        //InputStream dbf = getClass().getClassLoader().getResourceAsStream("PRODUCTO.dbf");

        InputStream dbf = null;
        //TextView productsTextView = (TextView) view.findViewById(R.id.textView_products);
        //productsTextView.setText("Aquí van los productos---");
        //productsTextView.setText(txtId.getText());
        //productsTextView.setMovementMethod(new ScrollingMovementMethod());
//        try {
//            dbf = getActivity().getAssets().open("PRODUCTO.dbf");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        DbfRecord rec;
//        try {
//            try (DbfReader reader = new DbfReader(dbf)) {
//                DbfMetadata meta = reader.getMetadata();
//
//                System.out.println("Read DBF Metadata: " + meta);
//                int i = 301;
//                int encontrado = 2;
//                while ((rec = reader.read()) != null && encontrado == 2) {
//                    rec.setStringCharset(stringCharset);
//                    //productsTextView.append((CharSequence) rec.toMap().get("DES_PROD"));
//                    //i++;
//                    if("004033".equals(rec.toMap().get("COD_PROD"))){
//                        System.out.println("----ççççççççççççççç---------ssssss" + rec.toMap() + "  ---  " );
//                    }
//
//
//
//
//
//                }
//            }
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }




        Button vaciarBtn = view.findViewById(R.id.btnVaciarLista);







        Button btnSearchProduct =  view.findViewById(R.id.btnSearchProduct);
        TextView productsTextView = (TextView) view.findViewById(R.id.textView_products);
        TextView serverIpTextView = (TextView) view.findViewById(R.id.serverIp);
        EditText txtId = view.findViewById(R.id.textBoxProductId);
        txtId.setText("");

        String filename = "serverIpData";
        FileInputStream fis = null;
        try {
            fis = getContext().openFileInput(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fis);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            line = reader.readLine();
            serverIpTextView.setText(line);
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        }

        FloatingActionButton addProductToList = view.findViewById(R.id.floatingActionButton);

        ArrayList<String> productsArray = new ArrayList();


        ListView productsList = (ListView)view.findViewById(R.id.productsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, productsArray);
//
      productsList.setAdapter(adapter);

        txtId.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtId.getWindowToken(), 0);


        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(getContext());
                adb.setTitle("Eliminar producto");
                adb.setMessage("Está seguro de eliminar el producto de la lista?");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.remove(adapter.getItem(positionToRemove));
                        adapter.notifyDataSetChanged();
                    }});
                adb.show();
            }
        });


        vaciarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getCount() > 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Vaciar listado de productos ");
                    builder.setMessage("Seguro de vaciar la lista de impresión?");
                    builder.setPositiveButton("Vaciar", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            adapter.clear();
                            Snackbar.make(view, "Se vació la lista", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            Snackbar.make(view, "Acción cancelada por el usuario", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Snackbar.make(view, "Lista sin elementos a eliminar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


            }
        });


        addProductToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(productsTextView.getText().toString() != ""){
                    if(productsArray.indexOf(productsTextView.getText().toString()) >= 0){
                        Snackbar.make(view, "Ya se había agregado este producto", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{
                        productsMap.put(Integer.toString(productsMap.size()), dataProd);
                        addProducto(productsTextView.getText().toString(), adapter);

                        productsTextView.setText("");
                        txtId.setText("");
                        txtId.clearFocus();

                        Snackbar.make(view, "Producto agregado", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    txtId.requestFocus();
                }else{
                    txtId.requestFocus();
                    Snackbar.make(view, "Sin producto a agregar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


            }
        });

        txtId.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {

                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {

                    btnSearchProduct.callOnClick();
                    txtId.requestFocus();

                    return false;
                }

                return false;
            }
        });









        serverIpTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String filename = "serverIpData";
                    String fileContents = serverIpTextView.getText().toString();
                    try (FileOutputStream fos = getContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
                        fos.write(fileContents.getBytes());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });







        btnSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String baseIp = serverIpTextView.getText().toString();
                String url ="http://" + baseIp + ":8000/products/" + txtId.getText();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                //textView.setText("Response: " + response.toString());
                                try {

                                    if(!txtId.getText().toString().equals(""))
                                        dataProd = new  String[]{txtId.getText().toString(), response.get("desc").toString(), response.get("precio").toString()};
                                    productsTextView.setText("$"  + dataProd[2] + "  ---  " + dataProd[1]);

                                    txtId.clearFocus();
                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(txtId.getWindowToken(), 0);
                                    txtId.setText("");
                                    txtId.requestFocus();
                                } catch (JSONException e) {
                                    Snackbar.make(view, "No se encontró el producto", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                Snackbar.make(view, "Solicitud Incorrecta", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });


                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);



                /*
                Charset stringCharset = Charset.forName("Cp866");

                EditText txtId = view.findViewById(R.id.textBoxProductId);

                //InputStream dbf = getClass().getClassLoader().getResourceAsStream("PRODUCTO.dbf");
                InputStream dbf = null;
                TextView productsTextView = (TextView) view.findViewById(R.id.textView_products);
                productsTextView.setText("Aquí van los productos---");
                productsTextView.setText(txtId.getText());
                productsTextView.setMovementMethod(new ScrollingMovementMethod());
                try {
                    dbf = getActivity().getAssets().open("PRODUCTO.dbf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DbfRecord rec;
                try {
                    try (DbfReader reader = new DbfReader(dbf)) {
                        DbfMetadata meta = reader.getMetadata();

                        System.out.println("Read DBF Metadata: " + meta);
                        int i = 301;
                        int encontrado = 2;
                        while ((rec = reader.read()) != null && encontrado == 2) {
                            rec.setStringCharset(stringCharset);
                            //productsTextView.append((CharSequence) rec.toMap().get("DES_PROD"));
                            //i++;
                            //System.out.println("" + rec.toMap().get("COD_PROD").getClass().getName() + "  ---  " + txtId.getText().toString().getClass().getName());

                            if( txtId.getText().toString().equals(rec.toMap().get("DES_PROD")) ||  txtId.getText().toString().equals(rec.toMap().get("DES_PROD2"))) {
                                System.out.println("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
                                productsTextView.append((CharSequence) rec.toMap().get("DES_PROD"));
                                //productsTextView.append((CharSequence) rec.toMap().get("DES_PROD2"));
                               encontrado = 1;
                            }


                        }
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }*/
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Preparando para impresión", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();



                // Create a WebView object specifically for printing
                WebView webView = new WebView(getActivity());
                webView.setWebViewClient(new WebViewClient() {

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        //Log.i(TAG, "page finished loading " + url);
                        createWebPrintJob(view);
                        mWebView = null;
                    }
                });

                // Generate an HTML document on the fly:
                String htmlDocument = "<html><body><h1>Listado de Productos</h1><hr/><br/><br/><table border='1' width='100%'><thead><tr><th>Código</th><th>Descripción</th><th>Precio</th></tr></thead>" + getProductsHtml(productsArray);
                htmlDocument += "</table></body></html>";
                webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

                // Keep a reference to WebView object until you pass the PrintDocumentAdapter
                // to the PrintManager
                mWebView = webView;
            }
        });






        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createWebPrintJob(WebView view) {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = getString(R.string.app_name) + " Document";

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());


    }

    private String getProductsHtml(ArrayList productsList){
        String html = "<tbody>";
        Iterator productsIterator = productsList.iterator();
       //String[] data_prod = productsMap.get("jdwhdhsad");

        for(String[] tmpData : productsMap.values()){
            html += "<tr>";
            html += "<td>" +tmpData[0] + "</td>";
            html += "<td>" +tmpData[1] + "</td>";
            html += "<td>" +tmpData[2] + "</td>";
            html += "</tr>";
        }


        int count = 0;
        /*while(productsIterator.hasNext()){
            html += "<tr>";
            html += "<td>" + productsIterator.next().toString() + "</td></tr>";
        }*/
        html += "</tbody>";
        return html;
    }
}