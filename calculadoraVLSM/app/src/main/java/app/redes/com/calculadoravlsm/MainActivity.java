package app.redes.com.calculadoravlsm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TextView tv4;

    private EditText et1, et2, et3;

    private ArrayList<Integer> ip, mascara, redes, hosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("VLSM Salvador Ponce");

        ((Button)findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calcular();

            }

        });

        ((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                agregar();

            }

        });

        tv4 = (TextView)findViewById(R.id.textView4);

        tv4.setMovementMethod(new ScrollingMovementMethod());

        et1 = (EditText)findViewById(R.id.editText1);

        et2 = (EditText)findViewById(R.id.editText2);

        et3 = (EditText)findViewById(R.id.editText3);

        redes = new ArrayList<Integer>();

        hosts = new ArrayList<Integer>();
    }

    public void calcular() {

        if(verificar(et1.getText().toString(), 1) && verificar(et2.getText().toString(), 2)) {

            if(verificarRedes()) {

                tv4.setText("RED ORIGINAL");

                tv4.append("\n\nRed: " + mostrarDir(obtenerDRed(ip, mascara)));

                tv4.append("\nBroadcast: " + mostrarDir(obtenerDDif(ip, mascara)));

                tv4.append("\nMáscara: " + mostrarDir(mascara) + " /" + (32-obtenerCeros(mascara)));

                tv4.append("\n\nSUBREDES\n\n");

                ArrayList<Integer> anterior = null;

                for(int i=0; i<redes.size(); i++) {

                    if(i == 0) {

                        tv4.append("Hosts: " + hosts.get(i));

                        tv4.append("\nRed: " + mostrarDir(obtenerDRed(ip, mascara)));

                        tv4.append("\nBroadcast: " + mostrarDir(obtenerDDif(obtenerDRed(ip, mascara), generarMasc(32-obtenerExp(redes.get(i))))));

                        tv4.append("\nMáscara: " + mostrarDir(generarMasc(32-obtenerExp(redes.get(i)))) + " /" + (32-obtenerCeros(generarMasc(32-obtenerExp(redes.get(i))))));

                        anterior = obtenerDDif(obtenerDRed(ip, mascara), generarMasc(32-obtenerExp(redes.get(i))));

                    } else {

                        tv4.append("Hosts: " + hosts.get(i));

                        tv4.append("\nRed: " + mostrarDir(obtenerSig(anterior)));

                        tv4.append("\nBroadcast: " + mostrarDir(obtenerDDif(obtenerSig(anterior), generarMasc(32-obtenerExp(redes.get(i))))));

                        tv4.append("\nMáscara: " + mostrarDir(generarMasc(32-obtenerExp(redes.get(i)))) + " /" + (32-obtenerCeros(generarMasc(32-obtenerExp(redes.get(i))))));

                        anterior = obtenerDDif(obtenerSig(anterior), generarMasc(32-obtenerExp(redes.get(i))));

                    }

                    tv4.append("\n\n");

                }

            } else {

                tv4.setText("Error en subredes");

            }

            redes = new ArrayList<Integer>();

            hosts = new ArrayList<Integer>();

        } else {

            tv4.setText("Error en IP y/o máscara");

        }

    }

    public void agregar() {

        redes.add(Integer.parseInt(et3.getText().toString()) + 2);

        hosts.add(Integer.parseInt(et3.getText().toString()));

        tv4.setText("Subred agregada");

    }

    private boolean verificar(String c, int x) {

        if(c.isEmpty()) {

            return false;

        }

        for(int i=0; i<c.length(); i++) {

            if(c.charAt(i) != '0' && c.charAt(i) != '1' && c.charAt(i) != '2' && c.charAt(i) != '3'
                    && c.charAt(i) != '4' && c.charAt(i) != '5' && c.charAt(i) != '6' && c.charAt(i) != '7'
                    && c.charAt(i) != '8' && c.charAt(i) != '9' && c.charAt(i) != '.') {

                return false;

            }

        }

        if(x == 1) {

            if(!ipValida(c)) {

                return false;

            }

        } else {

            if(!mascaraValida(c)) {

                return false;

            }

        }


        return true;

    }

    private boolean ipValida(String c) {

        int p = 0;

        for(int i=0; i<c.length(); i++) {

            if(c.charAt(i) == '.') {

                p ++;

            }

        }

        if(p != 3) {

            return false;

        }

        ip = new ArrayList<Integer>();

        String valor = "";

        for(int i=0; i<c.length(); i++) {

            if(c.charAt(i) == '.') {

                if(i == c.length() - 1) {

                    return false;

                }

                if(valor.isEmpty()) {

                    return false;

                }

                int numero = Integer.parseInt(valor);

                if(numero <= 255) {

                    ip.add(numero);

                    valor = "";

                } else {

                    return false;

                }

            } else {

                valor += c.charAt(i);

                if(i == c.length() - 1) {

                    int numero = Integer.parseInt(valor);

                    if(numero <= 255) {

                        ip.add(numero);

                        valor = "";

                    } else {

                        return false;

                    }

                }

            }

        }

        if(ip.get(0) == 0 || ip.get(0) > 223) {

            return false;

        }

        return true;

    }

    private boolean mascaraValida(String c) {

        if(c.contains(".")) {

            int p = 0;

            for (int i = 0; i < c.length(); i++) {

                if (c.charAt(i) == '.') {

                    p++;

                }

            }

            if (p != 3) {

                return false;

            }

            mascara = new ArrayList<Integer>();

            String valor = "";

            for (int i = 0; i < c.length(); i++) {

                if (c.charAt(i) == '.') {

                    if (i == c.length() - 1) {

                        return false;

                    }

                    if (valor.isEmpty()) {

                        return false;

                    }

                    int numero = Integer.parseInt(valor);

                    if (numero == 128 || numero == 192 || numero == 224 || numero == 240 || numero == 248
                            || numero == 252 || numero == 254 || numero == 255 || numero == 0) {

                        mascara.add(numero);

                        valor = "";

                    } else {

                        return false;

                    }

                } else {

                    valor += c.charAt(i);

                    if (i == c.length() - 1) {

                        int numero = Integer.parseInt(valor);

                        if (numero == 128 || numero == 192 || numero == 224 || numero == 240 || numero == 248
                                || numero == 252 || numero == 254 || numero == 255 || numero == 0) {

                            mascara.add(numero);

                            valor = "";

                        } else {

                            return false;

                        }

                    }

                }

            }

            if(mascara.get(0) == 255) {

                int x = 0;

                for (int i = mascara.size() - 1; i >= 0; i--) {

                    if (mascara.get(i) == 255) {

                        if (i == mascara.size() - 1) {

                            return false;

                        }

                        x = i;

                        break;

                    }

                }

                for (int i = 0; i < mascara.size(); i++) {

                    if (i < x) {

                        if (mascara.get(i) != 255) {

                            return false;

                        }

                    } else if (i == x + 1) {

                        if (mascara.get(i) != 0 && mascara.get(i) != 128 && mascara.get(i) != 192 && mascara.get(i) != 224
                                && mascara.get(i) != 240 && mascara.get(i) != 248 && mascara.get(i) != 252 && mascara.get(i) != 254) {

                            return false;

                        }

                    } else if (i > x + 1) {

                        if (mascara.get(i) != 0) {

                            return false;

                        }

                    }

                }

            } else {

                for(int i=1; i<mascara.size(); i++) {

                    if(mascara.get(i) != 0) {

                        return false;

                    }

                }

            }

        } else {

            if(Integer.parseInt(c) == 0 || Integer.parseInt(c) > 30) {

                return false;

            }

            String valor = "";

            for(int i=0; i<Integer.parseInt(c); i++) {

                valor += 1;

            }

            if(valor.length() < 32) {

                int x = 32 - valor.length();

                for(int i=0; i<x; i++) {

                    valor += "0";

                }

            }

            mascara = new ArrayList<Integer>();

            int x = 0;

            for(int i=0; i<4; i++) {

                mascara.add(aDecimal(valor.substring(x, x+8)));

                x += 8;

            }

            String aux = "";

            for(int i=0; i<mascara.size(); i++) {

                aux += mascara.get(i);

                if(i != mascara.size() - 1) {

                    aux += ".";

                }

            }

            et2.setText(aux);

        }

        return true;

    }

    private int aDecimal(String binario) {

        int potencia = 7, decimal = 0;

        for(int i=0; i<8; i++) {

            if(binario.charAt(i) == '1') {

                decimal += Math.pow(2, potencia);

            }

            potencia --;

        }

        return decimal;

    }

    private ArrayList<Integer> obtenerDRed(ArrayList<Integer> ip, ArrayList<Integer> mascara) {

        ArrayList<Integer> dRed = new ArrayList<Integer>();

        for(int i=0; i<4; i++) {

            if(mascara.get(i) == 255) {

                dRed.add(ip.get(i));

            } else {

                dRed.add(realizarAnd(ip.get(i), mascara.get(i)));

            }

        }

        return dRed;

    }

    private ArrayList<Integer> obtenerDDif(ArrayList<Integer> dRed, ArrayList<Integer> mascara) {

        ArrayList<Integer> dDif = new ArrayList<Integer>();

        double direcciones = Math.pow(2, obtenerCeros(mascara));

        int a = dRed.get(0), b = dRed.get(1), c = dRed.get(2), d = dRed.get(3);

        for(double i=0; i<direcciones-1; i++) {

            d ++;

            if(d == 256) {

                d = 0;

                c ++;

            }

            if(c == 256) {

                c = 0;

                b ++;

            }

            if(b == 256) {

                b = 0;

                a ++;

            }

        }


        dDif.add(a);

        dDif.add(b);

        dDif.add(c);

        dDif.add(d);

        return dDif;

    }

    private int realizarAnd(int n1, int n2) {

        String v1 = aBinario(n1);

        String v2 = aBinario(n2);

        String v3 = "";

        for(int i=0; i<8; i++) {

            if(v1.charAt(i) == '1' && v2.charAt(i) == '1') {

                v3 += "1";

            } else {

                v3 += "0";

            }

        }

        return aDecimal(v3);

    }

    private boolean verificarRedes() {

        if(redes.isEmpty()) {

            return false;

        }

        int[] redesFinales = new int[redes.size()];

        for(int i=0; i<redes.size(); i++) {

            int p = 2;

            while(Math.pow(2, p) < redes.get(i)) {

                p ++;

            }

            redesFinales[i] = (int)Math.pow(2, p);

        }

        Arrays.sort(redesFinales);

        redes = new ArrayList<Integer>();

        int j = redesFinales.length - 1;

        for(int i=0; i<redesFinales.length; i++) {

            redes.add(redesFinales[j]);

            j --;

        }

        double total = 0;

        for(int i=0; i<redes.size(); i++) {

            total += redes.get(i);

        }

        if(total > Math.pow(2, obtenerCeros(mascara))) {

            return false;

        }

        int[] aHosts = new int[hosts.size()];

        for(int i=0; i<aHosts.length; i++) {

            aHosts[i] = hosts.get(i);

        }

        Arrays.sort(aHosts);

        j = aHosts.length-1;

        hosts = new ArrayList<Integer>();

        for(int i=0; i<aHosts.length; i++) {

            hosts.add(aHosts[j]);

            j --;

        }

        return true;

    }

    private String aBinario(int decimal) {

        String binario = "", aux = Integer.toBinaryString(decimal);

        if(aux.length() != 8) {

            if(aux.length() == 7) {

                binario = "0" + aux;

            } else if(aux.length() == 6) {

                binario = "00" + aux;

            } else if(aux.length() == 5) {

                binario = "000" + aux;

            } else if(aux.length() == 4) {

                binario = "0000" + aux;

            } else if(aux.length() == 3) {

                binario = "00000" + aux;

            } else if(aux.length() == 2) {

                binario = "000000" + aux;

            } else {

                binario = "0000000" + aux;

            }

        } else {

            binario = aux;

        }

        return binario;

    }

    private int obtenerCeros(ArrayList<Integer> mascara) {

        String aux = "";

        for(int i=0; i<mascara.size(); i++) {

            aux += aBinario(mascara.get(i));

        }

        int ceros = 0;

        for(int i=0; i<aux.length(); i++) {

            if(aux.charAt(i) == '0') {

                ceros ++;

            }

        }

        return ceros;

    }

    private String mostrarDir(ArrayList<Integer> dir) {

        String cadena = "";

        for(int i=0; i<dir.size(); i++) {

            cadena += dir.get(i);

            if(i != dir.size()-1) {

                cadena += ".";

            }

        }

        return cadena;

    }

    private int obtenerExp(int n) {

        int exp = 0;

        while(Math.pow(2, exp) < n) {

            exp ++;

        }

        return exp;

    }

    private ArrayList<Integer> obtenerSig(ArrayList<Integer> anterior) {

        ArrayList<Integer> siguiente = new ArrayList<Integer>();

        int a = anterior.get(0), b = anterior.get(1), c = anterior.get(2), d = anterior.get(3);

        d ++;

        if(d == 256) {

            d = 0;

            c ++;

        }

        if(c == 256) {

            c = 0;

            b ++;

        }

        if(b == 256) {

            b = 0;

            a ++;

        }

        siguiente.add(a);

        siguiente.add(b);

        siguiente.add(c);

        siguiente.add(d);

        return siguiente;

    }

    private ArrayList<Integer> generarMasc(int n) {

        String binario = "";

        for(int i=0; i<n; i++) {

            binario += "1";

        }

        if(binario.length() != 32) {

            int x = binario.length();

            for(int i=0; i<32-x; i++) {

                binario += "0";

            }

        }

        ArrayList<Integer> mascara = new ArrayList<Integer>();

        for(int i=0; i<32; i=i+8) {

            mascara.add(aDecimal(binario.substring(i, i+8)));

        }

        return mascara;

    }


}
