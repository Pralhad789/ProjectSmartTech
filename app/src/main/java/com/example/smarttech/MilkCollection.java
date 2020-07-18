package com.example.smarttech;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MilkCollection extends AppCompatActivity {

    private static final String TAG = "MilkCollection";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, collecref;

    EditText custcode,custname, fat, SNF, lacto, rate, litres, amount;
    Button custsearch, custdatasubmit;

    MilkCollectionData collectionData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milkcollection);

        custcode = findViewById(R.id.edit_entercustcode);
        custname = findViewById(R.id.edit_custname);
        fat = findViewById(R.id.edit_fat);
        SNF = findViewById(R.id.edit_SNF);
        lacto = findViewById(R.id.edit_lacto);
        rate = findViewById(R.id.edit_rate);
        rate.setText("2");
        litres = findViewById(R.id.edit_litres);
        amount = findViewById(R.id.edit_amount);

        collectionData = new MilkCollectionData();

        custsearch = findViewById(R.id.btn_searchcustdata);
        custdatasubmit = findViewById(R.id.btn_addcustdata);

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


        custsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                String customercode = custcode.getText().toString();
                collecref = myRef.child(userId).child("CustomerDetails").child(customercode).child("custName");
                //String customercode = custcode.getText().toString();

                //Query query = FirebaseDatabase.getInstance().getReference("CustomerDetails").orderByChild("custcode").equalTo(customercode);

                collecref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

//                        if(snapshot.hasChild(customercode))
//                        {
                            String customername = snapshot.getValue(String.class);
                            custname.setText(customername);
//                        }
//                        else
//                        {
//                            Toast.makeText(MilkCollection.this,"Entered ID does not exist",Toast.LENGTH_LONG).show();
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        custdatasubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                String customercode = custcode.getText().toString();

                collectionData.setCustcode(custcode.getText().toString().trim());
                collectionData.setCustname(custname.getText().toString().trim());
                collectionData.setFat(fat.getText().toString().trim());
                collectionData.setSNF(SNF.getText().toString().trim());
                collectionData.setLacto(lacto.getText().toString().trim());
                collectionData.setRate(rate.getText().toString().trim());
                collectionData.setLitres(litres.getText().toString().trim());
                collectionData.setAmount(amount.getText().toString().trim());

                //myRef.child(userId).child("CustomerDetails").push().setValue(custmaster);
                myRef.child(userId).child("MilkCollectionDetails").child(customercode).setValue(collectionData);


                Toast.makeText(MilkCollection.this, "Data Inserted Successfully",Toast.LENGTH_LONG).show();
            }
        });


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
