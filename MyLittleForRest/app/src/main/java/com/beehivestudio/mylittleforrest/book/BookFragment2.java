package com.beehivestudio.mylittleforrest.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.beehivestudio.mylittleforrest.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class BookFragment2 extends Fragment {
    public BookFragment2() {

    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_book2, container, false);
        ImageView book2 = root.findViewById(R.id.book2);

        db.collection("Book").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int count =0;
                    for (int i = 1; i < 4; i++) {
                        if (document.getBoolean("seed" + i)) {
                            if(count==1) {
                                if (i == 1)
                                    Glide.with(root.getContext())
                                            .load(R.drawable.rose)
                                            .into(book2);
                                else if (i == 2)
                                    Glide.with(root.getContext())
                                            .load(R.drawable.sunflower)
                                            .into(book2);
                                else if (i == 3)
                                    Glide.with(root.getContext())
                                            .load(R.drawable.rosemoss)
                                            .into(book2);
                                break;
                            }
                            count++;
                        }
                    }
                }
            }
        });

        return root;
    }
}
