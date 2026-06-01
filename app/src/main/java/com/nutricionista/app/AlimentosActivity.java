package com.nutricionista.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nutricionista.app.dao.AlimentoDAO;
import com.nutricionista.app.modelos.Alimento;

import java.util.ArrayList;
import java.util.List;

public class AlimentosActivity extends AppCompatActivity {

    private EditText editNomeAlimento;
    private Button btnSalvarAlimento;
    private Button btnAtualizarAlimento;
    private Button btnExcluirAlimento;
    private ListView listAlimentos;

    private AlimentoDAO alimentoDAO;
    private ArrayAdapter<Alimento> adapter;
    private List<Alimento> alimentos;

    private Alimento alimentoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentos);

        editNomeAlimento = findViewById(R.id.editNomeAlimento);
        btnSalvarAlimento = findViewById(R.id.btnSalvarAlimento);
        btnAtualizarAlimento = findViewById(R.id.btnAtualizarAlimento);
        btnExcluirAlimento = findViewById(R.id.btnExcluirAlimento);
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
        btnAtualizarAlimento.setOnClickListener(v -> atualizarAlimento());
        btnExcluirAlimento.setOnClickListener(v -> confirmarExclusao());

        listAlimentos.setOnItemClickListener((parent, view, position, id) -> {
            alimentoSelecionado = alimentos.get(position);
            editNomeAlimento.setText(alimentoSelecionado.getNome());
            Toast.makeText(this, "Alimento selecionado para edição", Toast.LENGTH_SHORT).show();
        });

        carregarAlimentos();
    }

    private void salvarAlimento() {
        String nome = editNomeAlimento.getText().toString().trim();

        if (nome.isEmpty()) {
            editNomeAlimento.setError("Informe o nome do alimento");
            editNomeAlimento.requestFocus();
            return;
        }

        Alimento alimento = new Alimento(nome);

        alimentoDAO.Inserir(alimento, new AlimentoDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(AlimentosActivity.this, "Alimento salvo com sucesso", Toast.LENGTH_SHORT).show();
                limparCampos();
                carregarAlimentos();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(AlimentosActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atualizarAlimento() {
        if (alimentoSelecionado == null) {
            Toast.makeText(this, "Selecione um alimento para atualizar", Toast.LENGTH_SHORT).show();
            return;
        }

        String nome = editNomeAlimento.getText().toString().trim();

        if (nome.isEmpty()) {
            editNomeAlimento.setError("Informe o nome do alimento");
            editNomeAlimento.requestFocus();
            return;
        }

        alimentoSelecionado.setNome(nome);

        alimentoDAO.Alterar(alimentoSelecionado, new AlimentoDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(AlimentosActivity.this, "Alimento atualizado com sucesso", Toast.LENGTH_SHORT).show();
                limparCampos();
                carregarAlimentos();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(AlimentosActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmarExclusao() {
        if (alimentoSelecionado == null) {
            Toast.makeText(this, "Selecione um alimento para excluir", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Excluir alimento")
                .setMessage("Deseja realmente excluir este alimento?")
                .setPositiveButton("Excluir", (dialog, which) -> excluirAlimento())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirAlimento() {
        alimentoDAO.Excluir(alimentoSelecionado, new AlimentoDAO.Callback() {
            @Override
            public void onSucesso() {
                Toast.makeText(AlimentosActivity.this, "Alimento excluído com sucesso", Toast.LENGTH_SHORT).show();
                limparCampos();
                carregarAlimentos();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(AlimentosActivity.this, mensagem, Toast.LENGTH_LONG).show();
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
                Toast.makeText(AlimentosActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limparCampos() {
        editNomeAlimento.setText("");
        alimentoSelecionado = null;
    }
}