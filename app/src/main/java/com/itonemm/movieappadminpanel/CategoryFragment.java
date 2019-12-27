package com.itonemm.movieappadminpanel;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    static ListView listView;
    static FragmentManager fragmentManager;
   static ArrayList<String> documentids=new ArrayList<String>();
   public static LayoutInflater staticinflater;
   static Context context;



    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View layout_view= inflater.inflate(R.layout.fragment_category, container, false);

        fragmentManager=getFragmentManager();

        context=getContext();
        staticinflater=getLayoutInflater();
        FloatingActionButton floatingActionButton=layout_view.findViewById(R.id.addcategory);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp popUp=new CategoryPopUp();
                FragmentManager fm=getFragmentManager();
                popUp.show(fm,"Show Category");

            }
        });
       listView=layout_view.findViewById(R.id.categorylist);

        final ArrayList<CategoryModel> categoryModels=new ArrayList<CategoryModel>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final CollectionReference categoryRef=db.collection("categories");
        categoryRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                categoryModels.clear();
                documentids.clear();
                for (DocumentSnapshot snapshot:queryDocumentSnapshots)
                {
                    categoryModels.add(snapshot.toObject(CategoryModel.class));
                    documentids.add(snapshot.getId());
                }
                CategoryAdapter adapter=new CategoryAdapter(categoryModels);
                listView.setAdapter(adapter);

            }
        });


        final EditText edt_categorysearch=layout_view.findViewById(R.id.searchcatgory);
        edt_categorysearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

                if(edt_categorysearch.getText().toString().equals("")) {
                    categoryRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            categoryModels.clear();
                            documentids.clear();
                            for (DocumentSnapshot snapshot:queryDocumentSnapshots)
                            {
                                categoryModels.add(snapshot.toObject(CategoryModel.class));
                                documentids.add(snapshot.getId());
                            }
                            CategoryAdapter adapter=new CategoryAdapter(categoryModels);
                            listView.setAdapter(adapter);

                        }
                    });
                }
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference category = db.collection("categories");
                    category.whereEqualTo("categoryname", s.toString())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                    categoryModels.clear();
                                    documentids.clear();
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        categoryModels.add(snapshot.toObject(CategoryModel.class));
                                        documentids.add(snapshot.getId());
                                    }
                                    CategoryAdapter adapter = new CategoryAdapter(categoryModels);
                                    listView.setAdapter(adapter);
                                }
                            });
                }
            }
        });
        return  layout_view;
    }


    public static void loadCategories()
    {
        final ArrayList<CategoryModel> categoryModels=new ArrayList<CategoryModel>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference categoryRef=db.collection("categories");
        categoryRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                documentids.clear();
                categoryModels.clear();
                for (DocumentSnapshot snapshot:queryDocumentSnapshots)
                {
                    categoryModels.add(snapshot.toObject(CategoryModel.class));
                    documentids.add(snapshot.getId());
                }
                CategoryAdapter adapter=new CategoryAdapter(categoryModels);
                listView.setAdapter(adapter);

            }
        });
    }

    private static class CategoryAdapter extends BaseAdapter
    {

        ArrayList<CategoryModel>categoryModels=new ArrayList<>();

        public CategoryAdapter(ArrayList<CategoryModel> categoryModels) {
            this.categoryModels = categoryModels;
        }

        @Override
        public int getCount() {
            return categoryModels.size();
        }

        @Override
        public Object getItem(int position) {
            return categoryModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater=staticinflater;
            View view=layoutInflater.inflate(R.layout.categorylist,null);
            TextView txtcatgorysr=view.findViewById(R.id.category_sr);
            TextView categoryname=view.findViewById(R.id.category_name);
            txtcatgorysr.setText(String.valueOf(position+1));
            categoryname.setText(categoryModels.get(position).categoryname);
            ImageView ic_delete=view.findViewById(R.id.category_delete);
            ic_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation!")
                            .setMessage("Are you Sure To Delete?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                                    CollectionReference category=db.collection("categories");
                                    category.document(documentids.get(position)).delete();
                                    category.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                                            documentids.clear();
                                            ArrayList<CategoryModel> categoryModels=new ArrayList<>();
                                            for (DocumentSnapshot snapshot:queryDocumentSnapshots)
                                            {
                                                categoryModels.add(snapshot.toObject(CategoryModel.class));
                                                documentids.add(snapshot.getId());
                                            }
                                            CategoryAdapter adapter=new CategoryAdapter(categoryModels);
                                            listView.setAdapter(adapter);

                                        }
                                    });
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();


                }
            });

            ImageView edit=view.findViewById(R.id.btn_category_edit);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryPopUp popUp=new CategoryPopUp() ;
                    popUp.categoryModel=categoryModels.get(position);
                    popUp.id=documentids.get(position);
                    popUp.show(fragmentManager,"Edit Category");
                }
            });
            return view;
        }
    }



}
