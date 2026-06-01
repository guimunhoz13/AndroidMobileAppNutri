package com.nutricionista.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutricionista.app.dao.AlimentoConsultaDAO;
import com.nutricionista.app.dao.AlimentoDAO;
import com.nutricionista.app.dao.ConsultaDAO;
import com.nutricionista.app.modelos.Alimento;
import com.nutricionista.app.modelos.AlimentoConsulta;
import com.nutricionista.app.modelos.Consulta;
import com.nutricionista.app.modelos.Paciente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetalheConsultaActivity extends AppCompatActivity {

    EditText edtData, edtPeso, edtAltura, edtObservacoes;
    EditText edtQuantidade;
    Spinner spnAlimentos;
    Button btnInserir, btnAtualizar, btnExcluir, btnAddAlimento;
    TextView txtIMC;
    ListView lsvAlimentos;

    Consulta consulta;
    Paciente paciente;

    ConsultaDAO cDAO;
    AlimentoDAO alimentoDAO;
    AlimentoConsultaDAO alimentoConsultaDAO;

    private final List<Alimento> listaAlimentos = new ArrayList<>();
    private final Map<String, Alimento> mapaAlimentos = new HashMap<>();

    private ArrayAdapter<Alimento> adapterSpinnerAlimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_consulta);

        edtData = findViewById(R.id.edtDataConsulta);
        edtPeso = findViewById(R.id.edtPeso);
        edtAltura = findViewById(R.id.edtAltura);
        edtObservacoes = findViewById(R.id.edtObservacoes);
        edtQuantidade = findViewById(R.id.edtQuantidade);

        spnAlimentos = findViewById(R.id.spnAlimentos);

        txtIMC = findViewById(R.id.txtIMCResultado);
        lsvAlimentos = findViewById(R.id.lsvAlimentos);

        btnInserir = findViewById(R.id.btnInserir);
        btnAtualizar = findViewById(R.id.btnAtualizar);
        btnExcluir = findViewById(R.id.btnExcluir);
        btnAddAlimento = findViewById(R.id.btnAddAlimento);

        cDAO = new ConsultaDAO();
        alimentoDAO = new AlimentoDAO();
        alimentoConsultaDAO = new AlimentoConsultaDAO();

        consulta = (Consulta) getIntent().getSerializableExtra("consulta");
        paciente = (Paciente) getIntent().getSerializableExtra("paciente");

        if (consulta == null) {
            consulta = new Consulta();
        }

        if (paciente == null) {
            Toast.makeText(this, "Paciente não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        configurarSpinnerAlimentos();
        carregarDados();
        configurarTela();
        configurarAtualizacaoIMC();
        carregarAlimentosCadastrados();
    }

    private void configurarSpinnerAlimentos() {
        adapterSpinnerAlimentos = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                listaAlimentos
        );

        adapterSpinnerAlimentos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAlimentos.setAdapter(adapterSpinnerAlimentos);
    }

    private void carregarAlimentosCadastrados() {
        alimentoDAO.ListarTodos(new AlimentoDAO.CallbackLista() {
            @Override
            public void onSucesso(List<Alimento> lista) {
                listaAlimentos.clear();
                mapaAlimentos.clear();

                listaAlimentos.addAll(lista);

                for (Alimento alimento : lista) {
                    mapaAlimentos.put(alimento.getId(), alimento);
                }

                adapterSpinnerAlimentos.notifyDataSetChanged();

                if (consulta.getId() != null && !consulta.getId().isEmpty()) {
                    ListarAlimentos();
                }
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalheConsultaActivity.this, "Erro ao carregar alimentos: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDados() {
        edtData.setText(consulta.getDataConsulta() == null ? "" : consulta.getDataConsulta());
        edtPeso.setText(consulta.getPeso() > 0 ? String.valueOf(consulta.getPeso()) : "");
        edtAltura.setText(consulta.getAltura() > 0 ? String.format(Locale.getDefault(), "%.2f", consulta.getAltura()) : "");
        edtObservacoes.setText(consulta.getObservacoes() == null ? "" : consulta.getObservacoes());

        AtualizarIMC();
    }

    private void configurarTela() {
        boolean hasId = consulta.getId() != null && !consulta.getId().isEmpty();

        btnInserir.setVisibility(hasId ? View.GONE : View.VISIBLE);
        btnAtualizar.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnExcluir.setVisibility(hasId ? View.VISIBLE : View.GONE);

        spnAlimentos.setVisibility(hasId ? View.VISIBLE : View.GONE);
        edtQuantidade.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnAddAlimento.setVisibility(hasId ? View.VISIBLE : View.GONE);
        lsvAlimentos.setVisibility(hasId ? View.VISIBLE : View.GONE);
    }

    private void configurarAtualizacaoIMC() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AtualizarIMC();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edtPeso.addTextChangedListener(watcher);
        edtAltura.addTextChangedListener(watcher);
    }

    private void AtualizarIMC() {
        try {
            String pesoTexto = edtPeso.getText().toString().replace(",", ".").trim();
            String alturaTexto = edtAltura.getText().toString().replace(",", ".").trim();

            if (pesoTexto.isEmpty() || alturaTexto.isEmpty()) {
                txtIMC.setText("IMC: --");
                return;
            }

            double peso = Double.parseDouble(pesoTexto);
            double altura = Double.parseDouble(alturaTexto);

            if (altura <= 0) {
                txtIMC.setText("IMC: --");
                return;
            }

            double imc = peso / (altura * altura);
            String classificacao;

            if (imc < 18.5) {
                classificacao = "Abaixo do peso";
            } else if (imc < 25) {
                classificacao = "Peso normal";
            } else if (imc < 30) {
                classificacao = "Sobrepeso";
            } else {
                classificacao = "Obesidade";
            }

            txtIMC.setText(String.format(Locale.getDefault(), "IMC: %.1f - %s", imc, classificacao));
        } catch (NumberFormatException e) {
            txtIMC.setText("IMC: --");
        }
    }

    private boolean validarConsulta() {
        if (edtData.getText().toString().trim().isEmpty()) {
            edtData.setError("Informe a data da consulta");
            edtData.requestFocus();
            return false;
        }

        if (edtPeso.getText().toString().trim().isEmpty()) {
            edtPeso.setError("Informe o peso");
            edtPeso.requestFocus();
            return false;
        }

        if (edtAltura.getText().toString().trim().isEmpty()) {
            edtAltura.setError("Informe a altura");
            edtAltura.requestFocus();
            return false;
        }

        return true;
    }

    private double converterDouble(EditText editText) {
        return Double.parseDouble(editText.getText().toString().replace(",", ".").trim());
    }

    private void ListarAlimentos() {
        alimentoConsultaDAO.ListarPorConsulta(consulta.getId(), new AlimentoConsultaDAO.CallbackLista() {
            @Override
            public void onSucesso(List<AlimentoConsulta> lista) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        DetalheConsultaActivity.this,
                        android.R.layout.simple_list_item_1
                );

                for (AlimentoConsulta item : lista) {
                    Alimento alimento = mapaAlimentos.get(item.getIdAlimento());

                    if (alimento != null) {
                        adapter.add(alimento.getNome() + " - " + item.getQuantidade());
                    } else {
                        adapter.add("Alimento não encontrado - " + item.getQuantidade());
                    }
                }

                if (lista.isEmpty()) {
                    adapter.add("Nenhum alimento vinculado a esta consulta.");
                }

                lsvAlimentos.setAdapter(adapter);
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalheConsultaActivity.this, "Erro: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void InserirClique(View view) {
        if (!validarConsulta()) {
            return;
        }

        try {
            Consulta c = new Consulta(
                    paciente.getId(),
                    edtData.getText().toString().trim(),
                    converterDouble(edtPeso),
                    converterDouble(edtAltura),
                    edtObservacoes.getText().toString().trim()
            );

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
            Toast.makeText(this, "Preencha peso e altura corretamente.", Toast.LENGTH_LONG).show();
        }
    }

    public void AtualizarClique(View view) {
        if (!validarConsulta()) {
            return;
        }

        try {
            consulta.setIdPaciente(paciente.getId());
            consulta.setDataConsulta(edtData.getText().toString().trim());
            consulta.setPeso(converterDouble(edtPeso));
            consulta.setAltura(converterDouble(edtAltura));
            consulta.setObservacoes(edtObservacoes.getText().toString().trim());

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
            Toast.makeText(this, "Preencha peso e altura corretamente.", Toast.LENGTH_LONG).show();
        }
    }

    public void ExcluirClique(View view) {
        cDAO.Excluir(consulta, new ConsultaDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(DetalheConsultaActivity.this, "Consulta excluída!", Toast.LENGTH_SHORT).show();
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
        if (consulta.getId() == null || consulta.getId().isEmpty()) {
            Toast.makeText(this, "Salve a consulta antes de adicionar alimentos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaAlimentos.isEmpty()) {
            Toast.makeText(this, "Cadastre alimentos primeiro.", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantidade = edtQuantidade.getText().toString().trim();

        if (quantidade.isEmpty()) {
            edtQuantidade.setError("Informe a quantidade");
            edtQuantidade.requestFocus();
            return;
        }

        Alimento alimentoSelecionado = (Alimento) spnAlimentos.getSelectedItem();

        if (alimentoSelecionado == null || alimentoSelecionado.getId() == null || alimentoSelecionado.getId().isEmpty()) {
            Toast.makeText(this, "Selecione um alimento válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlimentoConsulta alimentoConsulta = new AlimentoConsulta(
                consulta.getId(),
                alimentoSelecionado.getId(),
                quantidade
        );

        alimentoConsultaDAO.Inserir(alimentoConsulta, new AlimentoConsultaDAO.Callback() {
            @Override
            public void onSucesso() {
                edtQuantidade.setText("");
                ListarAlimentos();
                Toast.makeText(DetalheConsultaActivity.this, "Alimento vinculado à consulta!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalheConsultaActivity.this, "Erro: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }
}