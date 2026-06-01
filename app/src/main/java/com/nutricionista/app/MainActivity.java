package com.nutricionista.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.nutricionista.app.adapter.PacienteAdapter;
import com.nutricionista.app.dao.PacienteDAO;
import com.nutricionista.app.modelos.Paciente;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lsvPacientes;
    List<Paciente> lista = new ArrayList<>();
    PacienteDAO pDAO;
    PacienteAdapter adapter;
    ActivityResultLauncher<Intent> detalheLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lsvPacientes = (ListView) findViewById(R.id.lsvPacientes);
        pDAO = new PacienteDAO();

        adapter = new PacienteAdapter(this, R.layout.paciente_item, lista);
        lsvPacientes.setAdapter(adapter);

        Listar();

        detalheLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> { if (result.getResultCode() == RESULT_OK) Listar(); }
        );

        lsvPacientes.setOnItemClickListener((list, view, posicao, id) -> {
            Intent intent = new Intent(this, DetalhePacienteActivity.class);
            intent.putExtra("paciente", lista.get(posicao));
            detalheLauncher.launch(intent);
        });
    }

    private void Listar() {
        pDAO.ListarTudo(new PacienteDAO.CallbackLista() {
            @Override
            public void onSucesso(List<Paciente> resultado) {
                lista.clear();
                lista.addAll(resultado);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onErro(String mensagem) {
                Toast.makeText(MainActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void NovoClique(View view) {
        Intent intent = new Intent(this, DetalhePacienteActivity.class);
        intent.putExtra("paciente", new Paciente());
        detalheLauncher.launch(intent);
    }
}
