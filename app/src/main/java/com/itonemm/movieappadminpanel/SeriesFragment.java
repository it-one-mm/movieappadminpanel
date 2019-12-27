package com.itonemm.movieappadminpanel;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import es.dmoral.toasty.Toasty;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesFragment extends Fragment {

    static ListView listView;
    static ArrayList<SeriesModel> seriesModels=new ArrayList<>();
    static  ArrayList<String>ids=new ArrayList<String>();
    static LayoutInflater layoutInflater;
    static Context context;
    static FragmentManager fragmentManager;
    public SeriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_series, container, false);
        FloatingActionButton floatingActionButton=view.findViewById(R.id.add_series);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeriesPopUp seriesPopUp=new SeriesPopUp();
                seriesPopUp.show(getFragmentManager(),"Show Series");
            }
        });

        layoutInflater=getLayoutInflater();
        context=getContext();
       listView=view.findViewById(R.id.series_list);
       fragmentManager=getFragmentManager();

       loadseries();

       final EditText edt_searcheries=view.findViewById(R.id.search_series);
       edt_searcheries.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {

               if(edt_searcheries.getText().toString().equals(""))
               {
                   loadseries();
               }

               else
               {
                   FirebaseFirestore db=FirebaseFirestore.getInstance();
                   CollectionReference collectionReference=db.collection("series");
                   collectionReference.whereEqualTo("seriesName",s.toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                       @Override
                       public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                           seriesModels.clear();;
                           ids.clear();;

                           for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                           {
                               ids.add(snapshot.getId());
                               seriesModels.add(snapshot.toObject(SeriesModel.class));
                           }

                           SeriesAdapter adapter=new SeriesAdapter(seriesModels);
                           listView.setAdapter(adapter);
                       }
                   });
               }
           }
       });

       return  view;
    }

    public  static  void loadseries()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final CollectionReference series=db.collection("series");
        series.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                seriesModels.clear();;
                ids.clear();;

                for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                {
                    ids.add(snapshot.getId());
                   seriesModels.add(snapshot.toObject(SeriesModel.class));
                }

                SeriesAdapter adapter=new SeriesAdapter(seriesModels);
                listView.setAdapter(adapter);
            }
        });


    }



    private static class SeriesAdapter extends BaseAdapter{
        ArrayList<SeriesModel> seriesModels=new ArrayList<SeriesModel>();

        public SeriesAdapter(ArrayList<SeriesModel> seriesModels) {
            this.seriesModels = seriesModels;
        }

        @Override
        public int getCount() {
            return seriesModels.size();
        }

        @Override
        public Object getItem(int position) {
            return seriesModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final View view=layoutInflater.inflate(R.layout.movielist,null);

            TextView sr=view.findViewById(R.id.movie_list_sr);
            TextView name=view.findViewById(R.id.movie_list_name);
            final ImageView imageView=view.findViewById(R.id.movie_list_image);

            sr.setText(String.valueOf(position+1));
            name.setText(seriesModels.get(position).seriesName);
            Glide.with(context)
                    .load(seriesModels.get(position).seriesImage)
                    .override(150,200)
                    .into(imageView);
            LinearLayout item=(LinearLayout) view.findViewById(R.id.itemlist);
            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu=new PopupMenu(context,imageView);
                    MenuInflater inflater=popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.popupmenu,popupMenu.getMenu());
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {


                            if(item.getItemId()==R.id.delet_menu){


                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setTitle("Confirmation!")
                                        .setMessage("Are you Sure To Delete?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseFirestore db=FirebaseFirestore.getInstance();
                                                CollectionReference sereisref=db.collection("series");

                                                sereisref.document(ids.get(position)).delete();
                                                SeriesFragment.loadseries();
                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();


                            }

                            if(item.getItemId()==R.id.edit_menu)
                            {
                                SeriesPopUp seriesPopUp=new SeriesPopUp();
                                seriesPopUp.seriesModel=seriesModels.get(position);
                                seriesPopUp.id=ids.get(position);
                                seriesPopUp.show(fragmentManager,"Edit Series");
                            }
                            return true;
                        }
                    });

                    return true;
                }
            });
            return view;

        }
    }


}
