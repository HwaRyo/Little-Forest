package com.beehivestudio.mylittleforrest.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class BookFragment3 extends Fragment {
    public BookFragment3() {

    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_book3, container, false);
        ImageView book3 = root.findViewById(R.id.book3);
        TextView book3_txt = root.findViewById(R.id.txtbook3);

        db.collection("Book").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int count =0;
                    for (int i = 1; i < 4; i++) {
                        if (document.getBoolean("seed" + i)) {
                            if(count==2) {
                                if (i == 1) {
                                    Glide.with(root.getContext())
                                            .load(R.drawable.book_rose)
                                            .into(book3);
                                    book3_txt.setText("장미의 꽃말은 욕망, 열정, 기쁨, 아름다움, 결정 입니다.\n" +
                                            "당신의 도전은 항상 욕망과 열정의 결정체죠.\n" +
                                            "힘든 일을 열정과 욕망으로 불태운다면 언젠가 장미처럼 아름다워지지 않을까요?");

                                } else if (i == 2) {
                                    Glide.with(root.getContext())
                                            .load(R.drawable.book_sunflower)
                                            .into(book3);
                                    book3_txt.setText("해바라기의 꽃말은 자부심입니다.\n" +
                                            "해바라기는 밝을 때 고개를 들어 세상을 향해 꿎꿎히 꽃을 세우죠.\n" +
                                            "그러나 어두운 밤에는 고개를 숙여 밝은 날을 기다리죠.\n" +
                                            "지금 당신이 힘들다고 느끼는 건 밝은 날을 위한 준비가 아닐까요?");
                                } else if (i == 3) {
                                    Glide.with(root.getContext())
                                            .load(R.drawable.book_cosmos)
                                            .into(book3);
                                    book3_txt.setText("코스모스의 꽃말을 아름다움, 질서, 평화, 온전함, 겸손함 입니다.\n" +
                                            "당신은 충분히 아름다워요.\n" +
                                            "아름답지 않다고 느낀다면 마음의 평화가 오지 않은건 아닐까요?");
                                }
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

