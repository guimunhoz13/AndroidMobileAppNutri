package com.nutricionista.app.dao;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nutricionista.app.modelos.AlimentoConsulta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlimentoConsultaDAO {

    private final FirebaseFirestore db;
    private static final String COLECAO = "alimentos_consulta";

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

    public void Inserir(AlimentoConsulta a, Callback callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("idConsulta", a.getIdConsulta());
        dados.put("nomeAlimento", a.getNomeAlimento());
        dados.put("quantidade", a.getQuantidade());
        dados.put("calorias", a.getCalorias());

        db.collection(COLECAO).add(dados)
                .addOnSuccessListener(ref -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Excluir(AlimentoConsulta a, Callback callback) {
        db.collection(COLECAO).document(a.getId()).delete()
                .addOnSuccessListener(unused -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void ListarPorConsulta(String idConsulta, CallbackLista callback) {
        db.collection(COLECAO).whereEqualTo("idConsulta", idConsulta).get()
                .addOnSuccessListener(documentos -> {
                    List<AlimentoConsulta> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : documentos) {
                        AlimentoConsulta a = new AlimentoConsulta(
                                doc.getString("idConsulta"),
                                doc.getString("nomeAlimento"),
                                doc.getString("quantidade"),
                                doc.getDouble("calorias") != null ? doc.getDouble("calorias") : 0
                        );
                        a.setId(doc.getId());
                        lista.add(a);
                    }
                    callback.onSucesso(lista);
                })
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }
}
