package com.nutricionista.app.dao;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nutricionista.app.modelos.Consulta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsultaDAO {

    private final FirebaseFirestore db;
    private static final String COLECAO = "consultas";

    public ConsultaDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public interface Callback {
        void onSucesso();
        void onErro(String mensagem);
    }

    public interface CallbackLista {
        void onSucesso(List<Consulta> lista);
        void onErro(String mensagem);
    }

    public interface CallbackTotal {
        void onSucesso(int total);
        void onErro(String mensagem);
    }

    public void Inserir(Consulta c, Callback callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("idPaciente", c.getIdPaciente());
        dados.put("dataConsulta", c.getDataConsulta());
        dados.put("peso", c.getPeso());
        dados.put("altura", c.getAltura());
        dados.put("observacoes", c.getObservacoes());

        db.collection(COLECAO).add(dados)
                .addOnSuccessListener(ref -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Atualizar(Consulta c, Callback callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("dataConsulta", c.getDataConsulta());
        dados.put("peso", c.getPeso());
        dados.put("altura", c.getAltura());
        dados.put("observacoes", c.getObservacoes());

        db.collection(COLECAO).document(c.getId()).update(dados)
                .addOnSuccessListener(unused -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Excluir(Consulta c, Callback callback) {
        db.collection("alimentos_consulta").whereEqualTo("idConsulta", c.getId()).get()
                .addOnSuccessListener(alimentos -> {
                    for (QueryDocumentSnapshot alimento : alimentos) {
                        alimento.getReference().delete();
                    }

                    db.collection(COLECAO).document(c.getId()).delete()
                            .addOnSuccessListener(unused -> callback.onSucesso())
                            .addOnFailureListener(e -> callback.onErro(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void ListarPorPaciente(String idPaciente, CallbackLista callback) {
        db.collection(COLECAO).whereEqualTo("idPaciente", idPaciente).get()
                .addOnSuccessListener(documentos -> {
                    List<Consulta> lista = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : documentos) {
                        Consulta c = new Consulta(
                                doc.getString("idPaciente"),
                                doc.getString("dataConsulta"),
                                doc.getDouble("peso") != null ? doc.getDouble("peso") : 0,
                                doc.getDouble("altura") != null ? doc.getDouble("altura") : 0,
                                doc.getString("observacoes")
                        );

                        c.setId(doc.getId());
                        lista.add(c);
                    }

                    callback.onSucesso(lista);
                })
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void ContarTodas(CallbackTotal callback) {
        db.collection(COLECAO).get()
                .addOnSuccessListener(documentos -> callback.onSucesso(documentos.size()))
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }
}