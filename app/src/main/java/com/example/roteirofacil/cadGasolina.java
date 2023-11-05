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

public class cadGasolina extends AppCompatActivity {

    EditText edtTotQuil;
    EditText edtMediaLitro;
    EditText edtCustoLitro;
    EditText edtTotVeic;
    CheckBox chkAdicionarViagem;
    EditText edtCalcTotal;
    private static final String DB_GASOLINA = "gasolina";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_gasolina);
        edtTotQuil = findViewById(R.id.edtTotQuil);
        edtMediaLitro =  findViewById(R.id.edtMediaLitro);
        edtCustoLitro = findViewById(R.id.edtCustoLitro);
        edtTotVeic = findViewById(R.id.edtTotVeic);
        chkAdicionarViagem = findViewById(R.id.chkAdicionarViagem);

        edtTotQuil.addTextChangedListener(textWatcher);
        edtMediaLitro.addTextChangedListener(textWatcher);
        edtCustoLitro.addTextChangedListener(textWatcher);
        edtTotVeic.addTextChangedListener(textWatcher);

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select QUILOMETRO, MEDIA, CUSTOMEDIO, TOTVEIC, CH_ADD, CH_PROVISORIO from " + DB_GASOLINA + " where CH_PROVISORIO = 'T'", null);
            if (tabela.moveToLast()) {
                edtTotQuil.setText(tabela.getString(0));
                edtMediaLitro.setText(tabela.getString(1));
                edtCustoLitro.setText(tabela.getString(2));
                edtTotVeic.setText(tabela.getString(3));
                String add = String.valueOf(tabela.getString(4));
                chkAdicionarViagem.setChecked(add.equals("T"));
            }
        } catch (Exception e) {

        }
    }

    public void funcVoltar(View view) {
        Intent intent = new Intent(this, cadDados.class);
        startActivity(intent);
    }
    public void funcAvancar(View view) {
        edtTotQuil = findViewById(R.id.edtTotQuil);
        edtMediaLitro =  findViewById(R.id.edtMediaLitro);
        edtCustoLitro = findViewById(R.id.edtCustoLitro);
        edtTotVeic = findViewById(R.id.edtTotVeic);
        chkAdicionarViagem = findViewById(R.id.chkAdicionarViagem);
        edtCalcTotal = findViewById(R.id.edtCalcTotal);

        int totQuil = 0;
        if (!String.valueOf(edtTotQuil.getText()).isEmpty()) {
            totQuil = Integer.parseInt(String.valueOf(edtTotQuil.getText()));
        }
        if (totQuil == 0) {
            Toast.makeText(this, "Total de Quilômetros está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        int mediaLitro = 0;
        if (!String.valueOf(edtMediaLitro.getText()).isEmpty()) {
            mediaLitro = Integer.parseInt(String.valueOf(edtMediaLitro.getText()));
        }

        if (mediaLitro == 0) {
            Toast.makeText(this, "Média de Quilômetros está vazia ou zerada", Toast.LENGTH_LONG).show();
            return;
        }

        int custoLitro = 0;
        if (!String.valueOf(edtCustoLitro.getText()).isEmpty()) {
            custoLitro = Integer.parseInt(String.valueOf(edtCustoLitro.getText()));
        }

        if (custoLitro == 0) {
            Toast.makeText(this, "Custo Médio de Litros está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        int totVeic = 0;
        if (!String.valueOf(edtTotVeic.getText()).isEmpty()) {
            totVeic = Integer.parseInt(String.valueOf(edtTotVeic.getText()));
        }

        if (totVeic == 0) {
            Toast.makeText(this, "Total de Veículos está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        String bAdd = String.valueOf(chkAdicionarViagem.isChecked());
        String adicionar = bAdd.equals("true") ? "T" : "F";

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            banco.execSQL("delete from " + DB_GASOLINA + " where CH_PROVISORIO = 'T'");
            banco.execSQL("insert into " + DB_GASOLINA + " (quilometro, media, customedio, totveic, ch_add, ch_provisorio) values ('"+ totQuil +"', '"+ mediaLitro +"', '"+ custoLitro +"', '"+ totVeic +"', '"+ adicionar +"', 'T')");
            Intent intent = new Intent(this, cadTarifaAerea.class);
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
            edtTotQuil = findViewById(R.id.edtTotQuil);
            edtMediaLitro =  findViewById(R.id.edtMediaLitro);
            edtCustoLitro = findViewById(R.id.edtCustoLitro);
            edtTotVeic = findViewById(R.id.edtTotVeic);
            edtCalcTotal = findViewById(R.id.edtCalcTotal);

            int totQuil = 0;
            if (!String.valueOf(edtTotQuil.getText()).isEmpty()) {
                totQuil = Integer.parseInt(String.valueOf(edtTotQuil.getText()));
            }

            int mediaLitro = 0;
            if (!String.valueOf(edtMediaLitro.getText()).isEmpty()) {
                mediaLitro = Integer.parseInt(String.valueOf(edtMediaLitro.getText()));
            }

            int custoLitro = 0;
            if (!String.valueOf(edtCustoLitro.getText()).isEmpty()) {
               custoLitro = Integer.parseInt(String.valueOf(edtCustoLitro.getText()));
            }

            int totVeic = 0;
            if (!String.valueOf(edtTotVeic.getText()).isEmpty()) {
                totVeic = Integer.parseInt(String.valueOf(edtTotVeic.getText()));
            }

            if ((totQuil > 0) && (mediaLitro > 0) && (custoLitro > 0) && (totVeic > 0)) {
                Double calcTotal = (double) (((totQuil / mediaLitro) * custoLitro) / totVeic);
                edtCalcTotal.setText(String.valueOf(calcTotal));
            }
        }
    };
}