package com.anggastudio.printama_sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DonationScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donation_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // donation provider buttons
        findViewById(R.id.btn_paypal).setOnClickListener(v -> openPaypal());
        findViewById(R.id.btn_ko_fi).setOnClickListener(v -> openKofi());
        findViewById(R.id.btn_trakteer).setOnClickListener(v -> openTrakteer());
        findViewById(R.id.btn_saweria).setOnClickListener(v -> openSaweria());
    }

    private void openPaypal() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/anggastudio"));
        startActivity(browserIntent);
    }

    private void openKofi() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/anggastudio"));
        startActivity(browserIntent);
    }

    private void openTrakteer() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://trakteer.id/anggastudio/tip"));
        startActivity(browserIntent);
    }

    private void openSaweria() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://saweria.co/anggastudio"));
        startActivity(browserIntent);
    }
}