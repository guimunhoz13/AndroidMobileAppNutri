package com.nutricionista.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nutricionista.app.dao.AlimentoConsultaDAO;
import com.nutricionista.app.dao.ConsultaDAO;
import com.nutricionista.app.modelos.AlimentoConsulta;
import com.nutricionista.app.modelos.Consulta;
import com.nutricionista.app.modelos.Paciente;
import java.util.List;
import java.util.Locale;

public class DetalheConsultaActivity extends AppCompatActivity {

    EditText edtData, edtPeso, edtAltura, edtObservacoes;
    EditText edtAlimento, edtQuantidade, edtCalorias;
    Button btnInserir, btnAtualizar, btnExcluir, btnAddAlimento;
    TextView txtIMC;
    ListView lsvAlimentos;
    Consulta consulta;
    Paciente paciente;
    ConsultaDAO cDAO;
    AlimentoConsultaDAO aDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_consulta);

        edtData = (EditText) findViewById(R.id.edtDataConsulta);
        edtPeso = (EditText) findViewById(R.id.edtPeso);
        edtAltura = (EditText) findViewById(R.id.edtAltura);
        edtObservacoes = (EditText) findViewById(R.id.edtObservacoes);
        edtAlimento = (EditText) findViewById(R.id.edtAlimento);
        edtQuantidade = (EditText) findViewById(R.id.edtQuantidade);
        edtCalorias = (EditText) findViewById(R.id.edtCalorias);
        txtIMC = (TextView) findViewById(R.id.txtIMCResultado);
        lsvAlimentos = (ListView) findViewById(R.id.lsvAlimentos);
        btnInserir = (Button) findViewById(R.id.btnInserir);
        btnAtualizar = (Button) findViewById(R.id.btnAtualizar);
        btnExcluir = (Button) findViewById(R.id.btnExcluir);
        btnAddAlimento = (Button) findViewById(R.id.btnAddAlimento);

        cDAO = new ConsultaDAO();
        aDAO = new AlimentoConsultaDAO();
        consulta = (Consulta) getIntent().getSerializableExtra("consulta");
        paciente = (Paciente) getIntent().getSerializableExtra("paciente");

        edtData.setText(consulta.getDataConsulta());
        edtPeso.setText(consulta.getPeso() > 0 ? String.valueOf(consulta.getPeso()) : "");
        edtAltura.setText(consulta.getAltura() > 0 ? String.format(Locale.getDefault(), "%.2f", consulta.getAltura()) : "");
        edtObservacoes.setText(consulta.getObservacoes());

        boolean hasId = consulta.getId() != null && !consulta.getId().isEmpty();
        btnInserir.setVisibility(hasId ? View.GONE : View.VISIBLE);
        btnAtualizar.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnExcluir.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnAddAlimento.setVisibility(hasId ? View.VISIBLE : View.GONE);
        lsvAlimentos.setVisibility(hasId ? View.VISIBLE : View.GONE);

        if (hasId) { AtualizarIMC(); ListarAlimentos(); }
    }

    private void AtualizarIMC() {
        try {
            double peso = Double.parseDouble(edtPeso.getText().toString());
            double altura = Double.parseDouble(edtAltura.getText().toString());
            if (altura > 0) {
                double imc = peso / (altura * altura);
                String cls = imc < 18.5 ? "Abaixo do peso" : imc < 25 ? "Peso normal" : imc < 30 ? "Sobrepeso" : "Obesidade";
                txtIMC.setText(String.format(Locale.getDefault(), "IMC: %.1f - %s", imc, cls));
            }
        } catch (NumberFormatException e) { txtIMC.setText("IMC: --"); }
    }

    private void ListarAlimentos() {
        aDAO.ListarPorConsulta(consulta.getId(), new AlimentoConsultaDAO.CallbackLista() {
            @Override
            public void onSucesso(List<AlimentoConsulta> lista) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DetalheConsultaActivity.this, android.R.layout.simple_list_item_1);
                double total = 0;
                for (AlimentoConsulta a : lista) {
                    adapter.add(a.getNomeAlimento() + " - " + a.getQuantidade() + " (" + a.getCalorias() + " kcal)");
                    total += a.getCalorias();
                }
                adapter.add("Total: " + String.format(Locale.getDefault(), "%.0f kcal", total));
                lsvAlimentos.setAdapter(adapter);
            }
            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalheConsultaActivity.this, "Erro: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void InserirClique(View view) {
        try {
            Consulta c = new Consulta(
                    consulta.getIdPaciente(),
                    edtData.getText().toString(),
                    Double.parseDouble(edtPeso.getText().toString()),
                    Double.parseDouble(edtAltura.getText().toString()),
                    edtObservacoes.getText().toString());

            cDAO.Inserir(c, new ConsultaDAO.Callback() {
                @Override
                public void onSucesso() {
                    Toast.makeText(DetalheConsultaActivity.this, "Consulta cadastrada!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                @Override
                public void onErro(String mensagem) {
                    Toast.makeText(DetalheConsultaActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Preencha peso e altura corretamente", Toast.LENGTH_LONG).show();
        }
    }

    public void AtualizarClique(View view) {
        try {
            consulta.setDataConsulta(edtData.getText().toString());
            consulta.setPeso(Double.parseDouble(edtPeso.getText().toString()));
            consulta.setAltura(Double.parseDouble(edtAltura.getText().toString()));
            consulta.setObservacoes(edtObservacoes.getText().toString());

            cDAO.Atualizar(consulta, new ConsultaDAO.Callback() {
                @Override
                public void onSucesso() {
                    AtualizarIMC();
                    Toast.makeText(DetalheConsultaActivity.this, "Consulta atualizada!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                }
                @Override
                public void onErro(String mensagem) {
                    Toast.makeText(DetalheConsultaActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Preencha peso e altura corretamente", Toast.LENGTH_LONG).show();
        }
    }

    public void ExcluirClique(View view) {
        cDAO.Excluir(consulta, new ConsultaDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(DetalheConsultaActivity.this, "Consulta excluida!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalheConsultaActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void AddAlimentoClique(View view) {
        String nome = edtAlimento.getText().toString().trim();
        String qtd = edtQuantidade.getText().toString().trim();
        String cal = edtCalorias.getText().toString().trim();

        if (nome.isEmpty() || qtd.isEmpty() || cal.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos do alimento", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            AlimentoConsulta a = new AlimentoConsulta(consulta.getId(), nome, qtd, Double.parseDouble(cal));
            aDAO.Inserir(a, new AlimentoConsultaDAO.Callback() {
                @Override
                public void onSucesso() {
                    edtAlimento.setText(""); edtQuantidade.setText(""); edtCalorias.setText("");
                    ListarAlimentos();
                    Toast.makeText(DetalheConsultaActivity.this, "Alimento adicionado!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onErro(String mensagem) {
                    Toast.makeText(DetalheConsultaActivity.this, "Erro: " + mensagem, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Calorias invalidas", Toast.LENGTH_SHORT).show();
        }
    }
}
