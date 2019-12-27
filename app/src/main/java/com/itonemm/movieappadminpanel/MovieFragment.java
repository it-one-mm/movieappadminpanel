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

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    static Context context;
    static LayoutInflater inflater;
    static ListView movielist;
    static FragmentManager fragmentManager;
    static ArrayList<String>ids=new ArrayList<String>();
    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_movie, container, false);
        FloatingActionButton floatingActionButton=view.findViewById(R.id.add_movie);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoviePopUp popUp=new MoviePopUp();
                popUp.show(getFragmentManager(),"Add Movie");
            }
        });
        context=getContext();
        this.inflater=getLayoutInflater();
        fragmentManager=getFragmentManager();

       movielist=view.findViewById(R.id.movielist);
       loadMovies();

        final EditText edt_moviesearch=view.findViewById(R.id.search_movie);
        edt_moviesearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(edt_moviesearch.getText().toString().equals(""))
                {
                    loadMovies();
                }
                else
                {
                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                    CollectionReference collectionReference=db.collection("movies");
                    collectionReference.whereEqualTo("movieName",s.toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            ArrayList<MoiveModel> moiveModels=new ArrayList<>();
                            ids.clear();
                            for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                            {
                                MoiveModel moiveModel=snapshot.toObject(MoiveModel.class);
                                ids.add(snapshot.getId());
                                moiveModels.add(moiveModel);

                            }
                            MovieAdapter adapter=new MovieAdapter(moiveModels);
                            movielist.setAdapter(adapter);
                        }
                    });
                }
            }
        });
        return  view;
    }

    public static void loadMovies()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference  reference=db.collection("movies");

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<MoiveModel> moiveModels=new ArrayList<>();
                ids.clear();
                for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                {
                    MoiveModel moiveModel=snapshot.toObject(MoiveModel.class);
                    ids.add(snapshot.getId());
                    moiveModels.add(moiveModel);

                }
                MovieAdapter adapter=new MovieAdapter(moiveModels);
                movielist.setAdapter(adapter);

            }
        });
    }
    private static class MovieAdapter extends BaseAdapter
    {
        ArrayList<MoiveModel>moiveModels=new ArrayList<MoiveModel>();

        public MovieAdapter(ArrayList<MoiveModel> moiveModels) {
            this.moiveModels = moiveModels;
        }

        @Override
        public int getCount() {
            return moiveModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view=inflater.inflate(R.layout.movielist,null);
            final ImageView imageView=view.findViewById(R.id.movie_list_image);
            TextView  movie_list_sr=view.findViewById(R.id.movie_list_sr);
            final TextView movie_list_name=view.findViewById(R.id.movie_list_name);
            Glide.with(context)
                    .load(moiveModels.get(position).movieImage)
                    .override(200,150)
                    .into(imageView);
            movie_list_sr.setText(String.valueOf(position+1));
            movie_list_name.setText(moiveModels.get(position).movieName);
            LinearLayout itemlayout=view.findViewById(R.id.itemlist);
            itemlayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu=new PopupMenu(context,imageView);
                    MenuInflater inflater=popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.popupmenu,popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if(item.getItemId()==R.id.delet_menu)
                            {
                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setTitle("Confirmation!")
                                        .setMessage("Are you Sure To Delete?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseFirestore db=FirebaseFirestore.getInstance();
                                                CollectionReference ref=db.collection("movies");
                                                ref.document(ids.get(position)).delete();
                                                loadMovies();
                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                            }

                            if(item.getItemId()==R.id.edit_menu)
                            {
                                MoviePopUp popUp=new MoviePopUp();
                                popUp.moiveModel=moiveModels.get(position);
                                popUp.id=ids.get(position);
                                popUp.show(fragmentManager,"Edit Movie");
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
