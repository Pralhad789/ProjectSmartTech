package com.example.smarttech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SalesPage extends AppCompatActivity {

    private static final String TAG = "Salespage";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    EditText supplcode, supplname, suppladdr, supplbalc, supplbillmt, supplpaidamt;
    Button supplier_submit,seestockbtn;

    SupplierMaster suppliermaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_page);

        supplcode = findViewById(R.id.edit_suppliercode);
        supplname = findViewById(R.id.edit_suppliername);
        suppladdr = findViewById(R.id.edit_supplieraddr);
        supplbalc = findViewById(R.id.edit_supplierbalc);
        supplbillmt = findViewById(R.id.edit_supplierbillamt);
        supplpaidamt = findViewById(R.id.edit_supplierpaidamt);
        supplier_submit = findViewById(R.id.btn_submitsupplier);

        suppliermaster = new SupplierMaster();

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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                toastMessage("Failed to alter database.");
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        supplier_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                suppliermaster.setSupplcode(supplcode.getText().toString().trim());
                suppliermaster.setSupplname(supplname.getText().toString().trim());
                suppliermaster.setSupplbalc(supplbalc.getText().toString().trim());
                suppliermaster.setSuppladdr(suppladdr.getText().toString().trim());
                suppliermaster.setSupplbillmt(supplbillmt.getText().toString().trim());
                suppliermaster.setSupplpaidamt(supplpaidamt.getText().toString().trim());

                myRef.child(userId).child("SupplierDetails").push().setValue(suppliermaster);



                Toast.makeText(SalesPage.this, "Data Inserted Successfully",Toast.LENGTH_LONG).show();
            }
        });



//        seestockbtn = findViewById(R.id.see_stock);
//        seestockbtn.setOnClickListener(v -> {
//            startActivity(new Intent(SalesPage.this, StocksPage.class));
//            finish();
//        });
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



    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}