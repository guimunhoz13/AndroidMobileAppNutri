package com.nutricionista.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nutricionista.app.R;
import com.nutricionista.app.modelos.Consulta;
import java.util.List;
import java.util.Locale;

public class ConsultaAdapter extends ArrayAdapter<Consulta> {
    private LayoutInflater mInflater;

    public ConsultaAdapter(Context context, int resource, List<Consulta> dados) {
        super(context, resource, dados);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int posicao, View linha, ViewGroup parent) {
        ViewHolder holder;
        if (linha == null) {
            linha = mInflater.inflate(R.layout.consulta_item, null);
            holder = new ViewHolder();
            holder.data = linha.findViewById(R.id.txtConsultaData);
            holder.pesoAltura = linha.findViewById(R.id.txtConsultaPesoAltura);
            holder.imc = linha.findViewById(R.id.txtConsultaIMC);
            linha.setTag(holder);
        } else {
            holder = (ViewHolder) linha.getTag();
        }
        Consulta c = getItem(posicao);
        holder.data.setText(c.getDataConsulta());
        holder.pesoAltura.setText(String.format(Locale.getDefault(), "Peso: %.1f kg  |  Altura: %.2f m", c.getPeso(), c.getAltura()));
        holder.imc.setText(String.format(Locale.getDefault(), "IMC: %.1f", c.calcularIMC()));
        return linha;
    }

    static class ViewHolder {
        public TextView data, pesoAltura, imc;
    }
}
