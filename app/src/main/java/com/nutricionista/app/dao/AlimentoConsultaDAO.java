package com.nutricionista.app.dao;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nutricionista.app.modelos.AlimentoConsulta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlimentoConsultaDAO {

    private static final String COLECAO = "alimentos_consulta";

    private final FirebaseFirestore db;

    public AlimentoConsultaDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public interface Callback {
        void onSucesso();
        void onErro(String mensagem);
    }

    public interface CallbackLista {
        void onSucesso(List<AlimentoConsulta> lista);
        void onErro(String mensagem);
    }

    public void Inserir(AlimentoConsulta alimentoConsulta, Callback callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("idConsulta", alimentoConsulta.getIdConsulta());
        dados.put("idAlimento", alimentoConsulta.getIdAlimento());
        dados.put("quantidade", alimentoConsulta.getQuantidade());

        db.collection(COLECAO)
                .add(dados)
                .addOnSuccessListener(documentReference -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Excluir(AlimentoConsulta alimentoConsulta, Callback callback) {
        db.collection(COLECAO)
                .document(alimentoConsulta.getId())
                .delete()
                .addOnSuccessListener(unused -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void ListarPorConsulta(String idConsulta, CallbackLista callback) {
        db.collection(COLECAO)
                .whereEqualTo("idConsulta", idConsulta)
                .get()
                .addOnSuccessListener(documentos -> {
                    List<AlimentoConsulta> lista = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : documentos) {
                        AlimentoConsulta alimentoConsulta = new AlimentoConsulta();

                        alimentoConsulta.setId(doc.getId());
                        alimentoConsulta.setIdConsulta(doc.getString("idConsulta"));
                        alimentoConsulta.setIdAlimento(doc.getString("idAlimento"));
                        alimentoConsulta.setQuantidade(doc.getString("quantidade"));

                        lista.add(alimentoConsulta);
                    }

                    callback.onSucesso(lista);
                })
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }
}