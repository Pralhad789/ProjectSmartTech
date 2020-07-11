package com.example.smarttech;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.api.services.drive.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CustomersPage extends AppCompatActivity {
//    private DatabaseReference mDatabase;

    private static final String TAG = "CustomersPage";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    EditText custcode,custname, custaddress, custbalance, custsaleamt, custrecievedamt;
    Button custsubmit;
    CustomerMaster custmaster;

//    FirebaseAuth mAuth;
//    DatabaseReference reff;

    long maxid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_page);

//        mAuth = FirebaseAuth.getInstance();

        custcode = findViewById(R.id.edit_custcode);
        custname = findViewById(R.id.edit_custname);
        custaddress = findViewById(R.id.edit_custaddress);
        custbalance = findViewById(R.id.edit_custbalance);
        custsaleamt = findViewById(R.id.edit_custsaleamt);
        custrecievedamt = findViewById(R.id.edit_custrecievedamt);
        custsubmit = findViewById(R.id.btn_addcustdata);
        custmaster = new CustomerMaster();

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

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        reff = FirebaseDatabase.getInstance().getReference().child("CustomerDetails");
//
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid = (dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        custsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String customercode = custcode.getText().toString();
//                String customername = custname.getText().toString();
//                String customeraddress = custaddress.getText().toString();
//                String customerbalance = custbalance.getText().toString();
//                String customersaleamt = custsaleamt.getText().toString();
//                String customerrecievedamt = custrecievedamt.getText().toString();

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                custmaster.setCustCode(custcode.getText().toString().trim());
                custmaster.setCustName(custname.getText().toString().trim());
                custmaster.setCustaddress(custaddress.getText().toString().trim());
                custmaster.setCustBalance(custbalance.getText().toString().trim());
                custmaster.setCustSaleamt(custsaleamt.getText().toString().trim());
                custmaster.setCustRecievedamt(custrecievedamt.getText().toString().trim());

                myRef.child(userId).child("CustomerDetails").push().setValue(custmaster);

//                myRef.child(userId).child("CustomerDetails").child(customercode).setValue(customercode);

//                DatabaseReference custdetailsref = myRef.child(userId).child("CustomerDetails").child(customercode);
//
//                Map<String, User> custdetails = new HashMap<>();
//                custdetails.put("Customer Name",new User());

                Toast.makeText(CustomersPage.this, "Data Inserted Successfully",Toast.LENGTH_LONG).show();
            }
        });





//        custsubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                FirebaseDatabase.getInstance().getReference(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
////                long points = dataSnapshot.child("Points").getValue(Long.class);
//                        addcustomerdetails();
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        throw databaseError.toException();
//                    }
//                });
//
//            }
//        });
    }

    private void addcustomerdetails()
    {

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