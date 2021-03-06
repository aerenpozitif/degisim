// -*- @author aeren_pozitif  -*- //
package dergi.degisim.splash;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import dergi.degisim.MainActivity;
import dergi.degisim.R;
import dergi.degisim.fragment.MainFragment;
import dergi.degisim.db.Database;

public class SplashScreen extends AppCompatActivity {
    public static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Loader(getApplicationContext()).execute();
    }

    class Loader extends AsyncTask<Void, Void, Void> {
        private Context context;

        Loader(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (Database.checkLoggedIn()) {
                final FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

                assert usr != null;
                ref.child(usr.getUid()).child("markeds").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MainFragment.LAST_MARKINGS = Arrays.asList(dataSnapshot.getValue().toString().split(","));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Query q = firestore.collection("haberler").orderBy("id", Query.Direction.DESCENDING);
            q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    final DocumentSnapshot ds = queryDocumentSnapshots.getDocuments().get(0);
                    Picasso.with(context).load(ds.getString("uri")).fetch(new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("SPLASH", "Cached the image of " + (ds.getLong("id") + 1) + ". article");
                        }

                        @Override
                        public void onError() {
                            Log.e("SPLASH", "Error on caching");
                        }
                    });

                }
            });

            try {
                Thread.sleep(SPLASH_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);

            cancel(true);
            finish();
        }
    }
}
