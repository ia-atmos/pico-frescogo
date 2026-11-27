// The oracle of the score of FrescoGO!
//
// The functions of the score of `placar/placar.pde` (v4.1.0, the source of
// the release package), taken out of the sketch into plain Java, with no
// Processing. It reads a script from the standard input and prints the
// numbers that the scoreboard computes: the port must print the same lines
// for the same scripts.
//
// The functions between the marks BEGIN and END were copied from the sketch
// by a script, with `static` as the only change (the branch `4.1` has them
// so). Here they have the changes of the rule after v4.1.0, each marked
// with its item of `.claude/plans/TODO.md`, written apart from the port:
//
//      #5      no `trinca` mode
//      #23     no hits by the keyboard: the speeds are those of the radar;
//              the clock of a sequence starts at its first hit, or at the
//              key of the referee (`crono`)
//      #6      the rule of time: 6 minutes, and each fall takes 12 seconds
//              of the time; no percentage for the falls, and no limit of
//              falls; the 150 strongest hits of each athlete count
//      #24     the version is 4.2.0, and the report no longer says `dupla`
//      #32     no warm-up: it has no score (only the speeds and the time)
//
// `min`, `max`, `nf`, and `IntList` are those of Processing, redefined at
// the end of this file.
//
// See `oracle/README.md` for the format of the scripts.

import java.io.*;
import java.util.*;

public class Oracle {

    static int         MAJOR    = 4;
    static int         MINOR    = 2;        // #24
    static int         REVISION = 0;
    static String      VERSAO   = MAJOR + "." + MINOR + "." + REVISION;

    static int         NOW;

    static boolean     RADAR = false;      // stands for (RADAR!=null || RADAR_MOCK)


    // the defaults are those of `conf.json`
    static int         CONF_TEMPO       = 360;      // #6
    static int         CONF_ATAQUES     = 150;      // #6: of each athlete
    static int         CONF_EQUILIBRIO  = 130;
    static int         CONF_VEL_MIN     = 50;
    static int         CONF_SAQUE       = 45;
    static int         CONF_QUEDA       = 12;       // #6: seconds per fall
    static int         CONF_DESCANSO    = 90;

    static int         LADO_RADAR       = 0;

    static String[]    CONF_NOMES = { "Atleta 1", "Atleta 2", "Árbitro" };
    static String      CONF_PARS;

    static String      ESTADO = "ocioso";
    static String      ESTADO_JOGANDO = "sacando";

    static int         JOGO_DESCANSO_TOTAL;
    static int         JOGO_TOTAL, JOGO_QUEDAS, JOGO_QUEDAS_MANUAL;
    static int         JOGO_TEMPO_INICIO, JOGO_TEMPO_PASSADO, JOGO_TEMPO_RESTANTE, JOGO_TEMPO_RESTANTE_SHOW;
    static int[][]     JOGO_JOGS = new int[2][5];  // pts, golpes, med, min, max

    static ArrayList<ArrayList> JOGO = new ArrayList<ArrayList>();
    static ArrayList<Integer>   CRONO = new ArrayList<Integer>();   // #23: the key of the referee, or -1

    ///////////////////////////////////////////////////////////////////////////
    // BEGIN: copied from `placar.pde`
    ///////////////////////////////////////////////////////////////////////////

    static String ns (String str, int n) {
        int len = str.length();
        for (int i=0; i<n-len; i++) {
            str += " ";
        }
        return str;
    }

    ///////////////////////////////////////////////////////////////////////////////

    static int conf_tempo () {
        return CONF_TEMPO;                                      // #32
    }

    static int conf_ataques (int jog) {
        return CONF_ATAQUES;                                    // #5, #6
    }

    static boolean conf_radar () {
        return RADAR;
    }

    static void _jogo_tempo () {
        int ret = 0;
        int last = NOW;

        {                                                   // #32
            for (int i=0; i<JOGO.size(); i++) {
                ArrayList<int[]> seq = JOGO.get(i);
                int S = seq.size();
                int c = CRONO.get(i);                       // #23
                if (c >= 0) {
                    last = (S > 0) ? seq.get(S-1)[0] : c;
                    ret += last - c;
                } else if (S > 0) {
                    last = seq.get(S-1)[0];
                    if (S >= 2) {
                        ret += (seq.get(S-1)[0] - seq.get(0)[0]);
                    }
                }
            }
            //if (ESTADO.equals("jogando") && ESTADO_JOGANDO.equals("jogando")) {
            //    ArrayList<int[]> seq = JOGO.get(JOGO.size()-1);
            //    ret += millis() - seq.get(seq.size()-1)[0];
            //}
        }

        JOGO_TEMPO_PASSADO = ret / 1000;
        JOGO_TEMPO_RESTANTE = max(0, conf_tempo()-JOGO_TEMPO_PASSADO-jogo_desconto());   // #6
        JOGO_TEMPO_RESTANTE_SHOW = JOGO_TEMPO_RESTANTE;

        if (ESTADO.equals("jogando") && ESTADO_JOGANDO.equals("jogando")) {
            JOGO_TEMPO_RESTANTE_SHOW -= ((NOW - last) / 1000);
        }
    }

    static void _jogo_lado (int jog) {
        IntList kmhs = new IntList();
        for (int i=0; i<JOGO.size(); i++) {
            ArrayList<int[]> seq = JOGO.get(i);
            for (int j=0; j<seq.size()-1; j++) {    // -1: ignora ultimo golpe
                int[] golpe = seq.get(j);
                if (golpe[1] == jog) {
                    int kmh = jogo_kmh(seq,j);
                    if (kmh >= CONF_VEL_MIN) {
                        kmhs.append(kmh);
                    }
                }
            }
        }
        kmhs.sortReverse();
        int N = min(conf_ataques(jog),kmhs.size());
        int sum1 = 0;   // simples
        int sum2 = 0;   // quadrado
        for (int i=0; i<N; i++) {
            int cur = kmhs.get(i);
            sum1 += cur;
            sum2 += cur*cur/50;
        }
        JOGO_JOGS[jog][0] = sum2;
        JOGO_JOGS[jog][1] = kmhs.size();
        JOGO_JOGS[jog][2] = sum1 * 100 / max(1,N);
        JOGO_JOGS[jog][3] = (N == 0) ? 0 : kmhs.get(N-1);
        JOGO_JOGS[jog][4] = (N == 0) ? 0 : kmhs.get(0);
    }

    static int jogo_kmh (ArrayList<int[]> seq, int i) {
        return seq.get(i)[2];                               // #23
    }

    static int jogo_quedas () {
        return JOGO_QUEDAS + JOGO_QUEDAS_MANUAL;
    }

    static int jogo_desconto () {                           // #6: in seconds
        return max(0, jogo_quedas() * CONF_QUEDA);          // #32
    }

    static int[] jogo_equ () {
        int p0 = JOGO_JOGS[0][0];
        int p1 = JOGO_JOGS[1][0];

        int n0 = JOGO_JOGS[0][1];
        int n1 = JOGO_JOGS[1][1];

        if (CONF_EQUILIBRIO!=0 && n0+n1>=30) {
            p0 = min(p0, p1*CONF_EQUILIBRIO/100);
            p1 = min(p1, p0*CONF_EQUILIBRIO/100);
        }

        return new int[] {p0,p1};
    }

    static void jogo_calc () {
        _jogo_tempo();
        _jogo_lado(0);
        _jogo_lado(1);

        int[] ps = jogo_equ();
        JOGO_TOTAL = ps[0] + ps[1];                         // #6
    }

    // `CONF_PARS`, as `setup` builds it.
    static void conf_pars () {
        CONF_PARS = "v" + VERSAO + " / " +
                    (conf_radar() ? "radar" : "sem radar") + " / " +    // #23
                    CONF_TEMPO   + "s";
    }

    // The text of the report, from `go_termino`: what goes to `saveStrings`,
    // with the date given by the script.
    static String report (String ts) {
        String manual = "";
        if (JOGO_QUEDAS_MANUAL != 0) {
            String plus = (JOGO_QUEDAS_MANUAL > 0 ? "+" : "");
            manual = " (" + JOGO_QUEDAS + plus + JOGO_QUEDAS_MANUAL + ")";
        }

        String[] jogs = new String[2];
        for (int i=0; i<2; i++) {
            jogs[i] = ns(CONF_NOMES[i]+":",15) + nf(JOGO_JOGS[i][0],5) + " pontos / " +
                      nf(JOGO_JOGS[i][1],3) + " golpes / " +
                      nf(JOGO_JOGS[i][2]/100,2) + "." + nf(JOGO_JOGS[i][2]%100,2) + " km/h" + "\n";
        }

        int[] ps = jogo_equ();

        String out = ns("Data:",          15) + ts + "\n"
                   + ns("Versão:",        15) + CONF_PARS + "\n"
                   + "\n"
                   + ns("Descanso:",      15) + (JOGO_DESCANSO_TOTAL/1000) + "\n"
                   + ns("Quedas:",        15) + jogo_quedas() + manual + "\n"
                   + ns("Tempo:",         15) + JOGO_TEMPO_PASSADO + "s + " + jogo_desconto() + "s das quedas\n"  // #6
                   + "\n"
                   + jogs[0] + jogs[1]
                   + "\n"
                   + ns("Parcial:",       15) + nf(JOGO_JOGS[0][0]+JOGO_JOGS[1][0],5) + " pontos\n"
                   + ns("Desequilibrio:", 15) + nf((JOGO_JOGS[0][0]+JOGO_JOGS[1][0]) - (ps[0]+ps[1]), 5) + " (-)\n"
                   + "\n"
                   + ns("FINAL:",         15) + nf(JOGO_TOTAL,5) + " pontos\n"
                   + "\n";
        for (int i=0; i<JOGO.size(); i++) {
            ArrayList<int[]> seq = JOGO.get(i);
            out += "SEQUÊNCIA " + nf(i+1,2) + "\n============\n\nTEMPO   DIR   KMH\n-----   ---   ---\n";
            if (CRONO.get(i) >= 0) {                        // #23
                out += nf(CRONO.get(i) - JOGO_TEMPO_INICIO, 6) + "   cronômetro pelo árbitro\n";
            }
            for (int j=0; j<seq.size(); j++) {
                int[] golpe = seq.get(j);
                int ms = golpe[0] - JOGO_TEMPO_INICIO;
                out += nf(ms,6) + "   " + (golpe[1]==0 ? "->" : "<-") + "   " + nf(jogo_kmh(seq,j),3) + "\n";
            }
            out += "\n\n";
        }
        return out;
    }

    ///////////////////////////////////////////////////////////////////////////
    // END: copied from `placar.pde`
    ///////////////////////////////////////////////////////////////////////////

    // Processing

    static int min (int a, int b) { return Math.min(a,b); }
    static int max (int a, int b) { return Math.max(a,b); }

    static String nf (int n, int digits) {
        String s = Integer.toString(Math.abs(n));
        while (s.length() < digits) {
            s = "0" + s;
        }
        return (n < 0 ? "-" : "") + s;
    }

    static class IntList {
        ArrayList<Integer> l = new ArrayList<Integer>();
        int  size   ()      { return l.size(); }
        int  get    (int i) { return l.get(i); }
        void append (int v) { l.add(v); }
        void sortReverse () { Collections.sort(l, Collections.reverseOrder()); }
    }

    // The script

    static void conf (String key, String val) {
        int v = Integer.parseInt(val);
        switch (key) {
            case "tempo":      CONF_TEMPO       = v;   break;
            case "ataques":    CONF_ATAQUES     = v;   break;
            case "equilibrio": CONF_EQUILIBRIO  = v;   break;
            case "minima":     CONF_VEL_MIN     = v;   break;
            case "queda":      CONF_QUEDA       = v;   break;
            case "saque":      CONF_SAQUE       = v;   break;
            case "descanso":   CONF_DESCANSO    = v;   break;
            case "lado_radar": LADO_RADAR       = v-1; break;
            default: throw new RuntimeException("conf : invalid key : " + key);
        }
    }

    static String ints (int[] v) {
        String s = "";
        for (int i=0; i<v.length; i++) {
            s += (i==0 ? "" : ",") + v[i];
        }
        return s;
    }

    static void print_calc () {
        int[] ps = jogo_equ();
        System.out.println("calc"
            + " tempo="  + JOGO_TEMPO_PASSADO + "," + JOGO_TEMPO_RESTANTE + "," + JOGO_TEMPO_RESTANTE_SHOW
            + " total="  + JOGO_TOTAL
            + " equ="    + ps[0] + "," + ps[1]
            + " desconto=" + jogo_desconto()
            + " jog0="   + ints(JOGO_JOGS[0]) + "/" + conf_ataques(0)
            + " jog1="   + ints(JOGO_JOGS[1]) + "/" + conf_ataques(1)
        );
    }

    public static void main (String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        System.setOut(out);
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] w = line.split("\\s+");
            switch (w[0]) {
                case "conf":
                    conf(w[1], w[2]);
                    break;
                case "radar":
                    RADAR = w[1].equals("1");
                    break;
                case "inicio":
                    JOGO_TEMPO_INICIO = Integer.parseInt(w[1]);
                    break;
                case "estado":
                    ESTADO = w[1];
                    if (w.length > 2) {
                        ESTADO_JOGANDO = w[2];
                    }
                    break;
                case "now":
                    NOW = Integer.parseInt(w[1]);
                    break;
                case "nome":
                    // the rest of the line: a name may have spaces
                    CONF_NOMES[Integer.parseInt(w[1])] = line.split("\\s+", 3)[2];
                    break;
                case "seq":
                    JOGO.add(new ArrayList<int[]>());
                    CRONO.add(-1);
                    break;
                case "crono":
                    CRONO.set(CRONO.size()-1, Integer.parseInt(w[1]));
                    break;
                case "hit": {
                    int[] golpe = {
                        Integer.parseInt(w[1]), Integer.parseInt(w[2]), Integer.parseInt(w[3])
                    };
                    ArrayList<int[]> seq = JOGO.get(JOGO.size()-1);
                    seq.add(golpe);
                    break;
                }
                case "quedas":
                    JOGO_QUEDAS = Integer.parseInt(w[1]);
                    break;
                case "manual":
                    JOGO_QUEDAS_MANUAL = Integer.parseInt(w[1]);
                    break;
                case "descanso":
                    JOGO_DESCANSO_TOTAL = Integer.parseInt(w[1]);
                    break;
                case "calc":
                    jogo_calc();
                    print_calc();
                    break;
                case "report":
                    conf_pars();
                    System.out.print(report(w[1]));
                    break;
                default:
                    throw new RuntimeException("invalid command : " + w[0]);
            }
        }
    }
}
