package com.example.roteirofacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class cadHospedagem extends AppCompatActivity {
    private static final String DB_HOSPEDAGEM = "hospedagem";
    EditText edtCustoperNoite;
    EditText edtTotNoite;
    EditText edtTotQuartos;
    CheckBox chkAdicionarViagem;
    EditText edtCalcTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_hospedagem);
        edtCustoperNoite = findViewById(R.id.edtCustoperNoite);
        edtTotNoite =  findViewById(R.id.edtTotNoite);
        edtTotQuartos =  findViewById(R.id.edtTotQuartos);
        chkAdicionarViagem = findViewById(R.id.chkAdicionarViagem);

        edtCustoperNoite.addTextChangedListener(textWatcher);
        edtTotNoite.addTextChangedListener(textWatcher);
        edtTotQuartos.addTextChangedListener(textWatcher);

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select CUSTONOITE, TOTNOITE, TOTQUARTO, CH_ADD, CH_PROVISORIO from " + DB_HOSPEDAGEM + " where CH_PROVISORIO = 'T'", null);
            if (tabela.moveToLast()) {
                edtCustoperNoite.setText(tabela.getString(0));
                edtTotNoite.setText(tabela.getString(1));
                edtTotQuartos.setText(tabela.getString(2));
                String add = String.valueOf(tabela.getString(3));
                chkAdicionarViagem.setChecked(add.equals("T"));
            }
        } catch (Exception e) {

        }
    }

    public void funcVoltar(View view) {
        Intent intent = new Intent(this, cadRefeicao.class);
        startActivity(intent);
    }
    public void funcAvancar(View view) {
        edtCustoperNoite = findViewById(R.id.edtCustoperNoite);
        edtTotNoite =  findViewById(R.id.edtTotNoite);
        edtTotQuartos = findViewById(R.id.edtTotQuartos);
        chkAdicionarViagem = findViewById(R.id.chkAdicionarViagem);

        int custoPorNoite = 0;
        if (!String.valueOf(edtCustoperNoite.getText()).isEmpty()) {
            custoPorNoite = Integer.parseInt(String.valueOf(edtCustoperNoite.getText()));
        }

        if (custoPorNoite == 0) {
            Toast.makeText(this, "Custo médio por Noite está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        int totNoite = 0;
        if (!String.valueOf(edtTotNoite.getText()).isEmpty()) {
            totNoite = Integer.parseInt(String.valueOf(edtTotNoite.getText()));
        }

        if (totNoite == 0) {
            Toast.makeText(this, "Total de Noites está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        int totQuartos = 0;
        if (!String.valueOf(edtTotQuartos.getText()).isEmpty()) {
            totQuartos = Integer.parseInt(String.valueOf(edtTotQuartos.getText()));
        }

        if (totQuartos == 0) {
            Toast.makeText(this, "Total de Quartos está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        String bAdd = String.valueOf(chkAdicionarViagem.isChecked());
        String adicionar = bAdd.equals("true") ? "T" : "F";

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            //"(id INTEGER PRIMARY KEY AUTOINCREMENT, id_principal INTEGER,  custonoite TEXT, totnoite TEXT, totquarto TEXT, ch_add TEXT, ch_provisorio TEXT)";
            banco.execSQL("delete from " + DB_HOSPEDAGEM + " where CH_PROVISORIO = 'T'");
            banco.execSQL("insert into " + DB_HOSPEDAGEM + " (custonoite, totnoite, totquarto, ch_add, ch_provisorio) values ('"+ custoPorNoite +"', '"+ totNoite +"', '"+ totQuartos +"', '"+ adicionar +"', 'T')");
            Intent intent = new Intent(this, cadEntretenimento.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // this function is called before text is edited
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // this function is called when text is edited
        }

        @Override
        public void afterTextChanged(Editable s) {
            edtCustoperNoite = findViewById(R.id.edtCustoperNoite);
            edtTotNoite =  findViewById(R.id.edtTotNoite);
            edtTotQuartos = findViewById(R.id.edtTotQuartos);
            edtCalcTotal = findViewById(R.id.edtCalcTotal);

            int custoPerNoite = 0;
            if (!String.valueOf(edtCustoperNoite.getText()).isEmpty()) {
                custoPerNoite = Integer.parseInt(String.valueOf(edtCustoperNoite.getText()));
            }

            int totNoite = 0;
            if (!String.valueOf(edtTotNoite.getText()).isEmpty()) {
                totNoite = Integer.parseInt(String.valueOf(edtTotNoite.getText()));
            }

            int totQuartos = 0;
            if (!String.valueOf(edtTotQuartos.getText()).isEmpty()) {
                totQuartos = Integer.parseInt(String.valueOf(edtTotQuartos.getText()));
            }

            if ((custoPerNoite > 0) && (totNoite > 0) && (totQuartos > 0)) {
                Double calcTotal = (double) ((custoPerNoite * totNoite) * totQuartos);
                edtCalcTotal.setText(String.valueOf(calcTotal));
            }
        }
    };
}