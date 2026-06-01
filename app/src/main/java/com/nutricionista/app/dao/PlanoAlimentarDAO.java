package com.nutricionista.app.dao;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nutricionista.app.modelos.PlanoAlimentar;

import java.util.HashMap;
import java.util.Map;

public class PlanoAlimentarDAO {

    private static final String COLECAO = "planos_alimentares";

    private final FirebaseFirestore db;

    public PlanoAlimentarDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public interface Callback {
        void onSucesso();
        void onErro(String mensagem);
    }

    public interface CallbackPlano {
        void onSucesso(PlanoAlimentar plano);
        void onErro(String mensagem);
    }

    public void BuscarPorPaciente(String idPaciente, CallbackPlano callback) {
        db.collection(COLECAO)
                .whereEqualTo("idPaciente", idPaciente)
                .limit(1)
                .get()
                .addOnSuccessListener(documentos -> {
                    if (documentos.isEmpty()) {
                        PlanoAlimentar plano = new PlanoAlimentar();
                        plano.setIdPaciente(idPaciente);
                        callback.onSucesso(plano);
                        return;
                    }

                    QueryDocumentSnapshot doc = (QueryDocumentSnapshot) documentos.getDocuments().get(0);

                    PlanoAlimentar plano = new PlanoAlimentar();

                    plano.setId(doc.getId());
                    plano.setIdPaciente(doc.getString("idPaciente"));
                    plano.setCafeManha(doc.getString("cafeManha"));
                    plano.setLancheManha(doc.getString("lancheManha"));
                    plano.setAlmoco(doc.getString("almoco"));
                    plano.setLancheTarde(doc.getString("lancheTarde"));
                    plano.setJantar(doc.getString("jantar"));
                    plano.setCeia(doc.getString("ceia"));
                    plano.setObservacoes(doc.getString("observacoes"));

                    callback.onSucesso(plano);
                })
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Salvar(PlanoAlimentar plano, Callback callback) {
        Map<String, Object> dados = new HashMap<>();

        dados.put("idPaciente", plano.getIdPaciente());
        dados.put("cafeManha", plano.getCafeManha());
        dados.put("lancheManha", plano.getLancheManha());
        dados.put("almoco", plano.getAlmoco());
        dados.put("lancheTarde", plano.getLancheTarde());
        dados.put("jantar", plano.getJantar());
        dados.put("ceia", plano.getCeia());
        dados.put("observacoes", plano.getObservacoes());

        if (plano.getId() == null || plano.getId().isEmpty()) {
            db.collection(COLECAO)
                    .add(dados)
                    .addOnSuccessListener(documentReference -> callback.onSucesso())
                    .addOnFailureListener(e -> callback.onErro(e.getMessage()));
        } else {
            db.collection(COLECAO)
                    .document(plano.getId())
                    .set(dados)
                    .addOnSuccessListener(unused -> callback.onSucesso())
                    .addOnFailureListener(e -> callback.onErro(e.getMessage()));
        }
    }
}