package com.nutricionista.app.dao;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nutricionista.app.modelos.Paciente;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacienteDAO {

    private final FirebaseFirestore db;
    private static final String COLECAO = "pacientes";

    public PacienteDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public interface Callback {
        void onSucesso();
        void onErro(String mensagem);
    }

    public interface CallbackLista {
        void onSucesso(List<Paciente> lista);
        void onErro(String mensagem);
    }

    public void Inserir(Paciente p, Callback callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("nome", p.getNome());
        dados.put("dataNascimento", p.getDataNascimento());
        dados.put("telefone", p.getTelefone());
        dados.put("objetivoNutricional", p.getObjetivoNutricional());

        db.collection(COLECAO).add(dados)
                .addOnSuccessListener(ref -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Atualizar(Paciente p, Callback callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("nome", p.getNome());
        dados.put("dataNascimento", p.getDataNascimento());
        dados.put("telefone", p.getTelefone());
        dados.put("objetivoNutricional", p.getObjetivoNutricional());

        db.collection(COLECAO).document(p.getId()).update(dados)
                .addOnSuccessListener(unused -> callback.onSucesso())
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void Excluir(Paciente p, Callback callback) {
        // Exclui consultas e alimentos relacionados em cascata
        db.collection("consultas").whereEqualTo("idPaciente", p.getId()).get()
                .addOnSuccessListener(consultas -> {
                    for (QueryDocumentSnapshot consulta : consultas) {
                        String idConsulta = consulta.getId();
                        db.collection("alimentos_consulta")
                                .whereEqualTo("idConsulta", idConsulta).get()
                                .addOnSuccessListener(alimentos -> {
                                    for (QueryDocumentSnapshot alimento : alimentos) {
                                        alimento.getReference().delete();
                                    }
                                });
                        consulta.getReference().delete();
                    }
                    db.collection(COLECAO).document(p.getId()).delete()
                            .addOnSuccessListener(unused -> callback.onSucesso())
                            .addOnFailureListener(e -> callback.onErro(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }

    public void ListarTudo(CallbackLista callback) {
        db.collection(COLECAO).orderBy("nome").get()
                .addOnSuccessListener(documentos -> {
                    List<Paciente> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : documentos) {
                        Paciente p = new Paciente(
                                doc.getString("nome"),
                                doc.getString("dataNascimento"),
                                doc.getString("telefone"),
                                doc.getString("objetivoNutricional")
                        );
                        p.setId(doc.getId());
                        lista.add(p);
                    }
                    callback.onSucesso(lista);
                })
                .addOnFailureListener(e -> callback.onErro(e.getMessage()));
    }
}
