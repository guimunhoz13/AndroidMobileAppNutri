package com.nutricionista.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutricionista.app.dao.AlimentoDAO;
import com.nutricionista.app.modelos.Alimento;

import java.util.ArrayList;
import java.util.List;

public class AlimentosActivity extends AppCompatActivity {

    private EditText editNomeAlimento;
    private EditText editCaloriasAlimento;
    private Button btnSalvarAlimento;
    private ListView listAlimentos;

    private AlimentoDAO alimentoDAO;
    private ArrayAdapter<Alimento> adapter;
    private List<Alimento> alimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentos);

        editNomeAlimento = findViewById(R.id.editNomeAlimento);
        editCaloriasAlimento = findViewById(R.id.editCaloriasAlimento);
        btnSalvarAlimento = findViewById(R.id.btnSalvarAlimento);
        listAlimentos = findViewById(R.id.listAlimentos);

        alimentoDAO = new AlimentoDAO();
        alimentos = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                alimentos
        );

        listAlimentos.setAdapter(adapter);

        btnSalvarAlimento.setOnClickListener(v -> salvarAlimento());

        carregarAlimentos();
    }

    private void salvarAlimento() {
        String nome = editNomeAlimento.getText().toString().trim();
        String caloriasTexto = editCaloriasAlimento.getText().toString().trim();

        if (nome.isEmpty()) {
            editNomeAlimento.setError("Informe o nome do alimento");
            return;
        }

        if (caloriasTexto.isEmpty()) {
            editCaloriasAlimento.setError("Informe as calorias");
            return;
        }

        double calorias;

        try {
            calorias = Double.parseDouble(caloriasTexto);
        } catch (NumberFormatException e) {
            editCaloriasAlimento.setError("Calorias inválidas");
            return;
        }

        Alimento alimento = new Alimento(nome, calorias);

        alimentoDAO.Inserir(alimento, new AlimentoDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(AlimentosActivity.this, "Alimento salvo com sucesso", Toast.LENGTH_SHORT).show();

                editNomeAlimento.setText("");
                editCaloriasAlimento.setText("");

                carregarAlimentos();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(AlimentosActivity.this, "Erro: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarAlimentos() {
        alimentoDAO.ListarTodos(new AlimentoDAO.CallbackLista() {
            @Override
            public void onSucesso(List<Alimento> lista) {
                alimentos.clear();
                alimentos.addAll(lista);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(AlimentosActivity.this, "Erro: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }
}