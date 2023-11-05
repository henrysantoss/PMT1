package com.example.roteirofacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.logging.Logger;

public class cadTarifaAerea extends AppCompatActivity {

    private static final String DB_TARIFA = "tarifa";
    EditText edtCustoPessoa;
    EditText edtAluguel;
    CheckBox chkAdicionarViagem;
    EditText edtCalcTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_tarifa_aerea);
        edtCustoPessoa = findViewById(R.id.edtCustoPessoa);
        edtAluguel =  findViewById(R.id.edtAluguel);
        chkAdicionarViagem = findViewById(R.id.chkAdicionarViagem);

        edtCustoPessoa.addTextChangedListener(textWatcher);
        edtAluguel.addTextChangedListener(textWatcher);

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select CUSTOPESSOA, ALUGUELVEIC, CH_ADD, CH_PROVISORIO from " + DB_TARIFA + " where CH_PROVISORIO = 'T'", null);
            if (tabela.moveToLast()) {
                edtCustoPessoa.setText(tabela.getString(0));
                edtAluguel.setText(tabela.getString(1));
                String add = String.valueOf(tabela.getString(2));
                chkAdicionarViagem.setChecked(add.equals("T"));
            }
        } catch (Exception e) {

        }
    }

    public void funcVoltar(View view) {
        Intent intent = new Intent(this, cadGasolina.class);
        startActivity(intent);
    }
    public void funcAvancar(View view) {
        edtCustoPessoa = findViewById(R.id.edtCustoPessoa);
        edtAluguel =  findViewById(R.id.edtAluguel);
        chkAdicionarViagem = findViewById(R.id.chkAdicionarViagem);

        int custoPessoa = 0;
        if (!String.valueOf(edtCustoPessoa.getText()).isEmpty()) {
            custoPessoa = Integer.parseInt(String.valueOf(edtCustoPessoa.getText()));
        }

        if (custoPessoa == 0) {
            Toast.makeText(this, "Custo Médio por Pessoas está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        int aluguel = 0;
        if (!String.valueOf(edtAluguel.getText()).isEmpty()) {
            aluguel = Integer.parseInt(String.valueOf(edtAluguel.getText()));
        }

        if (aluguel == 0) {
            Toast.makeText(this, "Custo do Aluguel está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        String bAdd = String.valueOf(chkAdicionarViagem.isChecked());
        String adicionar = bAdd.equals("true") ? "T" : "F";

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            banco.execSQL("delete from " + DB_TARIFA + " where CH_PROVISORIO = 'T'");
            banco.execSQL("insert into " + DB_TARIFA + " (custopessoa, aluguelveic, ch_add, ch_provisorio) values ('"+ custoPessoa +"', '"+ aluguel +"', '"+ adicionar +"', 'T')");
            Intent intent = new Intent(this, cadRefeicao.class);
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
            edtCustoPessoa = findViewById(R.id.edtCustoPessoa);
            edtAluguel =  findViewById(R.id.edtAluguel);
            edtCalcTotal = findViewById(R.id.edtCalcTotal);

            try {
                SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
                Cursor tabela = banco.rawQuery("select TOTPESSOAS from dados where CH_PROVISORIO = 'T'", null);
                if (tabela.moveToLast()) {
                    int custoPessoa = 0;
                    if (!String.valueOf(edtCustoPessoa.getText()).isEmpty()) {
                        custoPessoa = Integer.parseInt(String.valueOf(edtCustoPessoa.getText()));
                    }

                    int aluguel = 0;
                    if (!String.valueOf(edtAluguel.getText()).isEmpty()) {
                        aluguel = Integer.parseInt(String.valueOf(edtAluguel.getText()));
                    }

                    if ((custoPessoa > 0) && (aluguel > 0)) {
                        Double calcTotal = (double) ((custoPessoa * tabela.getInt(0)) + aluguel);
                        edtCalcTotal.setText(String.valueOf(calcTotal));
                    }
                }
            } catch (Exception e) {
                String lsTeste = e.getMessage();
            }
        }
    };
}