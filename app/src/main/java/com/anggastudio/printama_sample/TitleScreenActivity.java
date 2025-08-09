package com.anggastudio.printama_sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TitleScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_title_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // print test buttons
        findViewById(R.id.btn_settings_page).setOnClickListener(v -> gotoSettingsPage());
        findViewById(R.id.btn_github).setOnClickListener(v -> gotoGithubPage());
        findViewById(R.id.btn_donate).setOnClickListener(v -> gotoDonatePage());

        String appVersion = BuildConfig.VERSION_NAME;
        String appVersionDisplay = "Version " + appVersion;
        TextView tvAppVersion = findViewById(R.id.tv_app_version);
        tvAppVersion.setText(appVersionDisplay);
    }

    private void gotoGithubPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/anggastudio/Printama"));
        startActivity(browserIntent);
    }

    private void gotoDonatePage() {
        Intent intent = new Intent(TitleScreenActivity.this, DonationScreenActivity.class);
        ContextCompat.startActivity(this, intent, new Bundle());
    }

    private void gotoSettingsPage() {
        Intent intent = new Intent(TitleScreenActivity.this, MainActivity.class);
        ContextCompat.startActivity(this, intent, new Bundle());
    }
}