package com.example.roteirofacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int DB_VERSAO= 1;
    private static final String DB_NOME = "roteiro";
    private static final String DB_PRINCIPAL = "principal";
    private static final String DB_DADOS = "dados";
    private static final String DB_GASOLINA = "gasolina";
    private static final String DB_TARIFA = "tarifa";
    private static final String DB_REFEICAO = "refeicao";
    private static final String DB_HOSPEDAGEM = "hospedagem";
    private static final String DB_ENTRETENIMENTO = "entretenimento";
    public static final String DB_USUARIOS = "usuarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
        CriaOuReseta(banco);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    void CriaTabelas(SQLiteDatabase banco) {
        String PRINCIPAL = "CREATE TABLE IF NOT EXISTS " + DB_PRINCIPAL + "(id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT)";
        String DADOS = "CREATE TABLE IF NOT EXISTS " + DB_DADOS + "(id INTEGER PRIMARY KEY AUTOINCREMENT, id_principal TEXT, totpessoas TEXT, diasViagem TEXT, custoTotal TEXT, custoPessoa TEXT, ch_provisorio TEXT)";
        String GASOLINA = "CREATE TABLE IF NOT EXISTS " + DB_GASOLINA + "(id INTEGER PRIMARY KEY AUTOINCREMENT, id_principal TEXT,  quilometro TEXT, media TEXT, customedio TEXT, totveic TEXT, ch_add TEXT, ch_provisorio TEXT)";
        String TARIFA = "CREATE TABLE IF NOT EXISTS " + DB_TARIFA + "(id INTEGER PRIMARY KEY AUTOINCREMENT, id_principal TEXT,  custopessoa TEXT, aluguelveic TEXT, ch_add TEXT, ch_provisorio TEXT)";
        String REFEICAO = "CREATE TABLE IF NOT EXISTS " + DB_REFEICAO + "(id INTEGER PRIMARY KEY AUTOINCREMENT, id_principal TEXT,  custorefeicao TEXT, refeicaodia TEXT, ch_add TEXT, ch_provisorio TEXT)";
        String HOSPEDAGEM = "CREATE TABLE IF NOT EXISTS " + DB_HOSPEDAGEM + "(id INTEGER PRIMARY KEY AUTOINCREMENT, id_principal TEXT,  custonoite TEXT, totnoite TEXT, totquarto TEXT, ch_add TEXT, ch_provisorio TEXT)";
        String ENTRETENIMENTO = "CREATE TABLE IF NOT EXISTS " + DB_ENTRETENIMENTO + "(id INTEGER PRIMARY KEY AUTOINCREMENT, id_principal TEXT,  descricao TEXT, custo TEXT, ch_provisorio TEXT)";
        String USUARIO = "CREATE TABLE IF NOT EXISTS " + DB_USUARIOS + "(id INTEGER PRIMARY KEY AUTOINCREMENT, username text not null, senha text not null)";
        banco.execSQL(PRINCIPAL);
        banco.execSQL(DADOS);
        banco.execSQL(GASOLINA);
        banco.execSQL(TARIFA);
        banco.execSQL(REFEICAO);
        banco.execSQL(HOSPEDAGEM);
        banco.execSQL(ENTRETENIMENTO);
        banco.execSQL(USUARIO);
    }

    void CriaOuReseta(SQLiteDatabase db) {
        CriaTabelas(db);
    }
}