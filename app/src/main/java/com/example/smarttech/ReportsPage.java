package com.example.smarttech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReportsPage extends AppCompatActivity {

    private static final String TAG = "CustomersPage";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;


    private static final int STORAGE_PERMISSION_CODE = 101;

    EditText customercode;
    Button generatepdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_page);

        customercode = findViewById(R.id.edit_entercustcode);
        generatepdf = findViewById(R.id.btn_generatepdf);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
            }
        };




        generatepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                createPDF();

            }
        });

    }

    public void createPDF()
    {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                String custcodereport = customercode.getText().toString();

                String custcode = snapshot.child(userId).child("CustomerDetails").child(custcodereport).child("custCode").getValue().toString();
                String customername = snapshot.child(userId).child("CustomerDetails").child(custcodereport).child("custName").getValue().toString();
                String quantity = snapshot.child(userId).child("MilkCollectionDetails").child(custcodereport).child("litres").getValue().toString();
                String rate = snapshot.child(userId).child("MilkCollectionDetails").child(custcodereport).child("rate").getValue().toString();
                String amount = snapshot.child(userId).child("MilkCollectionDetails").child(custcodereport).child("amount").getValue().toString();

                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                String filePath = Environment.getExternalStorageDirectory().getPath() + "/Download/Bill.pdf";
                File file = new File(filePath);
                int i=1;
                page.getCanvas().drawText("Report: ",0,20,new Paint());


                page.getCanvas().drawText("Customer Code: ",0,25*i+25,new Paint());
                page.getCanvas().drawText(custcode, 105, 25*i+25, new Paint());

                page.getCanvas().drawText("Customer Name: ",0,25*i+45,new Paint());
                page.getCanvas().drawText(customername, 105, 25*i+45, new Paint());

                page.getCanvas().drawText("Quantity: ",0,25*i+65,new Paint());
                page.getCanvas().drawText(quantity, 55, 25*i+65, new Paint());

                page.getCanvas().drawText("Rate: ",0,25*i+85,new Paint());
                page.getCanvas().drawText(rate, 55, 25*i+85, new Paint());

                page.getCanvas().drawText("Amount: ",0,25*i+105,new Paint());
                page.getCanvas().drawText(amount, 55, 25*i+105, new Paint());

                page.getCanvas().drawText("---------------------------------------------------",0,25*i+145,new Paint());


                pdfDocument.finishPage(page);
                try {
                    pdfDocument.writeTo(new FileOutputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pdfDocument.close();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(ReportsPage.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(ReportsPage.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
            Toast.makeText(ReportsPage.this,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ReportsPage.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(ReportsPage.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //add a toast to show when successfully signed in

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


}