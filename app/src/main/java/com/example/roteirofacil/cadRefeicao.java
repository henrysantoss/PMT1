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

public class cadRefeicao extends AppCompatActivity {
    private static final String DB_REFEICAO = "refeicao";
    EditText edtCustoRefeicao;
    EditText edtRefeicaoDia;
    CheckBox chkAdicionarViagem;
    EditText edtCalcTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_refeicao);
        edtCustoRefeicao = findViewById(R.id.edtCustoRefeicao);
        edtRefeicaoDia =  findViewById(R.id.edtRefeicaoDia);
        chkAdicionarViagem = findViewById(R.id.chkAdicionarViagem);

        edtCustoRefeicao.addTextChangedListener(textWatcher);
        edtRefeicaoDia.addTextChangedListener(textWatcher);

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select CUSTOREFEICAO, REFEICAODIA, CH_ADD, CH_PROVISORIO from " + DB_REFEICAO + " where CH_PROVISORIO = 'T'", null);
            if (tabela.moveToLast()) {
                edtCustoRefeicao.setText(tabela.getString(0));
                edtRefeicaoDia.setText(tabela.getString(1));
                String add = String.valueOf(tabela.getString(2));
                chkAdicionarViagem.setChecked(add.equals("T"));
            }
        } catch (Exception e) {

        }

    }

    public void funcVoltar(View view) {
        Intent intent = new Intent(this, cadTarifaAerea.class);
        startActivity(intent);
    }
    public void funcAvancar(View view) {
        edtCustoRefeicao = findViewById(R.id.edtCustoRefeicao);
        edtRefeicaoDia =  findViewById(R.id.edtRefeicaoDia);
        chkAdicionarViagem = findViewById(R.id.chkAdicionarViagem);

        int custoRefeicao = 0;
        if (!String.valueOf(edtCustoRefeicao.getText()).isEmpty()) {
            custoRefeicao = Integer.parseInt(String.valueOf(edtCustoRefeicao.getText()));
        }

        if (custoRefeicao == 0) {
            Toast.makeText(this, "Custo Médio de Refeições está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        int refeicaoDia = 0;
        if (!String.valueOf(edtRefeicaoDia.getText()).isEmpty()) {
            refeicaoDia = Integer.parseInt(String.valueOf(edtRefeicaoDia.getText()));
        }

        if (refeicaoDia == 0) {
            Toast.makeText(this, "Total de Refeições está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        String bAdd = String.valueOf(chkAdicionarViagem.isChecked());
        String adicionar = bAdd.equals("true") ? "T" : "F";

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            banco.execSQL("delete from " + DB_REFEICAO + " where CH_PROVISORIO = 'T'");
            banco.execSQL("insert into " + DB_REFEICAO + " (custorefeicao, refeicaodia, ch_add, ch_provisorio) values ('"+ custoRefeicao +"', '"+ refeicaoDia +"', '"+ adicionar +"', 'T')");
            Intent intent = new Intent(this, cadHospedagem.class);
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
            edtCustoRefeicao = findViewById(R.id.edtCustoRefeicao);
            edtRefeicaoDia =  findViewById(R.id.edtRefeicaoDia);
            edtCalcTotal = findViewById(R.id.edtCalcTotal);

            try {
                SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
                Cursor tabela = banco.rawQuery("select TOTPESSOAS, DIASVIAGEM from dados where CH_PROVISORIO = 'T'", null);
                if (tabela.moveToLast()) {
                    int custoRefeicao = 0;
                    if (!String.valueOf(edtCustoRefeicao.getText()).isEmpty()) {
                        custoRefeicao = Integer.parseInt(String.valueOf(edtCustoRefeicao.getText()));
                    }

                    int refeicaoDia = 0;
                    if (!String.valueOf(edtRefeicaoDia.getText()).isEmpty()) {
                        refeicaoDia = Integer.parseInt(String.valueOf(edtRefeicaoDia.getText()));
                    }

                    if ((custoRefeicao > 0) && (refeicaoDia > 0)) {
                        Double calcTotal = (double) (((refeicaoDia * tabela.getInt(0)) * custoRefeicao) * tabela.getInt(1));
                        edtCalcTotal.setText(String.valueOf(calcTotal));
                    }
                }
            } catch (Exception e) {
                String erro = e.getMessage();
            }
        }
    };
}