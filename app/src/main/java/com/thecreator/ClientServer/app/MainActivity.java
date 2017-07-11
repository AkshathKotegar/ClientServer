package com.thecreator.ClientServer.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button server, client, testWebOld, testWebNew, uploadPdf, uploadDoc;
    private ArrayList<Parameters> toSend = new ArrayList<>();

    public static final String UPLOAD_URL = "http://akshathkotegar.000webhostapp.com/uploads.php";
    private Uri filePath;
    private int PICK_PDF_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        server = (Button) findViewById(R.id.btn_server);
        client = (Button) findViewById(R.id.btn_client);
        testWebOld = (Button) findViewById(R.id.btn_test);
        testWebNew = (Button) findViewById(R.id.btn_test_new);
        uploadPdf = (Button) findViewById(R.id.btn_test_pdf);
        uploadDoc = (Button) findViewById(R.id.btn_test_doc);
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ServerActivity.class);
                startActivity(i);
            }
        });


        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ClientActivity.class);
                startActivity(i);
            }
        });

        testWebOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebserviceOld wb = new WebserviceOld();
                String serviceUrlSecondPart = "Register/Register.svc?wsdl";
                String Method_name = "IRegister/DropDownMerchantCategory";
                String Function_Name = "DropDownMerchantCategory";
                toSend.clear();
                String result = wb.callMethodName(toSend, MainActivity.this, serviceUrlSecondPart, Method_name, Function_Name);
                Toast.makeText(MainActivity.this, "Result = " + result, Toast.LENGTH_LONG).show();
            }
        });

        testWebNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceUrlSecondPart = "Register/Register.svc?wsdl";
                String Method_name = "IRegister/DropDownMerchantCategory";
                String Function_Name = "DropDownMerchantCategory";
                toSend.clear();
                Webservice webVerify = new Webservice();
                webVerify.callMethodName(toSend, getApplicationContext(), serviceUrlSecondPart, Method_name, Function_Name);
                Webservice.YourTask taskVerifyAppUserEmail = webVerify.new YourTask(MainActivity.this, new OnTaskCompleted() {
                    @Override

                    public void onTaskCompleted(String webResponse) {
                        String result;
                        result = webResponse;
                        Toast.makeText(MainActivity.this, "Result = " + result, Toast.LENGTH_LONG).show();
                    }
                });
                taskVerifyAppUserEmail.execute();

            }
        });

        uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        uploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, UploadToServer.class);
                startActivity(i);
            }
        });
    }


    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        //intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_PDF_REQUEST);
    }

    //handling the ima chooser activity result
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadPdf();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadPdf() {
        //getting name for the pdf
        String name = "gdfg";

        //getting the actual path of the pdf
        String path = FilePath.getPath(this, filePath);

        if (path == null) {

            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("name", name) //Adding text parameter to the request
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        JSONObject jsonObject = new JSONObject();
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }




}
