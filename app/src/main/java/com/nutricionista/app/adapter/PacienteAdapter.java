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
    private LayoutInflater mInflater;

    public PacienteAdapter(Context context, int resource, List<Paciente> dados) {
        super(context, resource, dados);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int posicao, View linha, ViewGroup parent) {
        ViewHolder holder;
        if (linha == null) {
            linha = mInflater.inflate(R.layout.paciente_item, null);
            holder = new ViewHolder();
            holder.nome = linha.findViewById(R.id.txtPacienteNome);
            holder.telefone = linha.findViewById(R.id.txtPacienteTelefone);
            holder.objetivo = linha.findViewById(R.id.txtPacienteObjetivo);
            linha.setTag(holder);
        } else {
            holder = (ViewHolder) linha.getTag();
        }
        Paciente p = getItem(posicao);
        holder.nome.setText(p.getNome());
        holder.telefone.setText(p.getTelefone());
        holder.objetivo.setText(p.getObjetivoNutricional());
        return linha;
    }

    static class ViewHolder {
        public TextView nome, telefone, objetivo;
    }
}
