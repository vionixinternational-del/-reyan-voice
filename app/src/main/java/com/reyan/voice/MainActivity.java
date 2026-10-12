package com.reyan.voice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech speaker;
    private SpeechRecognizer speechRecognizer;
    private TextView statusText;
    private TextView messageText;
    private Button micButton;

    private static final String BRIDGE_URL = "http://127.0.0.1:8080/chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // സ്റ്റാറ്റസ് ബാർ ഡിസൈൻ
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        // മൈക്രോഫോൺ പെർമിഷൻ പരിശോധിക്കുന്നു
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 101);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(36, 48, 36, 40);

        scrollView.addView(layout);

        // ലോഗോ
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.reyan_logo);
        logo.setAdjustViewBounds(true);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(240, 240);
        logoParams.gravity = Gravity.CENTER;
        logoParams.bottomMargin = 24;
        layout.addView(logo, logoParams);

        // ടൈറ്റിൽ
        TextView title = new TextView(this);
        title.setText("REYAN VOICE");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(25, 35, 65));
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        // സബ്‌ടൈറ്റിൽ
        TextView subtitle = new TextView(this);
        subtitle.setText("നിങ്ങളുടെ സ്വയംഭരണ AI കൂട്ടുകാരൻ");
        subtitle.setTextSize(17);
        subtitle.setTextColor(Color.rgb(80, 90, 110));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 20);
        layout.addView(subtitle);

        // റയാന്റെ മറുപടികൾ കാണിക്കുന്ന ബോക്സ്
        messageText = new TextView(this);
        messageText.setText("ഹരിയേട്ടാ, ഞാൻ കേൾക്കാൻ തയ്യാറാണ്. മൈക്ക് അമർത്തി സംസാരിച്ചോളൂ!");
        messageText.setTextSize(19);
        messageText.setTextColor(Color.rgb(35, 45, 65));
        messageText.setGravity(Gravity.CENTER);
        messageText.setPadding(16, 16, 16, 24);
        layout.addView(messageText);

        // സ്റ്റാറ്റസ് ടെക്സ്റ്റ്
        statusText = new TextView(this);
        statusText.setText("തയ്യാറാണ്...");
        statusText.setTextSize(14);
        statusText.setTextColor(Color.rgb(120, 130, 150));
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, 0, 0, 20);
        layout.addView(statusText);

        // മൈക്ക് ബട്ടൺ
        micButton = new Button(this);
        micButton.setText("🎤  സംസാരിക്കൂ (Tap to Speak)");
        micButton.setTextSize(18);
        micButton.setBackgroundColor(Color.rgb(40, 90, 210));
        micButton.setTextColor(Color.WHITE);
        micButton.setPadding(24, 20, 24, 20);
        layout.addView(micButton);

        setContentView(scrollView);

        // Text-To-Speech ഇൻസ്റ്റാളേഷൻ
        speaker = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = speaker.setLanguage(new Locale("ml", "IN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    speaker.setLanguage(Locale.getDefault());
                }
            }
        });

        // Speech Recognizer സജ്ജമാക്കുന്നു
        initSpeechRecognizer();

        micButton.setOnClickListener(v -> startListening());
    }

    private void initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override public void onReadyForSpeech(Bundle params) { statusText.setText("ശ്രദ്ധിക്കുന്നു... സംസാരിക്കൂ..."); }
                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() { statusText.setText("റയാൻ ചിന്തിക്കുന്നു..."); }
                @Override public void onError(int error) {
                    statusText.setText("ശബ്ദം വ്യക്തമായില്ല, വീണ്ടും ശ്രമിക്കുക.");
                }
                @Override public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String userQuery = matches.get(0);
                        messageText.setText("ഹരിയേട്ടൻ: " + userQuery);
                        sendQueryToReyanBridge(userQuery);
                    }
                }
                @Override public void onPartialResults(Bundle partialResults) {}
                @Override public void onEvent(int eventType, Bundle params) {}
            });
        }
    }

    private void startListening() {
        if (speechRecognizer != null) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL
