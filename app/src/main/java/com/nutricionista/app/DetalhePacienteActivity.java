package com.nutricionista.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.nutricionista.app.dao.PacienteDAO;
import com.nutricionista.app.modelos.Paciente;

public class DetalhePacienteActivity extends AppCompatActivity {

    EditText edtNome, edtDataNascimento, edtTelefone, edtObjetivo;
    Button btnInserir, btnAtualizar, btnExcluir, btnConsultas, btnPlanoAlimentar;

    Paciente paciente;
    PacienteDAO pDAO;
    ActivityResultLauncher<Intent> consultasLauncher;
    ActivityResultLauncher<Intent> planoLauncher;

    private boolean formatandoTelefone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_paciente);

        edtNome = findViewById(R.id.edtNome);
        edtDataNascimento = findViewById(R.id.edtDataNascimento);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtObjetivo = findViewById(R.id.edtObjetivo);

        btnInserir = findViewById(R.id.btnInserir);
        btnAtualizar = findViewById(R.id.btnAtualizar);
        btnExcluir = findViewById(R.id.btnExcluir);
        btnConsultas = findViewById(R.id.btnConsultas);
        btnPlanoAlimentar = findViewById(R.id.btnPlanoAlimentar);

        pDAO = new PacienteDAO();

        paciente = (Paciente) getIntent().getSerializableExtra("paciente");
        if (paciente == null) {
            paciente = new Paciente();
        }

        configurarMascaraTelefone();
        carregarDadosPaciente();
        configurarBotoes();

        consultasLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {}
        );

        planoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {}
        );
    }

    private void carregarDadosPaciente() {
        edtNome.setText(paciente.getNome() == null ? "" : paciente.getNome());
        edtDataNascimento.setText(paciente.getDataNascimento() == null ? "" : paciente.getDataNascimento());
        edtTelefone.setText(paciente.getTelefone() == null ? "" : paciente.getTelefone());
        edtObjetivo.setText(paciente.getObjetivoNutricional() == null ? "" : paciente.getObjetivoNutricional());
    }

    private void configurarBotoes() {
        boolean hasId = paciente.getId() != null && !paciente.getId().isEmpty();

        btnInserir.setVisibility(hasId ? View.GONE : View.VISIBLE);
        btnAtualizar.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnExcluir.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnConsultas.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnPlanoAlimentar.setVisibility(hasId ? View.VISIBLE : View.GONE);
    }

    private void configurarMascaraTelefone() {
        edtTelefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (formatandoTelefone) {
                    return;
                }

                formatandoTelefone = true;

                String numeros = s.toString().replaceAll("[^0-9]", "");

                if (numeros.startsWith("55")) {
                    numeros = numeros.substring(2);
                }

                if (numeros.length() > 11) {
                    numeros = numeros.substring(0, 11);
                }

                String telefoneFormatado = formatarTelefone(numeros);

                edtTelefone.setText(telefoneFormatado);
                edtTelefone.setSelection(edtTelefone.getText().length());

                formatandoTelefone = false;
            }
        });
    }

    private String formatarTelefone(String numeros) {
        StringBuilder resultado = new StringBuilder("+55 ");

        if (numeros.length() == 0) {
            return resultado.toString();
        }

        resultado.append("(");

        if (numeros.length() < 2) {
            resultado.append(numeros);
            return resultado.toString();
        }

        resultado.append(numeros.substring(0, 2)).append(") ");

        String numeroSemDDD = numeros.substring(2);

        if (numeroSemDDD.length() <= 5) {
            resultado.append(numeroSemDDD);
        } else {
            resultado.append(numeroSemDDD.substring(0, 5));
            resultado.append("-");
            resultado.append(numeroSemDDD.substring(5));
        }

        return resultado.toString();
    }

    private boolean validarCampos() {
        String nome = edtNome.getText().toString().trim();
        String telefone = edtTelefone.getText().toString().trim();

        if (nome.isEmpty()) {
            edtNome.setError("Informe o nome do paciente");
            edtNome.requestFocus();
            return false;
        }

        String apenasNumeros = telefone.replaceAll("[^0-9]", "");
        if (apenasNumeros.length() < 13) {
            edtTelefone.setError("Informe um telefone válido");
            edtTelefone.requestFocus();
            return false;
        }

        return true;
    }

    public void InserirClique(View view) {
        if (!validarCampos()) {
            return;
        }

        Paciente p = new Paciente(
                edtNome.getText().toString().trim(),
                edtDataNascimento.getText().toString().trim(),
                edtTelefone.getText().toString().trim(),
                edtObjetivo.getText().toString().trim()
        );

        pDAO.Inserir(p, new PacienteDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(DetalhePacienteActivity.this, "Paciente cadastrado!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalhePacienteActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void AtualizarClique(View view) {
        if (!validarCampos()) {
            return;
        }

        paciente.setNome(edtNome.getText().toString().trim());
        paciente.setDataNascimento(edtDataNascimento.getText().toString().trim());
        paciente.setTelefone(edtTelefone.getText().toString().trim());
        paciente.setObjetivoNutricional(edtObjetivo.getText().toString().trim());

        pDAO.Atualizar(paciente, new PacienteDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(DetalhePacienteActivity.this, "Paciente atualizado!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalhePacienteActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void ExcluirClique(View view) {
        pDAO.Excluir(paciente, new PacienteDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(DetalhePacienteActivity.this, "Paciente excluído!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalhePacienteActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void ConsultasClique(View view) {
        Intent intent = new Intent(this, ConsultasActivity.class);
        intent.putExtra("paciente", paciente);
        consultasLauncher.launch(intent);
    }

    public void PlanoAlimentarClique(View view) {
        Intent intent = new Intent(this, PlanoAlimentarActivity.class);
        intent.putExtra("paciente", paciente);
        planoLauncher.launch(intent);
    }
}