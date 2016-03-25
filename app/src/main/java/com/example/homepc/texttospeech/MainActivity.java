package com.example.homepc.texttospeech;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends ListActivity implements AdapterView.OnItemLongClickListener {

    private EditText editText;
    private String text;
    private TextToSpeech textToSpeech;
    private ArrayList<String> words;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (restoreWords() == null) {
            words = new ArrayList<>();
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, words);
        setListAdapter(arrayAdapter);

        editText = (EditText) findViewById(R.id.editText);
        text = "please type something";

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    textToSpeech.setLanguage(new Locale("Indonesian", "Indonesia"));
                }
            }
        });

        // Display keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        editText.setCursorVisible(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);

        // get listview for long press
        listView = getListView();
        listView.setOnItemLongClickListener(this);

    }

    public void doBunyi(View v) {
        text = editText.getText().toString();
        if (text.isEmpty()) {
            text="Sila taip dan kemudian tekan butang cakap"; //sealer tool ace bur toll bur toll
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        } else {

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            onAddWord(text);

        }
    }

    public void doClear( View v) {
        editText.setText("");
    }



    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();

    }

    public void onAddWord(String wordAdded) {
        if (words.contains(wordAdded)) return;
        words.add(wordAdded);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String text = (String) l.getItemAtPosition(position);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void saveWords(ArrayList<String> wordsToSave) {
        String FILENAME = "talktype";

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(wordsToSave);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> restoreWords() {
        String FILENAME = "talktype";

        try {
            FileInputStream fis = openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            words = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return words;
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveWords(words);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String toRemove = arrayAdapter.getItem(position);
        arrayAdapter.remove(toRemove);

        return true;
    }
}
