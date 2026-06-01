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
import com.nutricionista.app.adapter.ConsultaAdapter;
import com.nutricionista.app.dao.ConsultaDAO;
import com.nutricionista.app.modelos.Consulta;
import com.nutricionista.app.modelos.Paciente;
import java.util.ArrayList;
import java.util.List;

public class ConsultasActivity extends AppCompatActivity {

    ListView lsvConsultas;
    List<Consulta> lista = new ArrayList<>();
    ConsultaDAO cDAO;
    Paciente paciente;
    ConsultaAdapter adapter;
    ActivityResultLauncher<Intent> detalheLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);

        lsvConsultas = (ListView) findViewById(R.id.lsvConsultas);
        TextView txtTitulo = (TextView) findViewById(R.id.txtTituloPaciente);

        paciente = (Paciente) getIntent().getSerializableExtra("paciente");
        txtTitulo.setText("Consultas de: " + paciente.getNome());

        cDAO = new ConsultaDAO();
        adapter = new ConsultaAdapter(this, R.layout.consulta_item, lista);
        lsvConsultas.setAdapter(adapter);

        Listar();

        detalheLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == RESULT_OK) Listar(); }
        );

        lsvConsultas.setOnItemClickListener((list, view, posicao, id) -> {
            Intent intent = new Intent(this, DetalheConsultaActivity.class);
            intent.putExtra("consulta", lista.get(posicao));
            intent.putExtra("paciente", paciente);
            detalheLauncher.launch(intent);
        });
    }

    private void Listar() {
        cDAO.ListarPorPaciente(paciente.getId(), new ConsultaDAO.CallbackLista() {
            @Override
            public void onSucesso(List<Consulta> resultado) {
                lista.clear();
                lista.addAll(resultado);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onErro(String mensagem) {
                Toast.makeText(ConsultasActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void NovaConsultaClique(View view) {
        Intent intent = new Intent(this, DetalheConsultaActivity.class);
        Consulta nova = new Consulta();
        nova.setIdPaciente(paciente.getId());
        intent.putExtra("consulta", nova);
        intent.putExtra("paciente", paciente);
        detalheLauncher.launch(intent);
    }
}
