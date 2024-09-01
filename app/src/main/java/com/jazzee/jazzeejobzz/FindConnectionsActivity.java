package com.jazzee.jazzeejobzz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindConnectionsActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<User> userList;
    private UserAdapter userAdapter;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_connections);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(userAdapter);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return false;
            }
        });

        loadAllUsers();
    }

    private void loadAllUsers() {
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setUserid(dataSnapshot.getKey());  // Set UID from key
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FindConnectionsActivity.this, "Error loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchUsers(String query) {
        if (TextUtils.isEmpty(query)) {
            loadAllUsers();
        } else {
            UsersRef.orderByChild("fullname").startAt(query).endAt(query + "\uf8ff")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    user.setUserid(dataSnapshot.getKey());  // Set UID from key
                                    userList.add(user);
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(FindConnectionsActivity.this, "Error searching users", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void openProfile(User user) {
        if (user.getUserid() != null) {
            Intent intent = new Intent(FindConnectionsActivity.this, ProfileActivity.class);
            intent.putExtra("userId", user.getUserid());
            startActivity(intent);
        } else {
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show();
        }
    }
}
