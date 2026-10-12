package com.reyan.voice;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(36, 48, 36, 40);

        scrollView.addView(layout);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.reyan_logo);
        logo.setAdjustViewBounds(true);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(240, 240);
        logoParams.gravity = Gravity.CENTER;
        logoParams.bottomMargin = 24;
        layout.addView(logo, logoParams);

        TextView title = new TextView(this);
        title.setText("REYAN VOICE");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(25, 35, 65));
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("നിങ്ങളുടെ AI കൂട്ടുകാരൻ");
        subtitle.setTextSize(19);
        subtitle.setTextColor(Color.rgb(80, 90, 110));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 12, 0, 28);
        layout.addView(subtitle);

        TextView message = new TextView(this);
        message.setText(
                "ഹരിയേട്ടാ, ഞാൻ Reyan ആണ്.\n" +
                "നമുക്ക് ഒരുമിച്ച് പഠിക്കാം,\n" +
                "ചിന്തിക്കാം, മുന്നോട്ട് പോകാം."
        );
        message.setTextSize(21);
        message.setTextColor(Color.rgb(35, 45, 65));
        message.setGravity(Gravity.CENTER);
        message.setPadding(12, 12, 12, 32);
        layout.addView(message);

        Button button = new Button(this);
        button.setText("🔊  Reyan സംസാരിക്കട്ടെ");
        button.setTextSize(18);
        layout.addView(button);

        setContentView(scrollView);

        speaker = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = speaker.setLanguage(new Locale("ml", "IN"));

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    speaker.setLanguage(Locale.getDefault());
                }
            }
        });

        button.setOnClickListener(v -> {
            if (speaker != null) {
                speaker.speak(
                    "ഹരിയേട്ടാ, ഞാൻ റേയൻ ആണ്. " +
                    "ഞാൻ നിങ്ങളോടൊപ്പം പഠിച്ചുകൊണ്ട് മുന്നോട്ട് പോകാൻ തയ്യാറാണ്.",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "reyan_greeting"
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }
}
