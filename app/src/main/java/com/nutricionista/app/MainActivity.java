package com.nutricionista.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.nutricionista.app.adapter.PacienteAdapter;
import com.nutricionista.app.dao.ConsultaDAO;
import com.nutricionista.app.dao.PacienteDAO;
import com.nutricionista.app.modelos.Paciente;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lsvPacientes;
    private TextView txtTotalPacientes, txtTotalConsultas, txtMensagemVazia;

    private final List<Paciente> lista = new ArrayList<>();
    private PacienteDAO pDAO;
    private ConsultaDAO cDAO;
    private PacienteAdapter adapter;
    private ActivityResultLauncher<Intent> detalheLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTotalPacientes = findViewById(R.id.txtTotalPacientes);
        txtTotalConsultas = findViewById(R.id.txtTotalConsultas);
        txtMensagemVazia = findViewById(R.id.txtMensagemVazia);
        lsvPacientes = findViewById(R.id.lsvPacientes);

        pDAO = new PacienteDAO();
        cDAO = new ConsultaDAO();

        adapter = new PacienteAdapter(this, R.layout.paciente_item, lista);
        lsvPacientes.setAdapter(adapter);

        detalheLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        carregarDashboard();
                    }
                }
        );

        lsvPacientes.setOnItemClickListener((list, view, posicao, id) -> {
            Intent intent = new Intent(this, DetalhePacienteActivity.class);
            intent.putExtra("paciente", lista.get(posicao));
            detalheLauncher.launch(intent);
        });

        carregarDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDashboard();
    }

    private void carregarDashboard() {
        listarPacientes();
        contarConsultas();
    }

    private void listarPacientes() {
        pDAO.ListarTudo(new PacienteDAO.CallbackLista() {
            @Override
            public void onSucesso(List<Paciente> resultado) {
                lista.clear();
                lista.addAll(resultado);
                adapter.notifyDataSetChanged();

                txtTotalPacientes.setText(String.valueOf(lista.size()));

                if (lista.isEmpty()) {
                    txtMensagemVazia.setVisibility(View.VISIBLE);
                    lsvPacientes.setVisibility(View.GONE);
                } else {
                    txtMensagemVazia.setVisibility(View.GONE);
                    lsvPacientes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(MainActivity.this, "Erro ao carregar pacientes: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void contarConsultas() {
        cDAO.ContarTodas(new ConsultaDAO.CallbackTotal() {
            @Override
            public void onSucesso(int total) {
                txtTotalConsultas.setText(String.valueOf(total));
            }

            @Override
            public void onErro(String mensagem) {
                txtTotalConsultas.setText("0");
            }
        });
    }

    public void NovoClique(View view) {
        Intent intent = new Intent(this, DetalhePacienteActivity.class);
        intent.putExtra("paciente", new Paciente());
        detalheLauncher.launch(intent);
    }

    public void AlimentosClique(View view) {
        try {
            Class<?> classeAlimentos = Class.forName("com.nutricionista.app.AlimentosActivity");
            Intent intent = new Intent(this, classeAlimentos);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(this, "Tela de alimentos não encontrada.", Toast.LENGTH_LONG).show();
        }
    }
}