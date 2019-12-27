package com.itonemm.movieappadminpanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import es.dmoral.toasty.Toasty;

public class SeriesPopUp extends DialogFragment {

    public SeriesModel seriesModel;
    public String id="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.seriespopup,container,false);
        final Spinner spinner=view.findViewById(R.id.series_category);
        final ArrayList<String> category_names=new ArrayList<String>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference category_Ref=db.collection("categories");


        Button btnsaveseries=view.findViewById(R.id.save_series);
        final EditText edtname=view.findViewById(R.id.series_name);
        final EditText edtvideo=view.findViewById(R.id.series_video);
        final EditText edtimage=view.findViewById(R.id.series_image_link);

        if(seriesModel!=null)
        {
            edtname.setText(seriesModel.seriesName);
            edtvideo.setText(seriesModel.seriesVideo);
            edtimage.setText(seriesModel.seriesImage);


            category_Ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    category_names.clear();
                    for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                    {
                        CategoryModel c=snapshot.toObject(CategoryModel.class);
                        category_names.add(c.categoryname);

                    }
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,category_names);
                    spinner.setAdapter(adapter);

                    for(int i=0;i<category_names.size();i++)
                    {
                        if(category_names.get(i).equals(seriesModel.seriesCategory))
                        {
                            spinner.setSelection(i);
                            break;
                        }
                    }
                }
            });




        }
        else
        {

            category_Ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    category_names.clear();
                    for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                    {
                        CategoryModel c=snapshot.toObject(CategoryModel.class);
                        category_names.add(c.categoryname);

                    }
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,category_names);
                    spinner.setAdapter(adapter);


                }
            });
        }
        btnsaveseries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(seriesModel==null)
              {
                  if(!edtimage.getText().toString().equals("")
                          && !edtname.getText().toString().equals("")
                          && !edtvideo.getText().toString().equals(""))
                  {
                      FirebaseFirestore db=FirebaseFirestore.getInstance();
                      CollectionReference series_Ref=db.collection("series");
                      SeriesModel model=new SeriesModel(
                              edtname.getText().toString(),
                              edtimage.getText().toString(),
                              edtvideo.getText().toString(),
                              category_names.get(spinner.getSelectedItemPosition())
                      );
                      series_Ref.add(model);
                      edtname.setText("");
                      edtvideo.setText("");
                      edtimage.setText("");
                      spinner.setSelection(0);
                      Toasty.success(getContext(),"Series Save Successfully",Toasty.LENGTH_LONG).show();
                      SeriesFragment.loadseries();;
                  }
                  else
                  {
                      Toasty.error(getContext(),"Please Fill Series Data",Toasty.LENGTH_LONG).show();
                  }
              }
              else{
                  if(!edtimage.getText().toString().equals("")
                          && !edtname.getText().toString().equals("")
                          && !edtvideo.getText().toString().equals(""))
                  {
                      FirebaseFirestore db=FirebaseFirestore.getInstance();
                      CollectionReference series_Ref=db.collection("series");
                      SeriesModel model=new SeriesModel(
                              edtname.getText().toString(),
                              edtimage.getText().toString(),
                              edtvideo.getText().toString(),
                              category_names.get(spinner.getSelectedItemPosition())
                      );
                      series_Ref.document(id).set(model);
                      edtname.setText("");
                      edtvideo.setText("");
                      edtimage.setText("");
                      spinner.setSelection(0);
                      Toasty.success(getContext(),"Series Update Successfully",Toasty.LENGTH_LONG).show();
                      seriesModel=null;
                      id="";
                      SeriesFragment.loadseries();
                  }
                  else
                  {
                      Toasty.error(getContext(),"Please Fill Series Data",Toasty.LENGTH_LONG).show();
                  }
              }
            }
        });
        return view;
    }
}
