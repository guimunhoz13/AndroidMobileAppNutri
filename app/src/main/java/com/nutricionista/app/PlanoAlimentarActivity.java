package com.nutricionista.app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutricionista.app.dao.PlanoAlimentarDAO;
import com.nutricionista.app.modelos.Paciente;
import com.nutricionista.app.modelos.PlanoAlimentar;

public class PlanoAlimentarActivity extends AppCompatActivity {

    private TextView txtTituloPlano;
    private EditText edtCafeManha, edtLancheManha, edtAlmoco, edtLancheTarde, edtJantar, edtCeia, edtObservacoes;

    private Paciente paciente;
    private PlanoAlimentar plano;
    private PlanoAlimentarDAO planoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plano_alimentar);

        txtTituloPlano = findViewById(R.id.txtTituloPlano);
        edtCafeManha = findViewById(R.id.edtCafeManha);
        edtLancheManha = findViewById(R.id.edtLancheManha);
        edtAlmoco = findViewById(R.id.edtAlmoco);
        edtLancheTarde = findViewById(R.id.edtLancheTarde);
        edtJantar = findViewById(R.id.edtJantar);
        edtCeia = findViewById(R.id.edtCeia);
        edtObservacoes = findViewById(R.id.edtObservacoesPlano);

        paciente = (Paciente) getIntent().getSerializableExtra("paciente");

        if (paciente == null || paciente.getId() == null) {
            Toast.makeText(this, "Paciente não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        planoDAO = new PlanoAlimentarDAO();

        txtTituloPlano.setText("Plano alimentar de " + paciente.getNome());

        carregarPlano();
    }

    private void carregarPlano() {
        planoDAO.BuscarPorPaciente(paciente.getId(), new PlanoAlimentarDAO.CallbackPlano() {
            @Override
            public void onSucesso(PlanoAlimentar resultado) {
                plano = resultado;
                preencherCampos();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(PlanoAlimentarActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void preencherCampos() {
        edtCafeManha.setText(plano.getCafeManha() == null ? "" : plano.getCafeManha());
        edtLancheManha.setText(plano.getLancheManha() == null ? "" : plano.getLancheManha());
        edtAlmoco.setText(plano.getAlmoco() == null ? "" : plano.getAlmoco());
        edtLancheTarde.setText(plano.getLancheTarde() == null ? "" : plano.getLancheTarde());
        edtJantar.setText(plano.getJantar() == null ? "" : plano.getJantar());
        edtCeia.setText(plano.getCeia() == null ? "" : plano.getCeia());
        edtObservacoes.setText(plano.getObservacoes() == null ? "" : plano.getObservacoes());
    }

    public void SalvarPlanoClique(View view) {
        if (plano == null) {
            plano = new PlanoAlimentar();
            plano.setIdPaciente(paciente.getId());
        }

        plano.setCafeManha(edtCafeManha.getText().toString().trim());
        plano.setLancheManha(edtLancheManha.getText().toString().trim());
        plano.setAlmoco(edtAlmoco.getText().toString().trim());
        plano.setLancheTarde(edtLancheTarde.getText().toString().trim());
        plano.setJantar(edtJantar.getText().toString().trim());
        plano.setCeia(edtCeia.getText().toString().trim());
        plano.setObservacoes(edtObservacoes.getText().toString().trim());

        planoDAO.Salvar(plano, new PlanoAlimentarDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(PlanoAlimentarActivity.this, "Plano alimentar salvo!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(PlanoAlimentarActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }
}