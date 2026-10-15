package estatisticas;

import io.LeitorCSV;
import modelo.Estudante;

public class EstatisticasEstudantes {

    public static void mostrar(Estudante[] todos, LeitorCSV leitor) {
        int total = todos.length;

        System.out.println("\n========== ESTATISTICAS DOS ESTUDANTES ==========");

        // ---- gerais ----
        System.out.println("\n--- Gerais ---");
        System.out.println("Total de linhas lidas   : " + leitor.getTotalLinhas());
        System.out.println("Registos validos        : " + leitor.getValidos());
        System.out.println("Registos invalidos      : " + leitor.getInvalidos());
        System.out.println("Duplicados rejeitados   : " + leitor.getDuplicados());
        System.out.println("Estudantes inseridos    : " + leitor.getInseridos());

        if (total == 0) {
            System.out.println("\nNao ha estudantes na tabela.");
            return;
        }

        // ---- sexo ----
        int masculino = 0;
        int feminino = 0;
        for (int i = 0; i < total; i++) {
            if (todos[i].getSexo().equals("M")) {
                masculino++;
            } else {
                feminino++;
            }
        }
        System.out.println("\n--- Por sexo ---");
        System.out.println("Masculino : " + masculino + " (" + percentagem(masculino, total) + ")");
        System.out.println("Feminino  : " + feminino + " (" + percentagem(feminino, total) + ")");

        // ---- curso ----
        String[] nomesCurso = new String[total];
        int[] qtdCurso = new int[total];
        double[] somaCurso = new double[total];
        int numCursos = 0;

        for (int i = 0; i < total; i++) {
            int pos = -1;
            for (int j = 0; j < numCursos; j++) {
                if (nomesCurso[j].equals(todos[i].getCurso())) {
                    pos = j;
                    break;
                }
            }
            if (pos == -1) {
                pos = numCursos;
                nomesCurso[pos] = todos[i].getCurso();
                numCursos++;
            }
            qtdCurso[pos]++;
            somaCurso[pos] += todos[i].getMedia();
        }

        System.out.println("\n--- Por curso ---");
        for (int j = 0; j < numCursos; j++) {
            System.out.println(nomesCurso[j]);
            System.out.println("--------------------------------");
            System.out.println("Estudantes  : " + qtdCurso[j]);
            System.out.println("Percentagem : " + percentagem(qtdCurso[j], total));
            System.out.println("Media       : " + String.format("%.1f", somaCurso[j] / qtdCurso[j]));
            System.out.println();
        }

        // ---- ano ----
        int[] porAno = new int[5];
        for (int i = 0; i < total; i++) {
            porAno[todos[i].getAno()]++;
        }
        System.out.println("--- Por ano ---");
        for (int a = 1; a <= 4; a++) {
            System.out.println(a + " ano : " + porAno[a]);
        }

        // ---- medias ----
        double soma = 0;
        Estudante melhor = todos[0];
        Estudante pior = todos[0];
        int abaixo10 = 0;
        int de10a13 = 0;
        int de14a16 = 0;
        int acima17 = 0;

        for (int i = 0; i < total; i++) {
            double m = todos[i].getMedia();
            soma += m;
            if (m > melhor.getMedia()) {
                melhor = todos[i];
            }
            if (m < pior.getMedia()) {
                pior = todos[i];
            }
            if (m < 10) {
                abaixo10++;
            } else if (m < 14) {
                de10a13++;
            } else if (m < 17) {
                de14a16++;
            } else {
                acima17++;
            }
        }

        System.out.println("\n--- Medias ---");
        System.out.println("Media geral            : " + String.format("%.2f", soma / total));
        System.out.println("Maior media            : " + melhor.getMedia() + " (" + melhor.getNome() + ", " + melhor.getCodigo() + ")");
        System.out.println("Menor media            : " + pior.getMedia() + " (" + pior.getNome() + ", " + pior.getCodigo() + ")");
        System.out.println("Media < 10             : " + abaixo10);
        System.out.println("Media 10 a 13.9        : " + de10a13);
        System.out.println("Media 14 a 16.9        : " + de14a16);
        System.out.println("Media >= 17            : " + acima17);

        // ---- idade ----
        double somaIdade = 0;
        int menor = todos[0].getIdade();
        int maior = todos[0].getIdade();
        int i1 = 0;   // < 18
        int i2 = 0;   // 18-20
        int i3 = 0;   // 21-23
        int i4 = 0;   // 24-26
        int i5 = 0;   // > 26

        for (int i = 0; i < total; i++) {
            int idade = todos[i].getIdade();
            somaIdade += idade;
            if (idade < menor) {
                menor = idade;
            }
            if (idade > maior) {
                maior = idade;
            }
            if (idade < 18) {
                i1++;
            } else if (idade <= 20) {
                i2++;
            } else if (idade <= 23) {
                i3++;
            } else if (idade <= 26) {
                i4++;
            } else {
                i5++;
            }
        }

        System.out.println("\n--- Idades ---");
        System.out.println("Idade media  : " + String.format("%.1f", somaIdade / total));
        System.out.println("Menor idade  : " + menor);
        System.out.println("Maior idade  : " + maior);
        System.out.println("Menos de 18  : " + i1);
        System.out.println("18 a 20      : " + i2);
        System.out.println("21 a 23      : " + i3);
        System.out.println("24 a 26      : " + i4);
        System.out.println("Mais de 26   : " + i5);
    }

    private static String percentagem(int parte, int total) {
        return String.format("%.1f", 100.0 * parte / total) + "%";
    }
}
