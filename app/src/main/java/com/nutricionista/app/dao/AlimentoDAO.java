package com.nutricionista.app.dao;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nutricionista.app.modelos.Alimento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AlimentoDAO {

    private static final String COLECAO = "alimentos";

    private final FirebaseFirestore db;

    public AlimentoDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public interface Callback {
        void onSucesso();
        void onErro(String mensagem);
    }

    public interface CallbackLista {
        void onSucesso(List<Alimento> lista);
        void onErro(String mensagem);
    }

    public interface CallbackBoolean {
        void onSucesso(boolean existeVinculo);
        void onErro(String mensagem);
    }

    public void Inserir(Alimento alimento, Callback callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("nome", alimento.getNome());

        db.collection(COLECAO)
                .add(dados)
                .addOnSuccessListener(ref -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Alterar(Alimento alimento, Callback callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("nome", alimento.getNome());

        db.collection(COLECAO)
                .document(alimento.getId())
                .set(dados)
                .addOnSuccessListener(unused -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Excluir(Alimento alimento, Callback callback) {
        VerificarVinculo(alimento.getId(), new CallbackBoolean() {
            @Override
            public void onSucesso(boolean existeVinculo) {
                if (existeVinculo) {
                    callback.onErro("Este alimento não pode ser excluído porque está vinculado a uma consulta.");
                    return;
                }

                db.collection(COLECAO)
                        .document(alimento.getId())
                        .delete()
                        .addOnSuccessListener(unused -> callback.onSucesso())
                        .addOnFailureListener(e -> callback.onErro(e.getMessage()));
            }

            @Override
            public void onErro(String mensagem) {
                callback.onErro(mensagem);
            }
        });
    }

    public void VerificarVinculo(String idAlimento, CallbackBoolean callback) {
        db.collection("alimentos_consulta")
                .whereEqualTo("idAlimento", idAlimento)
                .limit(1)
                .get()
                .addOnSuccessListener(documentos -> callback.onSucesso(!documentos.isEmpty()))
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void ListarTodos(CallbackLista callback) {
        db.collection(COLECAO)
                .get()
                .addOnSuccessListener(documentos -> {
                    List<Alimento> lista = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : documentos) {
                        Alimento alimento = new Alimento();
                        alimento.setId(doc.getId());
                        alimento.setNome(doc.getString("nome"));
                        lista.add(alimento);
                    }

                    callback.onSucesso(lista);
                })
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }
}