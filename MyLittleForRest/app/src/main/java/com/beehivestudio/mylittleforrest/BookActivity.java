package com.beehivestudio.mylittleforrest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.beehivestudio.mylittleforrest.book.BookFragment;
import com.beehivestudio.mylittleforrest.book.BookFragment1;
import com.beehivestudio.mylittleforrest.book.BookFragment2;
import com.beehivestudio.mylittleforrest.book.BookFragment3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookActivity extends AppCompatActivity {
    private int NUM_PAGES = 0;
    private ViewPager2 pager;
    private FragmentStateAdapter pagerAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String uid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        db.collection("Book").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                int count = 1;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    for (int i = 1; i < 4; i++) {
                        if (document.getBoolean("seed" + i)) {
                            count++;
                        }
                    }
                }
                NUM_PAGES = count;
                pager = findViewById(R.id.pager);
                pagerAdapter = new ScreeSlidePagerAdapter(BookActivity.this);
                pager.setAdapter(pagerAdapter);
            }
        });


    }


    private class ScreeSlidePagerAdapter extends FragmentStateAdapter {
        public ScreeSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {


            if (position == 0) return new BookFragment();
            else if (position == 1) return new BookFragment1();
            else if (position == 2) return new BookFragment2();
            else return new BookFragment3();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}


