package com.example.roteirofacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class listaViagens extends AppCompatActivity {
    ArrayList<HashMap<String, String>> lista;
    private static final String DB_PRINCIPAL = "principal";
    private static final String DB_DADOS = "dados";
    private static final String DB_GASOLINA = "gasolina";
    private static final String DB_TARIFA = "tarifa";
    private static final String DB_REFEICAO = "refeicao";
    private static final String DB_HOSPEDAGEM = "hospedagem";
    private static final String DB_ENTRETENIMENTO = "entretenimento";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_viagens);
        lista = new ArrayList<>();
        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor tabela = banco.rawQuery("select ID from " + DB_PRINCIPAL, null);
            int viagem = 1;
            if (tabela.moveToFirst()) {
                do {
                    Cursor dados = banco.rawQuery("select TOTPESSOAS, DIASVIAGEM from " + DB_DADOS + " where ID_PRINCIPAL = '"+ tabela.getString(0) +"'", null);
                    dados.moveToLast();

                    Double calcTotalGasolina = 0.0;
                    Cursor gasolina = banco.rawQuery("select QUILOMETRO, MEDIA, CUSTOMEDIO, TOTVEIC, CH_ADD from " + DB_GASOLINA + " where ID_PRINCIPAL = '"+ tabela.getString(0) +"'", null);
                    if (gasolina.moveToLast()) {
                        if (String.valueOf(gasolina.getString(4)).equals("T")) {
                            calcTotalGasolina = (double) ((gasolina.getInt(0) / gasolina.getInt(1)) * gasolina.getInt(2)) / gasolina.getInt(3);
                        }
                    }

                    Double calcTotalTarifa = 0.0;
                    Cursor tarifa = banco.rawQuery("select CUSTOPESSOA, ALUGUELVEIC, CH_ADD from " + DB_TARIFA + " where ID_PRINCIPAL = '"+ tabela.getString(0) +"'", null);
                    if (tarifa.moveToLast()) {
                        if (String.valueOf(tarifa.getString(2)).equals("T")) {
                            calcTotalTarifa = (double) (tarifa.getInt(0) * dados.getInt(0)) + tarifa.getInt(1);
                        }
                    }

                    Double calcTotalRefeicao = 0.0;
                    Cursor refeicao = banco.rawQuery("select CUSTOREFEICAO, REFEICAODIA, CH_ADD from " + DB_REFEICAO + " where ID_PRINCIPAL = '"+ tabela.getString(0) +"'", null);
                    if (refeicao.moveToLast()) {
                        if (String.valueOf(refeicao.getString(2)).equals("T")) {
                            calcTotalRefeicao = (double) ((refeicao.getInt(1) * dados.getInt(0)) * refeicao.getInt(0)) * dados.getInt(1);
                        }
                    }

                    Double calcTotalHospedagem = 0.0;
                    Cursor hospedagem = banco.rawQuery("select CUSTONOITE, TOTNOITE, TOTQUARTO, CH_ADD from " + DB_HOSPEDAGEM + " where ID_PRINCIPAL = '"+ tabela.getString(0) +"'", null);
                    if (hospedagem.moveToLast()) {
                        if (String.valueOf(hospedagem.getString(3)).equals("T")) {
                            calcTotalHospedagem = (double) (hospedagem.getInt(0) * hospedagem.getInt(1)) * hospedagem.getInt(2);
                        }
                    }

                    Double calcTotalEntretenimento = 0.0;
                    Cursor entretenimento = banco.rawQuery("select DESCRICAO, CUSTO from " + DB_ENTRETENIMENTO + " where ID_PRINCIPAL = '"+ tabela.getString(0) +"'", null);
                    if (entretenimento.moveToFirst()) {
                        do {
                            calcTotalEntretenimento += entretenimento.getInt(1);
                        } while (entretenimento.moveToNext());
                    }

                    Double calcTotal = calcTotalGasolina + calcTotalTarifa + calcTotalRefeicao + calcTotalHospedagem + calcTotalEntretenimento;
                    HashMap<String,String> item = new HashMap<>();
                    item.put("descricao", "Viagem " + viagem);
                    item.put("totviaj", dados.getString(0));
                    item.put("durViagem", dados.getString(1));
                    item.put("custo", String.valueOf(calcTotal));
                    lista.add(item);

                    viagem ++;
                } while (tabela.moveToNext());

                ListView lv = (ListView) findViewById(R.id.listaViagem);
                ListAdapter adapter = new SimpleAdapter(this, lista, R.layout.registro_listaviagem,new String[]{"descricao", "totviaj", "durViagem","custo"}, new int[]{R.id.descricao, R.id.totviaj, R.id.durViagem, R.id.valor});
                lv.setAdapter(adapter);
            }
        } catch (Exception e) {

        }

        ListView lv = (ListView) findViewById(R.id.listaViagem);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), relatorio.class);
                intent.putExtra("id", position + 1);
                startActivityForResult(intent, 0);
                finish();
            }
        });
    }

    public void funcNovaViagem(View view) {
        SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
        banco.execSQL("delete from " + DB_GASOLINA + " where CH_PROVISORIO = 'T'");
        banco.execSQL("delete from " + DB_DADOS + " where CH_PROVISORIO = 'T'");
        banco.execSQL("delete from " + DB_TARIFA + " where CH_PROVISORIO = 'T'");
        banco.execSQL("delete from " + DB_REFEICAO + " where CH_PROVISORIO = 'T'");
        banco.execSQL("delete from " + DB_HOSPEDAGEM + " where CH_PROVISORIO = 'T'");
        banco.execSQL("delete from " + DB_ENTRETENIMENTO + " where CH_PROVISORIO = 'T'");

        Intent intent = new Intent(this, cadDados.class);
        startActivity(intent);
        finish();
    }
}