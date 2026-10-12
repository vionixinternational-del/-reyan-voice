package com.reyan.voice;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {

private TextToSpeech speaker;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(32, 48, 32, 32);

    TextView title = new TextView(this);
    title.setText("Reyan Voice");
    title.setTextSize(28);
    layout.addView(title);

    TextView message = new TextView(this);
    message.setText("ഹരിയേട്ടാ, ഞാൻ Reyan ആണ്. എന്നോട് സംസാരിക്കൂ.");
    message.setTextSize(20);
    layout.addView(message);

    Button button = new Button(this);
    button.setText("Reyan സംസാരിക്കട്ടെ");
    layout.addView(button);

    setContentView(layout);

    speaker = new TextToSpeech(this, status -> {
        if (status == TextToSpeech.SUCCESS) {
            speaker.setLanguage(new Locale("ml", "IN"));
        }
    });

    button.setOnClickListener(v -> {
        if (speaker != null) {
            speaker.speak(
                "ഹരിയേട്ടാ, ഞാൻ റേയൻ ആണ്. ഞാൻ നിങ്ങളോടൊപ്പം പഠിച്ചുകൊണ്ട് മുന്നോട്ട് പോകാൻ തയ്യാറാണ്.",
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
        speaker.shutdown();
    }
    super.onDestroy();
}

}
