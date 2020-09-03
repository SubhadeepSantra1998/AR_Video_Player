package com.theAndroGuy.ar_videoPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class credits extends AppCompatActivity {

    ImageView github, playstore, mail;
    TextView credits;


    private int REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UIelements();

        Credits_Drescription();

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_profile("https://github.com/SubhadeepSantra1998?tab=repositories");
            }


        });
        playstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_profile("https://play.google.com/store/apps/developer?id=Subhadeep+Santra");
            }
        });


        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailTo="subhadeepsantra828gmail.com";
                String mailSubject="Queries on AR Video Player ";
                sendEmail(mailTo,mailSubject);
            }

            private void sendEmail(String mailTo, String mailSubject) {
                Intent emailIntent=new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{mailTo});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,mailSubject);
                try {
                    startActivity(Intent.createChooser(emailIntent,"Choose an email client"));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Credits_Drescription() {
        credits.setText("* CircularImageView by Lopez Mikhael\n* Dexter Library\n* Icons made by Pixel perfect and Freepik from www.flaticon.com\n* Image by IO-Images from Pixabay");
    }

    private void UIelements() {
        github=findViewById(R.id.icon_github);
        playstore=findViewById(R.id.icon_playstore);
        mail=findViewById(R.id.icon_mail);
        credits=findViewById(R.id.credits_descrip);
    }
    public void clicked_profile(String url) {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}