package com.nutricionista.app;

import android.content.Intent;
import android.os.Bundle;
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
    Button btnInserir, btnAtualizar, btnExcluir, btnConsultas;
    Paciente paciente;
    PacienteDAO pDAO;
    ActivityResultLauncher<Intent> consultasLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_paciente);

        edtNome = (EditText) findViewById(R.id.edtNome);
        edtDataNascimento = (EditText) findViewById(R.id.edtDataNascimento);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        edtObjetivo = (EditText) findViewById(R.id.edtObjetivo);
        btnInserir = (Button) findViewById(R.id.btnInserir);
        btnAtualizar = (Button) findViewById(R.id.btnAtualizar);
        btnExcluir = (Button) findViewById(R.id.btnExcluir);
        btnConsultas = (Button) findViewById(R.id.btnConsultas);

        pDAO = new PacienteDAO();
        paciente = (Paciente) getIntent().getSerializableExtra("paciente");

        edtNome.setText(paciente.getNome());
        edtDataNascimento.setText(paciente.getDataNascimento());
        edtTelefone.setText(paciente.getTelefone());
        edtObjetivo.setText(paciente.getObjetivoNutricional());

        boolean hasId = paciente.getId() != null && !paciente.getId().isEmpty();
        btnInserir.setVisibility(hasId ? View.GONE : View.VISIBLE);
        btnAtualizar.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnExcluir.setVisibility(hasId ? View.VISIBLE : View.GONE);
        btnConsultas.setVisibility(hasId ? View.VISIBLE : View.GONE);

        consultasLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {});
    }

    public void InserirClique(View view) {
        Paciente p = new Paciente(
                edtNome.getText().toString(),
                edtDataNascimento.getText().toString(),
                edtTelefone.getText().toString(),
                edtObjetivo.getText().toString());

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
        paciente.setNome(edtNome.getText().toString());
        paciente.setDataNascimento(edtDataNascimento.getText().toString());
        paciente.setTelefone(edtTelefone.getText().toString());
        paciente.setObjetivoNutricional(edtObjetivo.getText().toString());

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
                Toast.makeText(DetalhePacienteActivity.this, "Paciente excluido!", Toast.LENGTH_SHORT).show();
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
}
