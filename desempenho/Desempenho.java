package desempenho;

import hash.TabelaHash;
import modelo.Estudante;

import java.util.ArrayList;

public class Desempenho {

    private static final int RONDAS = 10;

    /** Experiencia com 1000, 5000 e 10000 registos. */
    public static void testeDesempenho(ArrayList<Estudante> lista) {
        int[] tamanhos = {1000, 5000, 10000};

        // execucao previa (aquecimento da JVM), sem mostrar resultados
        for (int t = 0; t < tamanhos.length; t++) {
            if (tamanhos[t] <= lista.size()) {
                executar(lista, tamanhos[t], false);
            }
        }

        System.out.println("\n========== TESTE DE DESEMPENHO ==========");
        System.out.printf("%-9s %-9s %-12s %-11s %-8s %-15s %-18s%n",
                "Registos", "Colisoes", "Media sond.", "Max. sond.", "Carga",
                "Insercao (ms)", "Pesquisa (ns/pesq)");

        for (int t = 0; t < tamanhos.length; t++) {
            if (tamanhos[t] > lista.size()) {
                System.out.println(tamanhos[t] + " registos: ficheiro tem apenas " + lista.size());
            } else {
                executar(lista, tamanhos[t], true);
            }
        }
    }

    private static void executar(ArrayList<Estudante> lista, int n, boolean mostrar) {
        TabelaHash tabela = new TabelaHash(TabelaHash.CAPACIDADE_INICIAL);

        long inicio = System.nanoTime();
        for (int i = 0; i < n; i++) {
            tabela.inserir(lista.get(i));
        }
        long tempoInsercao = System.nanoTime() - inicio;

        // pesquisa de todos os codigos, repetida em varias rondas
        int rondas = Math.max(1, 200000 / n);
        inicio = System.nanoTime();
        for (int r = 0; r < rondas; r++) {
            for (int i = 0; i < n; i++) {
                tabela.pesquisar(lista.get(i).getCodigo());
            }
        }
        long tempoPesquisa = System.nanoTime() - inicio;

        if (mostrar) {
            System.out.printf("%-9d %-9d %-12.2f %-11d %-8.4f %-15.2f %-18.1f%n",
                    n, tabela.getColisoes(), tabela.mediaSondagens(),
                    tabela.getMaiorSondagem(), tabela.fatorCarga(),
                    tempoInsercao / 1000000.0, (double) tempoPesquisa / ((long) n * rondas));
        }
    }

    // ------------------------------------------------------------

    /** Pesquisa sequencial (percorre a lista) */
    private static Estudante pesquisarSequencial(ArrayList<Estudante> lista, String codigo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCodigo().equals(codigo)) {
                return lista.get(i);
            }
        }
        return null;
    }

    /** Compara a pesquisa sequencial com a pesquisa na Tabela Hash. */
    public static void compararPesquisas(TabelaHash tabela, ArrayList<Estudante> lista) {
        int total = lista.size();
        int numPesquisas = 1000;

        // codigos existentes (espalhados por todo o ficheiro)
        String[] existentes = new String[numPesquisas];
        for (int i = 0; i < numPesquisas; i++) {
            existentes[i] = lista.get((int) ((long) i * total / numPesquisas)).getCodigo();
        }

        // codigos inexistentes
        String[] inexistentes = new String[numPesquisas];
        for (int i = 0; i < numPesquisas; i++) {
            inexistentes[i] = String.valueOf(i + 1);
        }

        // aquecimento
        medirHash(tabela, existentes);
        medirSequencial(lista, existentes);

        double hashExistentes = medirHash(tabela, existentes);
        double seqExistentes = medirSequencial(lista, existentes);
        double hashInexistentes = medirHash(tabela, inexistentes);
        double seqInexistentes = medirSequencial(lista, inexistentes);

        System.out.println("\n========== PESQUISA SEQUENCIAL vs HASH ==========");
        System.out.println("Registos: " + total + " | pesquisas por teste: " + numPesquisas + " x " + RONDAS + " rondas");
        System.out.printf("%-22s %-18s %-18s %-10s%n",
                "Tipo de codigo", "Sequencial (ns)", "Hash (ns)", "Vezes mais rapida");
        System.out.printf("%-22s %-18.1f %-18.1f %-10.1f%n",
                "Existentes", seqExistentes, hashExistentes, seqExistentes / hashExistentes);
        System.out.printf("%-22s %-18.1f %-18.1f %-10.1f%n",
                "Inexistentes", seqInexistentes, hashInexistentes, seqInexistentes / hashInexistentes);
    }

    /** Tempo medio (ns) por pesquisa na Hash. */
    private static double medirHash(TabelaHash tabela, String[] codigos) {
        long inicio = System.nanoTime();
        for (int r = 0; r < RONDAS; r++) {
            for (int i = 0; i < codigos.length; i++) {
                tabela.pesquisar(codigos[i]);
            }
        }
        long fim = System.nanoTime();
        return (double) (fim - inicio) / ((long) codigos.length * RONDAS);
    }

    /** Tempo medio (ns) por pesquisa sequencial. */
    private static double medirSequencial(ArrayList<Estudante> lista, String[] codigos) {
        long inicio = System.nanoTime();
        for (int r = 0; r < RONDAS; r++) {
            for (int i = 0; i < codigos.length; i++) {
                pesquisarSequencial(lista, codigos[i]);
            }
        }
        long fim = System.nanoTime();
        return (double) (fim - inicio) / ((long) codigos.length * RONDAS);
    }
}
