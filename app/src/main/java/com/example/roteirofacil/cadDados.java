package com.example.roteirofacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class cadDados extends AppCompatActivity {
    private static final String DB_DADOS = "dados";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_dados);
        EditText edtTotViajantes = (EditText)findViewById(R.id.edtTotViajantes);
        EditText edtDurViagem = (EditText)findViewById(R.id.edtDurViagem);

        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select TOTPESSOAS, DIASVIAGEM from " + DB_DADOS + " where CH_PROVISORIO = 'T'", null);
            if (tabela.moveToLast()) {
                edtTotViajantes.setText(tabela.getString(0));
                edtDurViagem.setText(tabela.getString(1));
            }
        } catch (Exception e) {

        }
    }

    public void funcVoltar(View view) {
        Intent intent = new Intent(this, listaViagens.class);
        startActivity(intent);
    }

    public void funcAvancar(View view) {
        EditText edtTotViajantes = (EditText)findViewById(R.id.edtTotViajantes);
        EditText edtDurViagem = (EditText)findViewById(R.id.edtDurViagem);
        try {
            String lsTotViajantes = String.valueOf(edtTotViajantes.getText());
            String lsDurViagem = String.valueOf(edtDurViagem.getText());
            if (lsTotViajantes.isEmpty()) {
                Toast.makeText(this, "É necessário ter um Total de Viajantes", Toast.LENGTH_LONG).show();
                return;
            }
            if (lsDurViagem.isEmpty()) {
                Toast.makeText(this, "É necessário ter uma Duração da Viagem", Toast.LENGTH_LONG).show();
                return;
            }
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            banco.execSQL("delete from " + DB_DADOS + " where CH_PROVISORIO = 'T'");
            banco.execSQL("insert into " + DB_DADOS + " (totpessoas, diasViagem, custoTotal, custoPessoa, ch_provisorio) values ('"+ lsTotViajantes +"', '"+ lsDurViagem +"', '', '', 'T')");
            Intent intent = new Intent(this, cadGasolina.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}