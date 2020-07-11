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
import com.google.firebase.database.ValueEventListener;

public class ItemsPage extends AppCompatActivity {

    private static final String TAG = "Itemspage";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    EditText itemcode, itemname, itemcategory, itemstockqty, itemsaleqty, itemrecvqty;
    Button itemsubmit;

    ItemMaster itemmaster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_page);

        itemcode = findViewById(R.id.edit_itemcode);
        itemname = findViewById(R.id.edit_itemname);
        itemcategory = findViewById(R.id.edit_itemcategory);
        itemstockqty = findViewById(R.id.edit_itemstockqty);
        itemsaleqty = findViewById(R.id.edit_itemsaleqty);
        itemrecvqty = findViewById(R.id.edit_itemrecievedqty);
        itemsubmit = findViewById(R.id.btn_itemsubmit);

        itemmaster = new ItemMaster();

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



        itemsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                itemmaster.setItemcode(itemcode.getText().toString().trim());
                itemmaster.setItemname(itemname.getText().toString().trim());
                itemmaster.setItemcategory(itemcategory.getText().toString().trim());
                itemmaster.setItemstockqty(itemstockqty.getText().toString().trim());
                itemmaster.setItemsaleqty(itemsaleqty.getText().toString().trim());
                itemmaster.setItemrecvqty(itemrecvqty.getText().toString().trim());

                myRef.child(userId).child("ItemDetails").push().setValue(itemmaster);



                Toast.makeText(ItemsPage.this, "Data Inserted Successfully",Toast.LENGTH_LONG).show();
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



    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}