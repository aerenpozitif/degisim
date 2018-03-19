// -*- @author aeren_pozitif  -*- //
package dergi.degisim.news;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import dergi.degisim.R;

public class NewsPaper extends AppCompatActivity {
    private WebView w;
    private ImageView img;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspaper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        w = findViewById(R.id.web);
        img = findViewById(R.id.toolbar_image);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        String content = bundle.getString("content");
        String header = bundle.getString("header");
        String uri = bundle.getString("uri");
        final String id = bundle.getString("id");

        Picasso.with(getApplicationContext()).load(uri).resize(800,600).into(img);
        Log.d("CONTENT", content);

        String html = "<font size='4'>" + content + "<font/> <br/> <br/> <br/>" +
                      "<img src='https://www.universalorlando.com/web/k2/en/us/files/assets/spongebob-storepants-orlando-logo-b.png?imwidth=221'/> ";

        w.getSettings().setSupportZoom(true);
        w.loadData(html,"text/html", "UTF-8");
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setPluginState(WebSettings.PluginState.ON);
        setTitle(header);

        FirebaseFirestore.getInstance().collection("haberler").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                int read = Integer.parseInt(task.getResult().getString("read"));
                DocumentReference ref = FirebaseFirestore.getInstance().collection("haberler").document(id);
                ref.update("read", String.valueOf(read+1));
            }
        });
    }
}
