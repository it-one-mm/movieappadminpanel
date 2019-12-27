package com.itonemm.movieappadminpanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import es.dmoral.toasty.Toasty;

public class CategoryPopUp  extends DialogFragment {
    public CategoryModel categoryModel;
    public String id="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.cateogrylayout,container,false);
        final EditText edtcategoryanme=view.findViewById(R.id.edt_category_name);

        if(categoryModel!=null)
        {
            edtcategoryanme.setText(categoryModel.categoryname);
        }




        Button btnsave=view.findViewById(R.id.btn_save_category);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(!edtcategoryanme.getText().toString().equals("") && edtcategoryanme.getText().toString()!=null) {
                   if(categoryModel==null)
                   {
                       CategoryModel c = new CategoryModel(edtcategoryanme.getText().toString());
                       FirebaseFirestore db = FirebaseFirestore.getInstance();
                       CollectionReference categoryRef = db.collection("categories");
                       categoryRef.add(c);
                       edtcategoryanme.setText("");
                       Toasty.success(getContext(),"Category save successfully!",Toasty.LENGTH_LONG).show();
                       CategoryFragment.loadCategories();
                   }
                   else
                   {
                       CategoryModel c = new CategoryModel(edtcategoryanme.getText().toString());
                       FirebaseFirestore db = FirebaseFirestore.getInstance();
                       CollectionReference categoryRef = db.collection("categories");
                       categoryRef.document(id).set(c);
                       edtcategoryanme.setText("");
                       Toasty.success(getContext(),"Category Update successfully!",Toasty.LENGTH_LONG).show();
                       CategoryFragment.loadCategories();
                       categoryModel=null;
                       id="";

                   }
                }
                else
                {
                    Toasty.error(getContext(),"Please Fill Category Name",Toasty.LENGTH_LONG).show();
                }

            }
        });

        Button btncacelcategory=view.findViewById(R.id.btn_cancel_category);
        btncacelcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryModel!=null)
                {
                    categoryModel=null;
                    id="";

                }

                    edtcategoryanme.setText("");

            }
        });
        return view;
    }
}
