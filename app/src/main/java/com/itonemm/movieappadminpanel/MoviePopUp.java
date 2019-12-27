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

public class MoviePopUp extends DialogFragment {

    public MoiveModel moiveModel;
   ArrayList<String> seriesnames=new ArrayList<String>();
    public String id;
    ArrayList<String> names=new ArrayList<String>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view=inflater.inflate(R.layout.moviepopup,container,false);

        final Spinner cat_spinner=view.findViewById(R.id.movie_category);
        final Spinner series_spinner=view.findViewById(R.id.movie_series);
        final FirebaseFirestore db=FirebaseFirestore.getInstance();



        final EditText edtmoviename=view.findViewById(R.id.movie_name);
        final EditText edtmovieimage=view.findViewById(R.id.movie_image_link);
        final EditText edtmovievideo=view.findViewById(R.id.movie_video);



        if(moiveModel!=null)
        {
            edtmoviename.setText(moiveModel.movieName);
            edtmovieimage.setText(moiveModel.movieImage);
            edtmovievideo.setText(moiveModel.movieVideo);
            CollectionReference category=db.collection("categories");

            category.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    names.clear();;
                    for(DocumentSnapshot s: queryDocumentSnapshots)
                    {
                        CategoryModel categoryModel=s.toObject(CategoryModel.class);
                        names.add(categoryModel.categoryname);
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,names);
                    cat_spinner.setAdapter(arrayAdapter);

                    for(int i=0;i<names.size();i++)
                    {
                        if(names.get(i).equals(moiveModel.movieCategory))
                        {
                            cat_spinner.setSelection(i);
                            break;
                        }
                    }
                }
            });

            CollectionReference series=db.collection("series");

            series.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    seriesnames.clear();
                    for(DocumentSnapshot s: queryDocumentSnapshots)
                    {
                        SeriesModel seriesModel=s.toObject(SeriesModel.class);
                        seriesnames.add(seriesModel.seriesName);
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,seriesnames);
                    series_spinner.setAdapter(arrayAdapter);
                    for(int i=0;i<seriesnames.size();i++)
                    {
                        if(seriesnames.get(i).equals(moiveModel.movieSeries))
                        {
                            series_spinner.setSelection(i);
                            break;
                        }
                    }
                }
            });

        }

        else
        {
            CollectionReference category=db.collection("categories");
            category.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    names.clear();
                    for(DocumentSnapshot s: queryDocumentSnapshots)
                    {
                        CategoryModel categoryModel=s.toObject(CategoryModel.class);
                        names.add(categoryModel.categoryname);
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,names);
                    cat_spinner.setAdapter(arrayAdapter);
                }
            });

            CollectionReference series=db.collection("series");

            series.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    seriesnames.clear();
                    for(DocumentSnapshot s: queryDocumentSnapshots)
                    {
                        SeriesModel seriesModel=s.toObject(SeriesModel.class);
                        seriesnames.add(seriesModel.seriesName);
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,seriesnames);
                    series_spinner.setAdapter(arrayAdapter);
                }
            });

        }

        Button btnsavemovie=view.findViewById(R.id.save_movie);
        btnsavemovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               if(!edtmovieimage.getText().toString().equals("")
               && !edtmoviename.getText().toString().equals("")
               && !edtmovievideo.getText().toString().equals(""))
               {
                  if(moiveModel==null)
                  {
                      MoiveModel moiveModel=new MoiveModel(

                              edtmoviename.getText().toString(),
                              edtmovieimage.getText().toString(),
                              edtmovievideo.getText().toString(),
                              names.get(cat_spinner.getSelectedItemPosition()),
                              seriesnames.get(series_spinner.getSelectedItemPosition())
                      );
                      CollectionReference movie=db.collection("movies");
                      movie.add(moiveModel);
                      Toasty.success(getContext(),"Movie Save Successfully",Toasty.LENGTH_LONG).show();;
                      edtmoviename.setText("");
                      edtmovievideo.setText("");
                      edtmovieimage.setText("");
                      cat_spinner.setSelection(0);
                      series_spinner.setSelection(0);
                      MovieFragment.loadMovies();
                  }
                  else
                  {
                      MoiveModel moiveModel=new MoiveModel(

                              edtmoviename.getText().toString(),
                              edtmovieimage.getText().toString(),
                              edtmovievideo.getText().toString(),
                              names.get(cat_spinner.getSelectedItemPosition()),
                              seriesnames.get(series_spinner.getSelectedItemPosition())
                      );
                      CollectionReference movie=db.collection("movies");
                      movie.document(id).set(moiveModel);
                      Toasty.success(getContext(),"Movie Update Successfully",Toasty.LENGTH_LONG).show();;
                      edtmoviename.setText("");
                      edtmovievideo.setText("");
                      edtmovieimage.setText("");
                      cat_spinner.setSelection(0);
                      series_spinner.setSelection(0);
                      MovieFragment.loadMovies();;
                  }
               }
               else
               {
                   Toasty.error(getContext(),"Please Fill Movie Data",Toasty.LENGTH_LONG).show();
               }
            }

        });
      return view;
    }
}
