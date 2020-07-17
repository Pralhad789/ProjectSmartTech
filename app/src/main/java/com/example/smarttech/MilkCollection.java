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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milkcollection);

        custcode = findViewById(R.id.edit_entercustcode);
        custname = findViewById(R.id.edit_custname);
        fat = findViewById(R.id.edit_fat);
        lacto = findViewById(R.id.edit_lacto);
        rate = findViewById(R.id.edit_rate);
        litres = findViewById(R.id.edit_litres);
        amount = findViewById(R.id.edit_amount);

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


        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        String customercode = custcode.getText().toString();
        collecref = myRef.child(userId).child("CustomerDetails").child("20").child("custName");


        custsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String customercode = custcode.getText().toString();

                //Query query = FirebaseDatabase.getInstance().getReference("CustomerDetails").orderByChild("custcode").equalTo(customercode);

                collecref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

//                        if(snapshot.hasChild("20"))
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



//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        MilkCollectionData collectionData = snapshot.getValue(MilkCollectionData.class);
////                        artistList.add(artist);
//
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };


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
