package com.example.roteirofacil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.roteirofacil.api.API;
import com.example.roteirofacil.model.Entreterimento;
import com.example.roteirofacil.model.Gasolina;
import com.example.roteirofacil.model.Hospedagem;
import com.example.roteirofacil.model.Refeicao;
import com.example.roteirofacil.model.Resposta;
import com.example.roteirofacil.model.Viagem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class cadEntretenimento extends AppCompatActivity {
    ArrayList<HashMap<String, String>> lista;
    EditText edtDescricao;
    EditText edtCusto;
    EditText edtCalcTotal;

    String idGlobal;

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
        setContentView(R.layout.activity_cad_entretenimento);
        lista = new ArrayList<>();

        ListView lv = (ListView) findViewById(R.id.lista);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Remova o item da lista com base na posição do item clicado
                lista.remove(position);
                ListAdapter adapter = new SimpleAdapter(cadEntretenimento.this, lista, R.layout.registro_lista,new String[]{"descricao","custo"}, new int[]{R.id.descricao, R.id.valor});
                lv.setAdapter(adapter);

                Double calcTotal = 0.0;
                for (HashMap<String, String> hashMap : lista) {
                    String valor = hashMap.get("custo");
                    if (valor != null) {
                        calcTotal += Integer.parseInt(valor);
                    }
                }
                edtCalcTotal = findViewById(R.id.edtCalcTotal);
                edtCalcTotal.setText(String.valueOf(calcTotal));
            }
        });
    }

    public void funcAdd(View view) {
        edtDescricao = findViewById(R.id.edtDescricao);
        edtCusto = findViewById(R.id.edtCusto);

        String desc = String.valueOf(edtDescricao.getText());
        String custo = String.valueOf(edtCusto.getText());

        if (desc.isEmpty()) {
            Toast.makeText(this, "Descrição está vazia", Toast.LENGTH_LONG).show();
            return;
        }

        if (custo.isEmpty()) {
            Toast.makeText(this, "Custo está vazio ou zerado", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String,String> item = new HashMap<>();
        item.put("descricao", desc);
        item.put("custo", custo);
        lista.add(item);

        edtDescricao.setText("");
        edtCusto.setText("");
        ListView lv = (ListView) findViewById(R.id.lista);
        ListAdapter adapter = new SimpleAdapter(this, lista, R.layout.registro_lista,new String[]{"descricao","custo"}, new int[]{R.id.descricao, R.id.valor});
        lv.setAdapter(adapter);

        Double calcTotal = 0.0;
        for (HashMap<String, String> hashMap : lista) {
            String valor = hashMap.get("custo");
            if (valor != null) {
                calcTotal += Integer.parseInt(valor);
            }
        }
        edtCalcTotal = findViewById(R.id.edtCalcTotal);
        edtCalcTotal.setText(String.valueOf(calcTotal));
    }

    public void funcVoltar(View view) {
        Intent intent = new Intent(this, cadHospedagem.class);
        startActivity(intent);
    }

    public void funcFinalizar(View view) {
        try {
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            banco.execSQL("delete from " + DB_ENTRETENIMENTO + " where CH_PROVISORIO = 'T'");
            for (HashMap<String, String> hashMap : lista) {
                String descricao = hashMap.get("descricao");
                String valor = hashMap.get("custo");
                if ((valor != null) && (descricao != null)) {
                    banco.execSQL("insert into " + DB_ENTRETENIMENTO + " (descricao, custo, ch_provisorio) values ('"+ descricao +"', '"+ valor +"', 'T')");
                }
            }
            Date dataAtual = new Date();
            String formato = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
            String dataAtualString = sdf.format(dataAtual);

            ContentValues principal = new ContentValues();
            principal.put("data", dataAtualString);
            long id = banco.insert("principal", null, principal);
            banco.execSQL("update " + DB_DADOS + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_GASOLINA + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_REFEICAO + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_TARIFA + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_REFEICAO + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_HOSPEDAGEM + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");
            banco.execSQL("update " + DB_ENTRETENIMENTO + " set id_principal = '"+ id +"', ch_provisorio = 'F' where CH_PROVISORIO = 'T'");

            enviaAPI(String.valueOf(id));

            Intent intent = new Intent(view.getContext(), relatorio.class);
            int intId = (int) id;
            intent.putExtra("id", intId);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public Cursor enviaAPI(String id) {
        idGlobal = id;
        SendDataAsyncTask sendDataAsyncTask = new SendDataAsyncTask();
        sendDataAsyncTask.execute();
        return null;
    }

    private class SendDataAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... Voids) {
            Viagem v = new Viagem();
            int pessoas = 0;
            Double gasolinaTotal = 0.0;
            Double refeicaoTotal = 0.0;
            Double hospedagemTotal = 0.0;
            Double entrenimentoTotal = 0.0;
            SQLiteDatabase banco = openOrCreateDatabase("viagem", MODE_PRIVATE, null);
            Cursor dados = banco.rawQuery("select TOTPESSOAS, DIASVIAGEM from " + DB_DADOS + " where ID_PRINCIPAL = '"+ idGlobal +"'", null);
            try {
                if (dados.moveToLast()) {
                    pessoas = Integer.parseInt(dados.getString(0));
                    v.setIdConta(123806);
                    v.setLocal("RJ");
                    v.setTotalViajantes(Integer.parseInt(dados.getString(0)));
                    v.setDuracaoViagem(Integer.parseInt(dados.getString(1)));
                }
            } catch (Exception e) {

            }

            Gasolina g = new Gasolina();
            try {
                Cursor tabela = banco.rawQuery("select QUILOMETRO, MEDIA, CUSTOMEDIO, TOTVEIC, CH_ADD, CH_PROVISORIO from " + DB_GASOLINA + " where ID_PRINCIPAL = '"+ idGlobal +"'", null);
                if (tabela.moveToLast()) {
                    if (String.valueOf(tabela.getString(4)).equals("T")) {
                        g.setTotalEstimadoKM(Integer.parseInt(tabela.getString(0)));
                        g.setCustoMedioLitro(Double.parseDouble(tabela.getString(1)));
                        g.setMediaKMLitro(Double.parseDouble(tabela.getString(2)));
                        g.setTotalVeiculos(Integer.parseInt(tabela.getString(3)));

                        gasolinaTotal = (double) (((tabela.getInt(0) / tabela.getInt(1)) * tabela.getInt(2)) / tabela.getInt(3));
                    }
                }
            } catch (Exception e) {

            }
            Cursor tarifa = banco.rawQuery("select CUSTOPESSOA, ALUGUELVEIC, CH_ADD, CH_PROVISORIO from " + DB_TARIFA + " where ID_PRINCIPAL = '"+ idGlobal +"'", null);
            try {
                if (tarifa.moveToLast()) {
                }
            } catch (Exception e) {

            }

            Refeicao r = new Refeicao();

            try {
                Cursor tabela = banco.rawQuery("select CUSTOREFEICAO, REFEICAODIA, CH_ADD, CH_PROVISORIO from " + DB_REFEICAO + " where ID_PRINCIPAL = '"+ idGlobal +"'", null);
                if (tabela.moveToLast()) {
                    if (String.valueOf(tabela.getString(2)).equals("T")) {
                        r.setCustoRefeicao(Double.parseDouble(tabela.getString(0)));
                        r.setRefeicoesDia(Integer.parseInt(tabela.getString(1)));

                        refeicaoTotal = (double) (((tabela.getInt(1) * dados.getInt(0)) * tabela.getInt(0)) * dados.getInt(1));
                    }
                }
            } catch (Exception e) {

            }
            Hospedagem h = new Hospedagem();
            try {
                Cursor tabela = banco.rawQuery("select CUSTONOITE, TOTNOITE, TOTQUARTO, CH_ADD, CH_PROVISORIO from " + DB_HOSPEDAGEM + " where ID_PRINCIPAL = '"+ idGlobal +"'", null);
                if (tabela.moveToLast()) {
                    if (String.valueOf(tabela.getString(3)).equals("T")) {
                        h.setCustoMedioNoite(Double.parseDouble(tabela.getString(0)));
                        h.setTotalNoite(Integer.parseInt(tabela.getString(1)));
                        h.setTotalQuartos(Integer.parseInt(tabela.getString(2)));

                        hospedagemTotal = (double) ((tabela.getInt(0) * tabela.getInt(1)) * tabela.getInt(2));
                    }
                }
            } catch (Exception e) {

            }

            JSONArray arrayEnt = new JSONArray();
            ArrayList<Entreterimento> listaEntretenimento = new ArrayList<Entreterimento>();
            try {
                Cursor tabela = banco.rawQuery("select DESCRICAO, CUSTO from " + DB_ENTRETENIMENTO + " where ID_PRINCIPAL = '"+ idGlobal +"'", null);
                if (tabela.moveToFirst()) {
                    do {
                        Entreterimento e = new Entreterimento();
                        e.setEntretenimento(tabela.getString(0));
                        e.setValor(Double.parseDouble(tabela.getString(1)));
                        listaEntretenimento.add(e);
                        entrenimentoTotal += tabela.getInt(1);

                        JSONObject ent = new JSONObject();
                        ent.put("valor", tabela.getString(1));
                        ent.put("entretenimento", tabela.getString(0));


                        arrayEnt.put(ent);
                    } while (tabela.moveToNext());
                }
            } catch (Exception e) {

            }
            Double total = gasolinaTotal + refeicaoTotal + hospedagemTotal + entrenimentoTotal;
            v.setCustoPorPessoa(total / pessoas);
            v.setCustoTotalViagem(total);
            v.setGasolina(g);
            v.setHospedagem(h);
            v.setRefeicao(r);
            v.setListaEntretenimento(listaEntretenimento);


            try {
//                API.postViagem(v, new Callback<Resposta>() {
//                        @Override
//                        public void onResponse(Call<Resposta> call, Response<Resposta> response) {
//                            if (response != null && response.isSuccessful()) {
//
//                                Resposta r = response.body();
//                                r.getDado();
//                                r.getMensagem();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Resposta> call, Throwable t) {
//                        }
//                    });

                URL url = new URL("http://api.genialsaude.com.br/api/cadastro/viagem");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                JSONObject gasolina = new JSONObject();
                gasolina.put("totalEstimadoKM", g.getTotalEstimadoKM());
                gasolina.put("mediaKMLitro", g.getCustoMedioLitro());
                gasolina.put("custoMedioLitro", g.getMediaKMLitro());
                gasolina.put("custoPorPessoa", gasolinaTotal);

                JSONObject tarifaJSON = new JSONObject();
                tarifaJSON.put("custoPessoa", tarifa.getString(0));
                tarifaJSON.put("custoAluguelVeiculo", tarifa.getString(1));

                JSONObject hospedagemJSON = new JSONObject();
                hospedagemJSON.put("custoMedioNoite", h.getCustoMedioNoite());
                hospedagemJSON.put("totalNoite", h.getTotalNoite());
                hospedagemJSON.put("totalQuartos", h.getTotalQuartos());

                JSONObject refeicaoJSON = new JSONObject();
                refeicaoJSON.put("custoRefeicao", r.getCustoRefeicao());
                refeicaoJSON.put("refeicoesDia", r.getRefeicoesDia());

                JSONObject viagemJSON = new JSONObject();
                viagemJSON.put("gasolina", gasolina);
                viagemJSON.put("aereo", tarifaJSON);
                viagemJSON.put("listaEntretenimento", arrayEnt);
                viagemJSON.put("hospedagem", hospedagemJSON);
                viagemJSON.put("refeicao", refeicaoJSON);
                viagemJSON.put("totalViajantes", v.getTotalViajantes());
                viagemJSON.put("duracaoViagem", v.getDuracaoViagem());
                viagemJSON.put("custoTotalViagem", v.getCustoTotalViagem());
                viagemJSON.put("custoPorPessoa", v.getCustoPorPessoa());
                viagemJSON.put("local", "RJ");
                viagemJSON.put("idConta", 123806);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.writeBytes(viagemJSON.toString());
                    wr.flush();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Resposta da API: " + response.toString());

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                System.out.println("Resposta da API: " + result);
            }
        }
    }
}
