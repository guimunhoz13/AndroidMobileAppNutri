package com.nutricionista.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nutricionista.app.R;
import com.nutricionista.app.modelos.Paciente;
import java.util.List;

public class PacienteAdapter extends ArrayAdapter<Paciente> {

    private final LayoutInflater inflater;

    public PacienteAdapter(Context context, int resource, List<Paciente> dados) {
        super(context, resource, dados);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int posicao, View linha, ViewGroup parent) {
        ViewHolder holder;

        if (linha == null) {
            linha = inflater.inflate(R.layout.paciente_item, parent, false);

            holder = new ViewHolder();
            holder.nome = linha.findViewById(R.id.txtPacienteNome);
            holder.telefone = linha.findViewById(R.id.txtPacienteTelefone);
            holder.objetivo = linha.findViewById(R.id.txtPacienteObjetivo);

            linha.setTag(holder);
        } else {
            holder = (ViewHolder) linha.getTag();
        }

        Paciente p = getItem(posicao);

        if (p != null) {
            String nome = p.getNome();
            String telefone = p.getTelefone();
            String objetivo = p.getObjetivoNutricional();

            holder.nome.setText(nome == null || nome.trim().isEmpty() ? "Paciente sem nome" : nome);
            holder.telefone.setText(telefone == null || telefone.trim().isEmpty() ? "Telefone não informado" : telefone);
            holder.objetivo.setText(objetivo == null || objetivo.trim().isEmpty() ? "Objetivo não informado" : objetivo);
        }

        return linha;
    }

    static class ViewHolder {
        TextView nome;
        TextView telefone;
        TextView objetivo;
    }
}